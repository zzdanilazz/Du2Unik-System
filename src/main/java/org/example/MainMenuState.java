package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class MainMenuState extends State {
    public MainMenuState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleMessage(Message message) {
        // Инициализируем переменные
        super.handleMessage(message);

        switch (messageText){
            case "Мои тикеты" -> showTickets(false);
            case "Все тикеты" -> showTickets(true);
            case "Создать тикет" -> createTicket();
        }
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        int ticketId = extractIdFromMessage(messageText);

        switch (callbackData) {
            case "select" -> updateUserInTicket(du2UnikBot.getTickets().get(ticketId), ticketId, messageText, true);
            case "delete" -> updateUserInTicket(du2UnikBot.getTickets().get(ticketId), ticketId, messageText, false);
        }
    }
    public void showTickets(boolean showAll) {
//        du2UnikBot.setTickets(Ticket.initializeTickets());

        boolean hasTickets = false; // Флаг для проверки наличия тикетов

        for (Ticket ticket : du2UnikBot.getTickets().values()) {
            boolean containsUsername = ticket.getUsernames().contains(username);
            if (showAll || containsUsername) {
                SendMessage message = ticket.createMessage(containsUsername);
                message.setChatId(chatId);
                try {
                    du2UnikBot.execute(message);
                    hasTickets = true; // Устанавливаем флаг в true, если найдены тикеты
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!hasTickets) {
            String messageText = showAll ? "Пока не создан ни один тикет!" : "У вас нет тикетов!";
            du2UnikBot.sendMessage(chatId, messageText);
        }
    }
    public void createTicket(){
        du2UnikBot.setCurrentState("select_start_point");

        du2UnikBot.getCurrentTicket().setUsernames(new ArrayList<>(Collections.singleton((username))));
        du2UnikBot.setTicketCreationForm(new TicketCreationForm());
        SendMessage startPointsRequest = du2UnikBot.getTicketCreationForm().getFormMessages().get("start_point");
        startPointsRequest.setChatId(chatId);
//        startPointsRequest.setReplyMarkup(null);
        try {
            du2UnikBot.execute(startPointsRequest);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

//    private void updateUserInTicket(Ticket ticket, int ticketId, String messageText, boolean addingUser) {
//        if (ticket != null) {
//            if (addingUser) {
//                if (messageText.contains(username)) {
//                    du2UnikBot.sendMessage(chatId, "Вы уже записаны в тикет с ID: " + ticketId + "!");
//                } else {
//                    // Добавление пользователя в тикет
//                    ticket.getUsernames().add(username);
//
//                    du2UnikBot.sendMessage(chatId, "Вы успешно выбрали тикет с ID: " + ticketId);
//
//                    // Изменение числа забронированных мест в тикете
//                    ticket.setReservedSeats(ticket.getReservedSeats() + 1);
//
//                    checkTicketsForFull(ticket);
//
//                }
//            } else {
//                // Удаление пользователя из тикета
//                ticket.getUsernames().remove(username);
//
//                // Изменение числа забронированных мест в тикете
//                ticket.setReservedSeats(ticket.getReservedSeats() - 1);
//
//                if (ticket.getUsernames().isEmpty()) {
//                    // Если в тикете больше нет пользователей, удаляем тикет и сообщение
//                    du2UnikBot.getTickets().remove(ticketId);
//                    du2UnikBot.deleteMessage(chatId, messageId);
//                } else {
//                    // Удаление пользователя из сообщения
//                    deleteUsernameFromMessage(chatId, messageId, messageText, username, ticket.getReservedSeats());
//                }
//
//                du2UnikBot.sendMessage(chatId, "Ваш тикет с ID: " + ticketId + " успешно удален");
//            }
//        } else {
//            du2UnikBot.sendMessage(chatId, "Тикет с ID: " + ticketId + " не найден");
//        }
//    }

    private void updateUserInTicket(Ticket ticket, int ticketId, String messageText, boolean addingUser) {
        if (ticket != null) {
            if (addingUser) {
                if (messageText.contains(username)) {
                    du2UnikBot.sendMessage(chatId, "Вы уже записаны в тикет с ID: " + ticketId + "!");
                } else {
                    // Добавление пользователя в тикет
                    ticket.getUsernames().add(username);

                    // Изменение числа забронированных мест в тикете
                    ticket.setReservedSeats(ticket.getReservedSeats() + 1);

                    // Выполнение операции вставки данных в базу данных
//                    insertUserIntoTicket(ticketId, username, databaseManager);

                    checkTicketsForFull(ticket);
                    du2UnikBot.sendMessage(chatId, "Вы успешно выбрали тикет с ID: " + ticketId);
                }
            } else {
                // Удаление пользователя из тикета
                ticket.getUsernames().remove(username);

                // Изменение числа забронированных мест в тикете
                ticket.setReservedSeats(ticket.getReservedSeats() - 1);

                if (ticket.getUsernames().isEmpty()) {
                    // Если в тикете больше нет пользователей, удаляем тикет и сообщение
//                    deleteTicket(ticketId, databaseManager);
                    du2UnikBot.getTickets().remove(ticketId);
                    du2UnikBot.deleteMessage(chatId, messageId);
                } else {
                    // Удаление пользователя из сообщения
                    deleteUsernameFromMessage(chatId, messageId, messageText, username, ticket.getReservedSeats());
                }

                du2UnikBot.sendMessage(chatId, "Ваш тикет с ID: " + ticketId + " успешно удален");
            }
        } else {
            du2UnikBot.sendMessage(chatId, "Тикет с ID: " + ticketId + " не найден");
        }
    }

    private void insertUserIntoTicket(int ticketId, String username, DatabaseManager databaseManager) {
        String query = "INSERT tickets_users(ticket_id, user_id) VALUES (?, ?)";
        try (PreparedStatement statement = databaseManager.prepareStatement(query)) {
            statement.setInt(1, ticketId);
            statement.setString(2, username);
            statement.executeUpdate();
            databaseManager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTicket(int ticketId, DatabaseManager databaseManager) {
        String query = "DELETE FROM tickets WHERE id = ?";
        try (PreparedStatement statement = databaseManager.prepareStatement(query)) {
            statement.setInt(1, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkTicketsForFull(Ticket ticket) {
        if (ticket.getReservedSeats() == 4){
            StringBuilder usernamesString = new StringBuilder();

            for (String username : ticket.getUsernames()){
                if (!username.equals(this.username)){
                    usernamesString.append("\n@").append(username);
                }
            }
            du2UnikBot.sendMessage(chatId, "Пожалуйста, свяжитесь с" + usernamesString);

            du2UnikBot.deleteMessage(chatId, messageId);
        } else {
            addUsernameToMessage(chatId, messageId, messageText, username, ticket.getReservedSeats());
        }
    }

}
