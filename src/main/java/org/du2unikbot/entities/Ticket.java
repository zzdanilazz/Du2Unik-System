package org.du2unikbot.entities;

import com.vdurmont.emoji.EmojiParser;
import org.du2unikbot.web.bot.constant.CallbackButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.*;

import static org.du2unikbot.util.TicketStringFormatter.formatTicketMessage;
import static org.du2unikbot.web.bot.constant.Strings.*;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticketsIdSeq", sequenceName = "ticket_ids", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketsIdSeq")
    @Column
    private Integer id;

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "finish_point")
    private String finishPoint;

    @Column(name = "reserved_seats")
    private int reservedSeats;

    @Column(name = "meeting_time")
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

    public static InlineKeyboardMarkup createKeyboard(CallbackButton button) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        String emoji = button == CallbackButton.SELECT ? EmojiParser.parseToUnicode(THUMBS_UP_EMOJI) : EmojiParser.parseToUnicode(CROSS_EMOJI);
        String option = button.getOption();
        String callback = button.getCallback();
        InlineKeyboardButton inlineButton = new InlineKeyboardButton(emoji + "\t" + option);
        inlineButton.setCallbackData(callback);
        row.add(inlineButton);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public SendMessage createMessage(boolean containsUsername) {
        String messageText = formatTicketMessage(this);
        SendMessage message = new SendMessage();
        message.setParseMode(HTML_PARSE_MODE);
        message.setText(messageText);

        // Проверка, записан ли уже пользователь в тикет
        InlineKeyboardMarkup keyboardMarkup;
        if (containsUsername){
            keyboardMarkup = createKeyboard(CallbackButton.DELETE);
        } else {
            keyboardMarkup = createKeyboard(CallbackButton.SELECT);
        }
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
