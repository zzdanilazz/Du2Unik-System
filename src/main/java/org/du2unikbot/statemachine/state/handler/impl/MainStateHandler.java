package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.entities.User;
import org.du2unikbot.statemachine.StateMachineKey;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.constant.CallbackButton;
import org.du2unikbot.web.bot.dialogs.Dialog;
import org.du2unikbot.web.bot.dialogs.DialogKey;
import org.du2unikbot.web.bot.dialogs.adding_friend.AddingFriendsDialog;
import org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialog;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.du2unikbot.statemachine.StateMachineKey.TICKET_MESSAGE;
import static org.du2unikbot.statemachine.event.BotEvent.CREATED_TICKET;
import static org.du2unikbot.statemachine.event.BotEvent.SELECTED_TICKET;
import static org.du2unikbot.util.TicketStringFormatter.extractIdFromMessage;
import static org.du2unikbot.util.TicketStringFormatter.formatTicketMessage;
import static org.du2unikbot.web.bot.constant.CallbackButton.fromCallback;
import static org.du2unikbot.web.bot.constant.Strings.*;
import static org.du2unikbot.web.bot.dialogs.adding_friend.AddingFriendsDialogKey.THREE_FREE_SEATS;
import static org.du2unikbot.web.bot.dialogs.adding_friend.AddingFriendsDialogKey.TWO_FREE_SEATS;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.START_POINT;

public class MainStateHandler implements StateHandler {
    private Du2UnikBot du2UnikBot;

