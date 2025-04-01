package com.vendor.vendorticketsystem.controller;

import com.vendor.vendorticketsystem.model.dto.SimConfigDTO;
import com.vendor.vendorticketsystem.service.TicketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketingSystemController {

    private final TicketingService ticketingService;

    @Autowired
    public TicketingSystemController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    @PostMapping("/add-customer")
    public String addCustomer(@RequestParam String name,
                              @RequestParam boolean isVip,
                              @RequestParam int ticketCount) {
        return ticketingService.addCustomer(name, isVip, ticketCount);
    }

    @PostMapping("/start-vendor")
    public String startVendor() {
        return ticketingService.startVendor();
    }

    @PostMapping("/stop-vendor")
    public String stopVendor() {
        return ticketingService.stopVendor();
    }

    @PostMapping("/start-customers")
    public String startCustomers() {
        return ticketingService.startCustomers();
    }

    @GetMapping("/config")
    public SimConfigDTO getConfig() {
        return ticketingService.getConfig();
    }

    @PostMapping("/config")
    public String updateConfig(@RequestBody SimConfigDTO config) {
        return ticketingService.updateConfig(config);
    }

    @GetMapping("/status")
    public String getStatus() {
        return ticketingService.getStatus();
    }

    @GetMapping("/poolStatus")
    public int getPoolStatus() {
        return ticketingService.getPoolStatus();
    }
}
