package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.entities.User;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.constant.CallbackButton;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

import static org.du2unikbot.statemachine.StateMachineKey.TICKET_MESSAGE;
import static org.du2unikbot.statemachine.event.BotEvent.*;
import static org.du2unikbot.util.TicketStringFormatter.extractIdFromMessage;
import static org.du2unikbot.util.TicketStringFormatter.formatTicketMessage;
import static org.du2unikbot.web.bot.constant.CallbackButton.*;
import static org.du2unikbot.web.bot.constant.Strings.*;

public class AddingFriendsStateHandler implements StateHandler {
    private Du2UnikBot du2UnikBot;

    @Override
    public void handleCallbackQuery(Du2UnikBot du2UnikBot, CallbackQuery callbackQuery, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        try {
            this.du2UnikBot = du2UnikBot;
            final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
            final long userId = callbackQuery.getFrom().getId();
            persister.restore(stateMachine, userId);

            String callbackData = callbackQuery.getData();
            Message message = callbackQuery.getMessage();

            CallbackButton callbackButton = fromCallback(callbackData);
            if (callbackButton == FRIENDS_1 || callbackButton == FRIENDS_2 || callbackButton == FRIENDS_0) {
                handleFriendAdding(stateMachine, callbackButton, message, userId);
            } else {
                throw new IllegalStateException("Unexpected value: " + callbackButton);
            }

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleFriendAdding(
            StateMachine<BotState, BotEvent> stateMachine,
            CallbackButton callbackButton,
            Message dialogMessage,
            long userId
    ) {
        long chatId = dialogMessage.getChatId();

        Message ticketMessage = (Message) stateMachine.getExtendedState().getVariables().get(TICKET_MESSAGE);
        int ticketId = extractIdFromMessage(ticketMessage.getText());
        Ticket ticket = du2UnikBot.getTicketRepository().findById(ticketId);

        int friendsCount = Integer.parseInt(callbackButton.getOption());
        addUserInTicket(ticket, dialogMessage.getFrom(), ticketId, ticketMessage, chatId, userId, friendsCount);

        // Удаление диалога
        int messageId = dialogMessage.getMessageId();
        du2UnikBot.deleteMessage(chatId, messageId);

        switch (callbackButton) {
            case FRIENDS_1 -> stateMachine.sendEvent(ADDED_ONE_FRIEND);
            case FRIENDS_2 -> stateMachine.sendEvent(ADDED_TWO_FRIENDS);
            case FRIENDS_0 -> stateMachine.sendEvent(NOT_ADDED_FRIEND);
        }
    }

    private void addUserInTicket(
            Ticket ticket,
            org.telegram.telegrambots.meta.api.objects.User currentUser,
            int ticketId,
            Message ticketMessage,
            long chatId,
            long userId,
            int friendsCount
    ) {
        String messageText = ticketMessage.getText();
        if (messageText.contains(currentUser.getUserName())) {
            String sendMessageText = String.format(TICKET_ENROLLMENT, ticketId);
            du2UnikBot.sendMessage(chatId, sendMessageText);
        } else {
            // Добавление пользователя в тикет
            User addedUser = du2UnikBot.getUserRepository().findById(userId);

            Set<User> users = ticket.getUsers();
            users.add(addedUser);

            // Изменение числа забронированных мест в тикете
            ticket.setReservedSeats(ticket.getReservedSeats() + 1 + friendsCount);
            du2UnikBot.getTicketRepository().save(ticket);

            // Изменение числа друзей пользователя
            addedUser.setFriendsCount(friendsCount);
            du2UnikBot.getUserRepository().save(addedUser);

            String sendMessageText = String.format(TICKET_SELECTION_SUCCESS, ticketId);
            du2UnikBot.sendMessage(chatId, sendMessageText);

            updateTicketCompleteness(ticket, ticketMessage.getMessageId(), chatId);
        }
    }

    public void updateTicketCompleteness(Ticket ticket, Integer messageId, long chatId) {
        // Если тикет собрался
        if (ticket.getReservedSeats() == 4){
            // Удаляем текущее сообщение
            du2UnikBot.deleteMessage(chatId, messageId);

            Set<User> ticketUsers = ticket.getUsers();

            String pinnedTicketText = formatTicketMessage(ticket);

            // Каждому участнику тикета отправляем сообщение и закрепляем его
            for (User user : ticketUsers) {
                du2UnikBot.sendAndPinMessage(user.getChatId(), pinnedTicketText);
            }

            du2UnikBot.getTicketRepository().delete(ticket);
        } else {
            updateTicketMessage(chatId, messageId, ticket);
        }
    }

    private void updateTicketMessage(long chatId, int messageId, Ticket ticket) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        String newMessageText = formatTicketMessage(ticket);

        editMessageText.setText(newMessageText);
        editMessageText.setParseMode(HTML_PARSE_MODE);

        // Установка клавиатуры с кнопкой "Удалить"
        editMessageText.setReplyMarkup(Ticket.createKeyboard(CallbackButton.DELETE));

        try {
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Du2UnikBot du2UnikBot, Message message, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        this.du2UnikBot = du2UnikBot;

        try {
            final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
            final long userId = message.getFrom().getId();

            persister.restore(stateMachine, userId);

            long chatId = message.getChatId();
            du2UnikBot.sendMessage(chatId, ADDING_FRIENDS_CANCELLATION);

            stateMachine.sendEvent(CANCELLED_ADDING_FRIENDS);

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
