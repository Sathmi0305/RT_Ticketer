package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.service.interfaces.ITicketingService;
import com.vendor.vendorticketsystem.model.dto.SimConfigDTO;
import com.vendor.vendorticketsystem.worker.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketingService implements ITicketingService {

    private final Vendor vendor;
    private final CustomerService customerService;
    private final CustomerFactory customerFactory;
    private final TicketPool ticketPool;
    private final VendorManager configService;

    @Autowired
    public TicketingService(Vendor vendor,
                                CustomerService customerService,
                                CustomerFactory customerFactory,
                                TicketPool ticketPool,
                                VendorManager configService) {
        this.vendor = vendor;
        this.customerService = customerService;
        this.customerFactory = customerFactory;
        this.ticketPool = ticketPool;
        this.configService = configService;
    }

    @Override
    public String addCustomer(String name, boolean isVip, int ticketCount) {
        customerService.addCustomer(name, isVip, ticketCount);
        customerFactory.createCustomer(name, isVip, ticketPool, ticketCount, configService);
        return "Customer " + name + " added with request to buy " + ticketCount + " tickets. VIP: " + isVip;
    }

    @Override
    public String startVendor() {
        vendor.start();
        if (!customerFactory.hasCustomers()) {
            return "Vendor started. No customers available yet.";
        }
        customerFactory.startAllCustomers();
        return "Vendor and customer threads started.";
    }

    @Override
    public String stopVendor() {
        vendor.stop();
        customerFactory.stopAllCustomers();
        return "Vendor and customer threads stopped.";
    }

    @Override
    public String startCustomers() {
        customerFactory.startAllCustomers();
        return "All customer threads started.";
    }

    @Override
    public SimConfigDTO getConfig() {
        return configService.getConfig();
    }

    @Override
    public String updateConfig(SimConfigDTO config) {
        configService.updateConfig(config);
        return "Configuration updated successfully.";
    }

    @Override
    public String getStatus() {
        return "Current ticket pool size: " + configService.getConfig().getMaxTicketCapacity();
    }
    @Override
    public int getPoolStatus() {
        return ticketPool.getSize();
    }
}
