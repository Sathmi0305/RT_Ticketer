package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.worker.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerFactory {
    private final List<Customer> customerThreads = new ArrayList<>();

    public Customer createCustomer(String name, boolean isVip, TicketPool ticketPool, int ticketCount, VendorManager vendorManager) {
        Customer customer = new Customer(ticketPool, name, isVip, ticketCount, vendorManager);
        customerThreads.add(customer);
        return customer;
    }

    public void startAllCustomers() {
        if (customerThreads.isEmpty()) {
            System.out.println("No customers to start.");
            return;
        }

        customerThreads.forEach(customer -> {
            if (!customer.isRunning()) {
                customer.restart();
            }
        });
    }

    public void stopAllCustomers() {
        customerThreads.forEach(Customer::stop);

    }
    public boolean hasCustomers() {
        return !customerThreads.isEmpty();
    }
}
