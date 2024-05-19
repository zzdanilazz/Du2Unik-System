package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.entities.User;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.constant.CallbackButton;
import org.du2unikbot.web.bot.dialogs.Dialog;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_DIALOG;
import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_TICKET;
import static org.du2unikbot.statemachine.event.BotEvent.*;
import static org.du2unikbot.web.bot.constant.CallbackButton.*;
import static org.du2unikbot.web.bot.constant.Strings.RESERVED_SEATS_CANCELLATION;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.MEETING_TIME;

public class ReservedSeatsSelectionStateHandler implements StateHandler {
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
            String messageText = message.getText();
            long chatId = message.getChatId();
            int messageId = message.getMessageId();

            CallbackButton callbackButton = fromCallback(callbackData);
            if (callbackButton == SEATS_1 || callbackButton == SEATS_2 || callbackButton == SEATS_3) {
                handleReservedSeatsSelection(stateMachine, callbackButton, messageText, chatId, messageId, userId);
            } else {
                throw new IllegalStateException("Unexpected value: " + callbackButton);
            }

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
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
            du2UnikBot.sendMessage(chatId, RESERVED_SEATS_CANCELLATION);

            stateMachine.sendEvent(CANCELLED_RESERVED_SEATS_SELECTION);

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReservedSeatsSelection(
            StateMachine<BotState, BotEvent> stateMachine,
            CallbackButton callbackButton,
            String messageText,
            long chatId,
            int messageId,
            long userId) {
        String reservedSeatsString = callbackButton.getOption();
        int reservedSeats = Integer.parseInt(reservedSeatsString);

        // Изменение сообщения с учетом выбора
        du2UnikBot.editMessageAndRemoveKeyboard(chatId, messageText, reservedSeatsString, messageId);

        Ticket currentTicket = (Ticket) stateMachine.getExtendedState().getVariables().get(CURRENT_TICKET);
        User user = currentTicket.getUserById(userId);
        int friendsCount = reservedSeats - 1;
        user.setFriendsCount(friendsCount);
        du2UnikBot.getUserRepository().save(user);
        currentTicket.setReservedSeats(reservedSeats);

        Dialog currentDialog = (Dialog) stateMachine.getExtendedState().getVariables().get(CURRENT_DIALOG);
        SendMessage request = currentDialog.getDialogMessages().get(MEETING_TIME);
        request.setChatId(chatId);

        try {
            du2UnikBot.execute(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        switch (callbackButton) {
            case SEATS_1 -> stateMachine.sendEvent(RESERVED_ONE_SEAT);
            case SEATS_2 -> stateMachine.sendEvent(RESERVED_TWO_SEATS);
            case SEATS_3 -> stateMachine.sendEvent(RESERVED_THREE_SEATS);
        }
    }
}
