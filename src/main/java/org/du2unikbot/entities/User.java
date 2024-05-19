package org.du2unikbot.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column
    private Long id;

    @Column
    private String username;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "friends_count")
    private Integer friendsCount;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
    private Set<Ticket> tickets;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(Integer friendsCount) {
        if (friendsCount <= 2) {
            this.friendsCount = friendsCount;
        } else throw new IllegalArgumentException();
    }

    public User() {}

    public User(Long id, String username, Long chatId, Integer friendsCount) {
        this.id = id;
        this.username = username;
        this.chatId = chatId;
        this.friendsCount = friendsCount;
    }
}
