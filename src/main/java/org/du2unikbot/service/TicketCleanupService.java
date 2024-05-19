package org.du2unikbot.service;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class TicketCleanupService {

    @Autowired
    private TicketRepository ticketRepository;

    @Scheduled(fixedRate = 60000) // Запуск каждые 60 секунд
    public void removeExpiredTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        LocalTime now = LocalTime.now();

        for (Ticket ticket : tickets) {
            if (ticket.getMeetingTime().isBefore(now) || ticket.getMeetingTime().equals(now)) {
                ticketRepository.delete(ticket);
            }
        }
    }
}
