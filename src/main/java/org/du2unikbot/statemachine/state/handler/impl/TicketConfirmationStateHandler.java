package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.constant.CallbackButton;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_TICKET;
import static org.du2unikbot.statemachine.event.BotEvent.*;
import static org.du2unikbot.web.bot.constant.CallbackButton.*;
import static org.du2unikbot.web.bot.constant.Strings.*;

public class TicketConfirmationStateHandler implements StateHandler {
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
            if (callbackButton == CONFIRM_TICKET_CREATION || callbackButton == NOT_CONFIRM_TICKET_CREATION) {
                handleTicketConfirmation(stateMachine, callbackButton, message);
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
            du2UnikBot.sendMessage(chatId, TICKET_CONFIRMATION_CANCELLATION);

            stateMachine.sendEvent(CANCELLED_TICKET_CONFIRMATION);

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketConfirmation(StateMachine<BotState, BotEvent> stateMachine, CallbackButton callbackButton, Message message) {
        SendMessage sendMessage = new SendMessage();

        long chatId = message.getChatId();
        sendMessage.setChatId(chatId);

        switch (callbackButton) {
            case CONFIRM_TICKET_CREATION -> {
                Ticket currentTicket = (Ticket) stateMachine.getExtendedState().getVariables().get(CURRENT_TICKET);
                du2UnikBot.getTicketRepository().save(currentTicket);

                int ticketId = currentTicket.getId();
                String successText = String.format(TICKET_CONFIRMATION_SUCCESS, ticketId);
                sendMessage.setText(successText);
                stateMachine.sendEvent(CONFIRMED_TICKET);
            }
            case NOT_CONFIRM_TICKET_CREATION -> {
                sendMessage.setText(TICKET_CONFIRMATION_CANCELLATION);
                stateMachine.sendEvent(NOT_CONFIRMED_TICKET);
            }
        }

        Ticket newTicket = new Ticket();
        stateMachine.getExtendedState().getVariables().put(CURRENT_TICKET, newTicket);

        sendMessage.setReplyMarkup(du2UnikBot.getStartCommandKeyboard());

        try {
            du2UnikBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Удаление после подтверждения
        int messageId = message.getMessageId();
        du2UnikBot.deleteMessage(chatId, messageId);
    }
}
