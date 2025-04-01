package com.vendor.vendorticketsystem.worker;

import com.vendor.vendorticketsystem.model.Ticket;
import com.vendor.vendorticketsystem.service.TicketPool;
import com.vendor.vendorticketsystem.service.VendorManager;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final boolean isVip; // Indicates VIP status
    private final String name;
    private final int ticketCount;
    private Thread thread;
    private final VendorManager vendorManager;
    private volatile boolean running = false;

    public Customer(TicketPool ticketPool, String name, boolean isVip, int ticketCount, VendorManager vendorManager) {
        this.ticketPool = ticketPool;
        this.ticketCount = ticketCount;
        this.isVip = isVip;
        this.name = name;
        this.thread = new Thread(this, name);
        this.vendorManager = vendorManager ;
        if (isVip) {
            this.thread.setPriority(Thread.MAX_PRIORITY);
        } else {
            this.thread.setPriority(Thread.NORM_PRIORITY);
        }
    }

    @Override
    public void run() {
        running = true;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ticketPool.removeTicket(ticketCount);

                Thread.sleep(isVip ? 1000/(2* vendorManager.getConfig().getCustomerRetrievalRate()) : 1000/vendorManager.getConfig().getCustomerRetrievalRate()); // VIP customers have shorter wait intervals
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(Thread.currentThread().getName() + " interrupted.");
            }
        }
        System.out.println(Thread.currentThread().getName() + " has stopped.");
    }

    public void start() {
        if (!running) {
            thread.start();
        }
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public void restart() {
        if (!running) {
            thread = new Thread(this, name);  // Recreate the thread
            start();
        }
    }
}

