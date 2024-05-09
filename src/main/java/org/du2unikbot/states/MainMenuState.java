package org.du2unikbot.states;

import org.du2unikbot.Du2UnikBot;
import org.du2unikbot.entities.Ticket;
import org.du2unikbot.dialogs.TicketCreationDialog;
import org.du2unikbot.entities.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

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
            default -> throw new IllegalStateException("Unexpected value: " + messageText);
        }
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        int ticketId = extractIdFromMessage(messageText);

        switch (callbackData) {
            case "select" -> updateUserInTicket(
                    du2UnikBot.getTicketRepository().findById(ticketId), ticketId, messageText, true);
            case "delete" -> updateUserInTicket(
                    du2UnikBot.getTicketRepository().findById(ticketId), ticketId, messageText, false);
            default -> throw new IllegalStateException("Unexpected value: " + callbackData);
        }
    }
    public void showTickets(boolean showAll) {
        List<Ticket> tickets;

        if (showAll) {
            tickets = du2UnikBot.getTicketRepository().findAll();
        } else {
            tickets = du2UnikBot.getTicketRepository().findAllByUsers_Username(currentUser.getUserName());
        }

        for (Ticket ticket : tickets) {
            boolean containsUsername = du2UnikBot.getTicketRepository()
                    .existsByUsers_UsernameAndId(currentUser.getUserName(), ticket.getId());

            SendMessage message = ticket.createMessage(containsUsername);
            message.setChatId(chatId);
            try {
                du2UnikBot.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (tickets.isEmpty()) {
            String messageText = showAll ? "Пока не создан ни один тикет!" : "У вас нет тикетов!";
            du2UnikBot.sendMessage(chatId, messageText);
        }
    }

    public void createTicket(){
        du2UnikBot.setCurrentState("select_start_point");

        du2UnikBot.setCurrentTicket(new Ticket());
        userEntity = new User(currentUser.getId(), currentUser.getUserName());

        //Сохранение пользователя в бд независимо от того, создастся тикет или нет
        User createdUser = du2UnikBot.getUserRepository().save(userEntity);

        Set<User> users = new HashSet<>();
        users.add(createdUser);

        du2UnikBot.getCurrentTicket().setUsers(users);

        du2UnikBot.setCurrentDialog(new TicketCreationDialog());
        SendMessage startPointsRequest = du2UnikBot.getCurrentDialog().getDialogMessages().get("start_point");
        startPointsRequest.setChatId(chatId);
        try {
            du2UnikBot.execute(startPointsRequest);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUserInTicket(Ticket ticket, int ticketId, String messageText, boolean userAdding) {
        userEntity = new User(currentUser.getId(), currentUser.getUserName());

        if (userAdding) {
            if (messageText.contains(currentUser.getUserName())) {
                du2UnikBot.sendMessage(chatId, "Вы уже записаны в тикет с ID: " + ticketId + "!");
            } else {
                // Добавление пользователя в тикет

                //Сохранение пользователя в бд
                User createdUser = du2UnikBot.getUserRepository().save(userEntity);

                Set<User> users = ticket.getUsers();
                users.add(createdUser);

                // Изменение числа забронированных мест в тикете
                ticket.setReservedSeats(ticket.getReservedSeats() + 1);

                du2UnikBot.getTicketRepository().save(ticket);

                du2UnikBot.sendMessage(chatId, "Вы успешно выбрали тикет с ID: " + ticketId);

                checkTicketsForFull(ticket);
            }
        } else {
            // Удаление пользователя из тикета
            User deletedUser = ticket.getUserById(userEntity.getId());

            if (deletedUser != null){
                // Удаление пользователя из тикета
                Set<User> users = ticket.getUsers();
                users.remove(deletedUser);

                // Изменение числа забронированных мест в тикете
                ticket.setReservedSeats(ticket.getReservedSeats() - 1);

                du2UnikBot.getTicketRepository().save(ticket);

                if (users.isEmpty()) {
                    // Если в тикете больше нет пользователей, удаляем тикет и сообщение
                    du2UnikBot.getTicketRepository().delete(ticket);
                    du2UnikBot.deleteMessage(chatId, messageId);
                } else {
                    // Удаление пользователя из сообщения
                    deleteUsernameFromMessage(chatId, messageId, messageText, currentUser.getUserName(), ticket.getReservedSeats());
                }

                du2UnikBot.sendMessage(chatId, "Ваш тикет с ID: " + ticketId + " успешно удален");
            } else {
                du2UnikBot.sendMessage(chatId, "Пользователь не найден. Невозможно удалить тикет.");
            }
        }
    }

    public void checkTicketsForFull(Ticket ticket) {
        if (ticket.getReservedSeats() == 4){
            StringBuilder usernames = new StringBuilder();

            String currentUsername = currentUser.getUserName();
            for (User user : ticket.getUsers()){
                String username = user.getUsername();

                if (!username.equals(currentUsername)){
                    usernames.append("\n@").append(username);
                }
            }
//            createChatInviteLink(chatId);
//            bot.getInviteLink();
            du2UnikBot.sendMessage(chatId, "Пожалуйста, свяжитесь с" + usernames);

            du2UnikBot.getTicketRepository().delete(ticket);
            du2UnikBot.deleteMessage(chatId, messageId);
        } else {
            addUsernameToMessage(chatId, messageId, messageText, currentUser.getUserName(), ticket.getReservedSeats());
        }
    }

}
