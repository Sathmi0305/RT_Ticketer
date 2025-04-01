package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.model.dto.SimConfigDTO;
import org.springframework.stereotype.Service;

@Service
public class VendorManager {

    private final SimConfigDTO config;

    public VendorManager() {
        // Default configuration
        this.config = new SimConfigDTO();
        config.setTotalTickets(100);
        config.setTicketReleaseRate(1); // 1 ticket per second
        config.setCustomerRetrievalRate(1); // 1 ticket per second
        config.setMaxTicketCapacity(50);
    }

    public SimConfigDTO getConfig() {
        return config;
    }

    public void updateConfig(SimConfigDTO newConfig) {
        // Update only the properties needed
        this.config.setTotalTickets(newConfig.getTotalTickets());
        this.config.setTicketReleaseRate(newConfig.getTicketReleaseRate());
        this.config.setCustomerRetrievalRate(newConfig.getCustomerRetrievalRate());
        this.config.setMaxTicketCapacity(newConfig.getMaxTicketCapacity());
    }
}
