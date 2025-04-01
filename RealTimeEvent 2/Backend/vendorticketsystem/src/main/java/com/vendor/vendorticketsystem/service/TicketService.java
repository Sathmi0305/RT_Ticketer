package com.vendor.vendorticketsystem.service;

import com.vendor.vendorticketsystem.model.Ticket;
import com.vendor.vendorticketsystem.model.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Adds a ticket to the database.
     */
    public Ticket addTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    /**
     * Updates the status of a ticket in the database.
     */
    public void updateTicketStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() ->
                new RuntimeException("Ticket not found with ID: " + ticketId));
        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }


}
