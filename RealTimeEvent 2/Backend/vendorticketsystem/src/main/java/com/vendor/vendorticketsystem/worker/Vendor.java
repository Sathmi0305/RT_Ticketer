package com.vendor.vendorticketsystem.worker;

import com.vendor.vendorticketsystem.model.Ticket;
import com.vendor.vendorticketsystem.service.TicketPool;
import com.vendor.vendorticketsystem.service.VendorManager;
import com.vendor.vendorticketsystem.util.SimLogger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private volatile boolean running = false; // Controlled flag for execution
    private Thread thread;
    private VendorManager vendorManager;
    private SimpMessagingTemplate messagingTemplate;


    public Vendor(TicketPool ticketPool, VendorManager vendorManager,SimpMessagingTemplate messagingTemplate) {
        this.ticketPool = ticketPool;
        this.vendorManager = vendorManager;
        this.messagingTemplate = messagingTemplate;

    }

    @Override
    public void run() {
        running = true;
        try {
            int totalTicketsToSupply = vendorManager.getConfig().getTotalTickets();
            int maxTicketCapacity = vendorManager.getConfig().getMaxTicketCapacity();
            SimLogger.info("Total Tickets" + totalTicketsToSupply);
            int ticketsSupplied = 0;

            while (running) {
                if (ticketsSupplied >= totalTicketsToSupply) {
                    SimLogger.info("All tickets have been supplied. Stopping vendor.");
                    break;
                }
                ticketsSupplied += maxTicketCapacity;

                messagingTemplate.convertAndSend("/topic/vendorUpdate", "Vendor released ticket " + ticketsSupplied);
                ticketPool.addTicket();

                Thread.sleep(1000 / vendorManager.getConfig().getTicketReleaseRate()); // Simulate ticket release rate
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SimLogger.error("Vendor interrupted.");
        } finally {
            running = false;
            SimLogger.info("Vendor thread stopped.");
            stop();
        }
    }


    public void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this, "VendorThread");
            thread.start();
            sendVendorUpdate("System Started.");
        }
    }

    public void stop() {
        running = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            sendVendorUpdate("System Stopped.");
            SimLogger.info("Vendor thread interrupted for termination.");
        }
    }

    public void sendVendorUpdate(String info) {
        Map<String, String> message = new HashMap<>();
        message.put("message", info);
        messagingTemplate.convertAndSend("/topic/vendorUpdate", message);
    }


}
