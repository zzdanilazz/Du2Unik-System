package org.example;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ticket {
    private Integer id = 1;
    private static int counter = 1;
    private String startPoint;
    private String finishPoint;
    private int reservedSeats;
    private LocalTime meetingTime;
    private List<String> usernames;

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

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }


    public Ticket(int id, String startPoint, String finishPoint, int reservedSeats, LocalTime meetingTime, List<String> usernames) {
        if (id > this.id){
            counter = id;
        }
        this.id = id;
        this.startPoint = startPoint;
        this.finishPoint = finishPoint;
        this.reservedSeats = reservedSeats;
        this.meetingTime = meetingTime;
        this.usernames = usernames;
    }

    public Ticket(){
        if (id < counter){
            counter ++;
        }
        id = counter;
    }

//    public static Map<Integer, Ticket> initializeTickets() {
//        Map<Integer, Ticket> tickets = new LinkedHashMap<>();
//
//        DatabaseManager databaseManager = new DatabaseManager();
//        databaseManager.connect();
//
//        // Выполните операции с базой данных, заполните массив тикетов и т.д.
//        // Создание первого тикета
//        List<String> userIDs1 = new ArrayList<>(Arrays.asList("IIsaf4ik", "zz_danila_zz"));
//        Ticket ticket1 = new Ticket(1, "ДУ", "Двойка", 2, LocalTime.of(10, 0), userIDs1);
//        tickets.put(ticket1.id, ticket1);
//
//        // Создание второго тикета
//        List<String> userIDs2 = new ArrayList<>(Arrays.asList("probox36", "IIsaf4ik"));
//        Ticket ticket2 = new Ticket(2, "Двойка", "ДУ", 3, LocalTime.of(15, 30), userIDs2);
//        tickets.put(ticket2.id, ticket2);
//
//        // Создание третьего тикета
//        List<String> userIDs3 = new ArrayList<>(Arrays.asList("IIsaf4ik", "zz_danila_zz", "probox36"));
//        Ticket ticket3 = new Ticket(3, "ДУ", "Двойка", 3, LocalTime.of(9, 45), userIDs3);
//        tickets.put(ticket3.id, ticket3);
//
//        databaseManager.disconnect();
//
//
//
//        return tickets;
//    }

    public static Map<Integer, Ticket> initializeTickets() {
        Map<Integer, Ticket> tickets = new LinkedHashMap<>();

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();

        // Выполните SQL-запрос для выборки данных из таблицы tickets
        String query =
            "SELECT t.id, t.start_point, t.finish_point, t.reserved_seats, t.meeting_time, u.username " +
            "FROM tickets t " +
            "JOIN tickets_users tu ON t.id = tu.ticket_id " +
            "JOIN users u ON u.id = tu.user_id";
        try (Statement statement = databaseManager.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String start = resultSet.getString("start_point");
                String finish = resultSet.getString("finish_point");
                int reservedSeats = resultSet.getInt("reserved_seats");
                LocalTime meetingTime = resultSet.getTime("meeting_time").toLocalTime();

                // Получите список пользователей для данного тикета из таблицы tickets_users
                List<String> userIDs = getUsersForTicket(id, databaseManager);

                // Создайте экземпляр тикета и добавьте его в массив
                Ticket ticket = new Ticket(id, start, finish, reservedSeats, meetingTime, userIDs);
                tickets.put(id, ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseManager.disconnect();

        return tickets;
    }

    private static List<String> getUsersForTicket(int ticketID, DatabaseManager databaseManager) {
        List<String> usernames = new ArrayList<>();

        // Выполните SQL-запрос для выборки имен пользователей для данного тикета из таблицы tickets_users
        String query =
                "SELECT u.username " +
                "FROM tickets_users tu " +
                "JOIN users u ON tu.user_id = u.id " +
                "WHERE tu.ticket_id = ?";
        try (PreparedStatement statement = databaseManager.prepareStatement(query)) {
            statement.setInt(1, ticketID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usernames;
    }


    public String createTicketMessageText(String inputMessageText) {
        StringBuilder messageText = new StringBuilder();
        messageText.append(inputMessageText).append("\n");
        messageText.append("<b>ID: </b>").append(id).append("\n");
        messageText.append("<b>Старт: </b>").append(startPoint).append("\n");
        messageText.append("<b>Финиш: </b>").append(finishPoint).append("\n");
        messageText.append("<b>Свободных мест: </b>").append(4 - reservedSeats).append("\n");
        messageText.append("<b>Время встречи: </b>").append(meetingTime.format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
        messageText.append("<b>Попутчики: </b>").append("\n");
        messageText.append("@").append(usernames.get(0));
        if (reservedSeats > usernames.size()) {
            messageText.append(" и ").append(reservedSeats - usernames.size()).append(" его друга");
        }
        for (int i = 1; i < usernames.size(); i++) {
            String userId = usernames.get(i);
            messageText.append("\n").append("@").append(userId);
        }

        return messageText.toString();
    }

    public static InlineKeyboardMarkup createSelectButtonKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        String like_emoji = EmojiParser.parseToUnicode(":thumbsup:");
        InlineKeyboardButton selectButton = new InlineKeyboardButton(  like_emoji + "\tВыбрать");
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

        String cross_emoji = EmojiParser.parseToUnicode(":x:");
        InlineKeyboardButton deleteButton = new InlineKeyboardButton( cross_emoji + "\tУдалить");
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
