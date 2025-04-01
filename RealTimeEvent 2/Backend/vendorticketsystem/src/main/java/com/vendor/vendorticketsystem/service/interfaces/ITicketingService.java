package com.vendor.vendorticketsystem.service.interfaces;


import com.vendor.vendorticketsystem.model.dto.SimConfigDTO;

public interface ITicketingService {
    String addCustomer(String name, boolean isVip, int ticketCount);

    String startVendor();

    String stopVendor();

    String startCustomers();

    SimConfigDTO getConfig();

    String updateConfig(SimConfigDTO config);

    String getStatus();

    int getPoolStatus();
}
