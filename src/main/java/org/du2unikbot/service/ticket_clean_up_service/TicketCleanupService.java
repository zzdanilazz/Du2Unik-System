package org.du2unikbot.service.ticket_clean_up_service;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.entities.User;
import org.du2unikbot.repositories.TicketRepository;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.du2unikbot.util.TicketStringFormatter.formatTicketMessage;

@Service
public class TicketCleanupService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private Du2UnikBot du2UnikBot;

    @Scheduled(fixedRate = 60000) // Запуск каждые 60 секунд
    public void removeExpiredTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        LocalTime now = LocalTime.now();

        for (Ticket ticket : tickets) {
            if (ticket.getMeetingTime().isBefore(now) || ticket.getMeetingTime().equals(now)) {
                Set<User> ticketUsers = ticket.getUsers();
                String pinnedTicketText = formatTicketMessage(ticket);
                for (User user : ticketUsers) {
                    du2UnikBot.sendAndPinMessage(user.getChatId(), pinnedTicketText);
                }
                ticketRepository.delete(ticket);
            }
        }
    }
}
