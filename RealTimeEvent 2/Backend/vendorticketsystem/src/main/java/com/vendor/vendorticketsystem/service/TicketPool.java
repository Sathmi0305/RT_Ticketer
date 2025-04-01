package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.model.Ticket;
import com.vendor.vendorticketsystem.util.SimLogger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketPool {

    private final Queue<Ticket> tickets = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    private final TicketService ticketService;
    private final VendorManager vendorManager;
    private final SimpMessagingTemplate messagingTemplate;


    public TicketPool(TicketService ticketService, SimpMessagingTemplate messagingTemplate, VendorManager vendorManager) {
        this.ticketService = ticketService;
        this.vendorManager = vendorManager;
        this.messagingTemplate = messagingTemplate;
    }


//  Adds tickets to the pool and database until the pool reaches max capacity.

    public void addTicket() throws InterruptedException {
        lock.lock();
        try {
            while (tickets.size() < vendorManager.getConfig().getMaxTicketCapacity()) {
                Ticket ticket = new Ticket();
                ticket.setTicketCode("Ticket-" + System.currentTimeMillis());
                ticket.setStatus("AVAILABLE");

                // Add ticket to the database
                Ticket savedTicket = ticketService.addTicket(ticket);

                // Add the ticket to the in-memory queue
                tickets.add(savedTicket);
                SimLogger.logThreadStatus("VendorThread", "Added ticket: " + savedTicket.getTicketCode());

                // Notify clients via WebSocket
                messagingTemplate.convertAndSend("/topic/ticketUpdate", tickets.size());
            }
            SimLogger.info("Ticket pool is full. Vendor is waiting...");
            sendVendorUpdate("Ticket pool is full. Vendor is waiting...");
            notEmpty.signalAll(); // Notify waiting customers
            notFull.await();
            // Wait until a ticket is consumed
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a ticket from the pool and updates its status in the database.
     */
    public void removeTicket(int ticketCount) throws InterruptedException {
        lock.lock();
        try {
            int acquiredTickets = 0;

            while (acquiredTickets < ticketCount) {
                while (tickets.isEmpty()) {
                    SimLogger.info("Ticket pool is empty. " + Thread.currentThread().getName() + " is waiting...");
                    notFull.signalAll(); // Notify vendor
                    SimLogger.info("Vendor notified to replenish tickets.");
                    notEmpty.await(); // Wait until tickets are available
                }

                // Remove a ticket and update its status
                Ticket ticket = tickets.poll();
                if (ticket != null) {
                    ticketService.updateTicketStatus(ticket.getId(), "PURCHASED");
                    SimLogger.info(Thread.currentThread().getName() + " purchased ticket: " + ticket.getTicketCode());
                    sendCustomerUpdate(ticket);

                    messagingTemplate.convertAndSend("/topic/ticketUpdate", tickets.size());
                    acquiredTickets++;
                }

                // Notify vendor if pool is empty
                if (tickets.isEmpty()) {
                    SimLogger.info("Ticket pool is empty after removal. Notifying vendor...");
                    notFull.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }



    /**
     * Gets the current size of the ticket pool.
     */
    public int getSize() {
        return tickets.size();
    }

    public void sendCustomerUpdate(Ticket ticket) {
        Map<String, String> message = new HashMap<>();
        message.put("message", "Customer : " + Thread.currentThread().getName()+" purchased ticket: " + ticket.getTicketCode());
        messagingTemplate.convertAndSend("/topic/customerUpdate", message);
    }

    public void sendVendorUpdate(String info) {
        Map<String, String> message = new HashMap<>();
        message.put("message", info);
        messagingTemplate.convertAndSend("/topic/vendorUpdate", message);
    }


}