    @Override
    public void handleMessage(
            Du2UnikBot du2UnikBot,
            Message message,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister) {
        this.du2UnikBot = du2UnikBot;

        final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
        final long userId = message.getFrom().getId();

        String messageText = message.getText();
        String userName = message.getFrom().getUserName();
        long chatId = message.getChatId();

        boolean hasNoActiveTickets = !du2UnikBot.getTicketRepository().existsByUsers_Username(userName);

        try {
            persister.restore(stateMachine, userId);
            switch (messageText) {
                case MY_TICKETS -> showTickets(false, userName, chatId);
                case ALL_TICKETS -> showTickets(true, userName, chatId);
                case CREATE_TICKET -> {
                    if (hasNoActiveTickets) {
                        createTicket(stateMachine, userId, chatId);
                    } else {
                        du2UnikBot.sendMessage(chatId, ERROR_ONLY_ONE_ACTIVE_TICKET_ALLOWED);
                    }

                }
                default -> throw new IllegalStateException("Unexpected value: " + messageText);
            }

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleCallbackQuery(Du2UnikBot du2UnikBot, CallbackQuery callbackQuery, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        this.du2UnikBot = du2UnikBot;
        try {
            final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
            final long userId = callbackQuery.getFrom().getId();
            persister.restore(stateMachine, userId);

            Message message = callbackQuery.getMessage();
            String messageText = message.getText();
            int ticketId = extractIdFromMessage(messageText);
            Ticket ticket = du2UnikBot.getTicketRepository().findById(ticketId);

            long chatId = callbackQuery.getMessage().getChatId();
            if (ticket == null) {
                du2UnikBot.deleteMessage(chatId, message.getMessageId());
                du2UnikBot.sendMessage(chatId, NOT_EXISTING_TICKET_ERROR);
                return;
            }

            org.telegram.telegrambots.meta.api.objects.User currentUser = callbackQuery.getFrom();
            String callbackData = callbackQuery.getData();

            CallbackButton callbackButton = fromCallback(callbackData);

            String userName = message.getFrom().getUserName();
            boolean hasNoActiveTickets = !du2UnikBot.getTicketRepository().existsByUsers_Username(userName);

            switch (callbackButton) {
                case SELECT -> {
                    if (hasNoActiveTickets) {
                        handleTicketSelection(stateMachine, ticket, message, chatId, userId);
                    } else {
                        du2UnikBot.sendMessage(chatId, ERROR_ONLY_ONE_ACTIVE_TICKET_ALLOWED);
                    }
                }
                case DELETE -> deleteUserInTicket(ticket, currentUser, ticketId, message, chatId);
                default -> throw new IllegalStateException("Unexpected value: " + callbackButton);
            }

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTicketSelection(
            StateMachine<BotState, BotEvent> stateMachine,
            Ticket ticket,
            Message message,
            long chatId,
            long userId
    ) {
        stateMachine.getExtendedState().getVariables().put(TICKET_MESSAGE, message);

        int freeSeatsCount = 4 - ticket.getReservedSeats();
        switch (freeSeatsCount) {
            case 1 -> completeTicket(ticket, message, userId);
            case 2 -> {
                executeAddingFriendsDialog(TWO_FREE_SEATS, chatId);
                stateMachine.sendEvent(SELECTED_TICKET);
            }
            case 3 -> {
                executeAddingFriendsDialog(THREE_FREE_SEATS, chatId);
                stateMachine.sendEvent(SELECTED_TICKET);
            }
            default -> throw new IllegalArgumentException();
        }

    }

    private void completeTicket(Ticket ticket, Message message, long userId) {
        // Добавление пользователя в тикет
        User addedUser = du2UnikBot.getUserRepository().findById(userId);

        Set<User> ticketUsers = ticket.getUsers();
        ticketUsers.add(addedUser);

        // Изменение числа забронированных мест в тикете
        ticket.setReservedSeats(4);
        du2UnikBot.getTicketRepository().save(ticket);

        String sendMessageText = String.format(TICKET_SELECTION_SUCCESS, ticket.getId());
        long chatId = message.getChatId();
        du2UnikBot.sendMessage(chatId, sendMessageText);

        // Удаляем текущее сообщение с тикетом
        du2UnikBot.deleteMessage(chatId, message.getMessageId());

        String pinnedTicketText = formatTicketMessage(ticket);

        // Каждому участнику тикета отправляем сообщение и закрепляем его
        for (User user : ticketUsers) {
            du2UnikBot.sendAndPinMessage(user.getChatId(), pinnedTicketText);
        }

        du2UnikBot.getTicketRepository().delete(ticket);
    }

    private void executeAddingFriendsDialog(DialogKey dialogKey, long chatId){
        Dialog addingFriendsDialog = new AddingFriendsDialog();

        SendMessage addingFriendsRequest = addingFriendsDialog.getDialogMessages().get(dialogKey);
        addingFriendsRequest.setChatId(chatId);

        try {
            du2UnikBot.execute(addingFriendsRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void showTickets(boolean showAll, String userName, long chatId) {
        List<Ticket> tickets;

        if (showAll) {
            tickets = du2UnikBot.getTicketRepository().findAll();
        } else {
            tickets = du2UnikBot.getTicketRepository().findAllByUsers_Username(userName);
        }

        for (Ticket ticket : tickets) {
            boolean containsUsername = du2UnikBot.getTicketRepository()
                    .existsByUsers_UsernameAndId(userName, ticket.getId());

            SendMessage message = ticket.createMessage(containsUsername);
            message.setChatId(chatId);
            try {
                du2UnikBot.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (tickets.isEmpty()) {
            String messageText = showAll ? THERE_IS_NO_TICKET : YOU_HAVE_NO_TICKET;
            du2UnikBot.sendMessage(chatId, messageText);
        }
    }

    public void createTicket(StateMachine<BotState, BotEvent> stateMachine, long userId, long chatId){
        User user = du2UnikBot.getUserRepository().findById(userId);

        Ticket newTicket = new Ticket();
        newTicket.setUsers(new HashSet<>(Set.of(user)));
        stateMachine.getExtendedState().getVariables().put(StateMachineKey.CURRENT_TICKET, newTicket);

        Dialog newTicketCreationDialog = new TicketCreationDialog();
        stateMachine.getExtendedState().getVariables().put(StateMachineKey.CURRENT_DIALOG, newTicketCreationDialog);

        SendMessage startPointsRequest = newTicketCreationDialog.getDialogMessages().get(START_POINT);
        startPointsRequest.setChatId(chatId);

        try {
            du2UnikBot.execute(startPointsRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        stateMachine.sendEvent(CREATED_TICKET);
    }

    private void deleteUserInTicket(
            Ticket ticket,
            org.telegram.telegrambots.meta.api.objects.User currentUser,
            int ticketId,
            Message message,
            long chatId
    ) {
        // Удаление пользователя из тикета
        User deletedUser = ticket.getUserById(currentUser.getId());

        if (deletedUser != null){
            // Удаление пользователя из тикета
            Set<User> users = ticket.getUsers();
            users.remove(deletedUser);

            // Изменение числа забронированных мест в тикете
            ticket.setReservedSeats(ticket.getReservedSeats() - 1 - deletedUser.getFriendsCount());
            du2UnikBot.getTicketRepository().save(ticket);

            // Изменение числа друзей пользователя
            deletedUser.setFriendsCount(0);
            du2UnikBot.getUserRepository().save(deletedUser);

            int messageId = message.getMessageId();
            if (users.isEmpty()) {
                // Если в тикете больше нет пользователей, удаляем тикет и сообщение
                du2UnikBot.getTicketRepository().delete(ticket);
                du2UnikBot.deleteMessage(chatId, messageId);
            } else {
                // Удаление пользователя из сообщения
                updateTicketMessage(chatId, messageId, ticket);
            }

            String sendMessageText = String.format(TICKET_DELETING_SUCCESS, ticketId);
            du2UnikBot.sendMessage(chatId, sendMessageText);
        } else {
            String sendMessageText = String.format(TICKET_DELETING_FAILURE, ticketId);
            du2UnikBot.sendMessage(chatId, sendMessageText);
        }
    }

    private void updateTicketMessage(long chatId, int messageId, Ticket ticket) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        String newMessageText = formatTicketMessage(ticket);

        editMessageText.setText(newMessageText);
        editMessageText.setParseMode(HTML_PARSE_MODE);

        // Установка клавиатуры с кнопкой "Выбрать"
        editMessageText.setReplyMarkup(Ticket.createKeyboard(CallbackButton.SELECT));

        try {
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
