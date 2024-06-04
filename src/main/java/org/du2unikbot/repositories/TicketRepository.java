package org.du2unikbot.repositories;

import org.du2unikbot.entities.Ticket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Integer> {
    List<Ticket> findAll();
    List<Ticket> findAllByUsers_Username(String username);
    Ticket findById(int id);

     /**
     Содержится ли username в каком-либо тикете
      */
    boolean existsByUsers_Username(String username);

    /**
     Содержится ли username в заданном тикете
     */
    boolean existsByUsers_UsernameAndId(String username, Integer ticketId);
}
