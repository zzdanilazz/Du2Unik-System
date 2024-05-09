package org.du2unikbot.entities;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticketsIdSeq", sequenceName = "ticket_ids", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketsIdSeq")
    @Column
    private Integer id;

    @Column
    private String startPoint;

    @Column
    private String finishPoint;

    @Column
    private int reservedSeats;

    @Column
    private LocalTime meetingTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_tickets",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    public Ticket() {}

    public Ticket(int id, String startPoint, String finishPoint, int reservedSeats, LocalTime meetingTime) {
        this.id = id;
        this.startPoint = startPoint;
        this.finishPoint = finishPoint;
        this.reservedSeats = reservedSeats;
        this.meetingTime = meetingTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getFinishPoint() {
        return finishPoint;
    }

    public void setFinishPoint(String finishPoint) {
        this.finishPoint = finishPoint;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public LocalTime getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = meetingTime;
    }
    public Set<User> getUsers() {
        return users;
    }
    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public User getUserById(Long targetId) {
        for (User user : users) {
            if (Objects.equals(user.getId(), targetId)) {
                return user; // Возвращаем пользователя с заданным id
            }
        }
        return null; // Если пользователь с заданным id не найден
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", start:" + startPoint +
                ", finish:" + finishPoint +
                ", reserved seats:" + reservedSeats +
                ", meeting time:" + meetingTime;
    }

    //    public static Map<Integer, Ticket> initializeTickets() {
//        Map<Integer, Ticket> tickets = new LinkedHashMap<>();
//
//        // Выполните операции с базой данных, заполните массив тикетов и т.д.
//        // Создание первого тикета
//        List<String> userIDs1 = new ArrayList<>(Arrays.asList("IIsaf4ik", "zz_danila_zz"));
//        Ticket ticket1 = new Ticket(1, "ДУ", "Двойка", 2, LocalTime.of(10, 0), userIDs1);
//        tickets.put(ticket1.id, ticket1);
//
//        // Создание второго тикета
//        List<String> userIDs2 = new ArrayList<>(Arrays.asList("probox36", "IIsaf4ik"));
//        Ticket ticket2 = new Ticket(2, "Двойка", "ДУ", 2, LocalTime.of(15, 30), userIDs2);
//        tickets.put(ticket2.id, ticket2);
//
//        // Создание третьего тикета
//        List<String> userIDs3 = new ArrayList<>(Arrays.asList("IIsaf4ik", "zz_danila_zz", "probox36"));
//        Ticket ticket3 = new Ticket(3, "ДУ", "Двойка", 3, LocalTime.of(9, 45), userIDs3);
//        tickets.put(ticket3.id, ticket3);
//
//        return tickets;
//    }


    public String createTicketMessageText(String inputMessageText) {
        StringBuilder messageText = new StringBuilder();
        messageText.append(inputMessageText).append("\n");
        messageText.append("<b>ID: </b>").append(id).append("\n");
        messageText.append("<b>Старт: </b>").append(startPoint).append("\n");
        messageText.append("<b>Финиш: </b>").append(finishPoint).append("\n");
        messageText.append("<b>Свободных мест: </b>").append(4 - reservedSeats).append("\n");
        messageText.append("<b>Время встречи: </b>").append(meetingTime.format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
        messageText.append("<b>Попутчики: </b>").append("\n");
        if (reservedSeats > users.size()) {
            messageText.append(reservedSeats - users.size()).append(" неизвестных пользователя");
        }
        for (User user: users) {
            messageText.append("\n").append("@").append(user.getUsername());
        }

        return messageText.toString();
    }

    public static InlineKeyboardMarkup createSelectButtonKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        String likeEmoji = EmojiParser.parseToUnicode(":thumbsup:");
        InlineKeyboardButton selectButton = new InlineKeyboardButton(  likeEmoji + "\tВыбрать");
        selectButton.setCallbackData("select");
        row.add(selectButton);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createDeleteButtonKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        String crossEmoji = EmojiParser.parseToUnicode(":x:");
        InlineKeyboardButton deleteButton = new InlineKeyboardButton( crossEmoji + "\tУдалить");
        deleteButton.setCallbackData("delete");
        row.add(deleteButton);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public SendMessage createMessage(boolean containsUsername) {
        String messageText = createTicketMessageText("");
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setText(messageText);

        // Проверка, записан ли уже пользователь в тикет
        InlineKeyboardMarkup keyboardMarkup;
        if (containsUsername){
            keyboardMarkup = createDeleteButtonKeyboard();
        } else {
            keyboardMarkup = createSelectButtonKeyboard();
        }
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
