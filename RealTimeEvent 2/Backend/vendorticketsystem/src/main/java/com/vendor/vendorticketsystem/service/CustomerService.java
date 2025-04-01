package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.model.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendor.vendorticketsystem.model.Customer;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(String name, boolean isVip, int ticketCount) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setVip(isVip);
        customer.setTicketCount(ticketCount);
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
