package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
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
import static org.du2unikbot.web.bot.constant.Strings.FINISH_POINT_SELECTION_TITLE;
import static org.du2unikbot.web.bot.constant.Strings.START_POINT_CANCELLATION;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.AVAILABLE_SEATS;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.FINISH_POINT;

public class StartPointSelectionStateHandler implements StateHandler {
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
            if (callbackButton == START_DU || callbackButton == START_FF || callbackButton == START_DVOYKA) {
                handleStartPointSelection(stateMachine, callbackButton, messageText, chatId, messageId);
            } else {
                throw new IllegalStateException("Unexpected value: " + callbackButton);
            }

            persister.persist(stateMachine, userId);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(
            Du2UnikBot du2UnikBot, Message message,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister) {
        this.du2UnikBot = du2UnikBot;

        try {
            final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
            final long userId = message.getFrom().getId();

            persister.restore(stateMachine, userId);

            long chatId = message.getChatId();
            du2UnikBot.sendMessage(chatId, START_POINT_CANCELLATION);

            stateMachine.sendEvent(CANCELLED_START_POINT_SELECTION);

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleStartPointSelection(
            StateMachine<BotState, BotEvent> stateMachine,
            CallbackButton startPointCallbackButton,
            String messageText,
            long chatId,
            int messageId
    ) {
        String startPoint = startPointCallbackButton.getOption();
        // Изменение сообщения с учетом выбора
        du2UnikBot.editMessageAndRemoveKeyboard(chatId, messageText, startPoint, messageId);

        Ticket currentTicket = (Ticket) stateMachine.getExtendedState().getVariables().get(CURRENT_TICKET);
        currentTicket.setStartPoint(startPoint);

        Dialog currentDialog = (Dialog) stateMachine.getExtendedState().getVariables().get(CURRENT_DIALOG);

        switch (startPointCallbackButton) {
            case START_DU -> {
                SendMessage request = currentDialog.getDialogMessages().get(FINISH_POINT);
                request.setChatId(chatId);

                try {
                    du2UnikBot.execute(request);
                    stateMachine.sendEvent(SELECTED_START_DU);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case START_DVOYKA -> {
                handleStartUniversity(chatId, currentTicket, currentDialog);
                stateMachine.sendEvent(SELECTED_START_DVOYKA);
            }
            case START_FF -> {
                handleStartUniversity(chatId, currentTicket, currentDialog);
                stateMachine.sendEvent(SELECTED_START_FF);
            }
        }
    }

    private void handleStartUniversity(long chatId, Ticket currentTicket, Dialog currentDialog) {
        // Жесткая установка точки финиша
        du2UnikBot.sendMessage(chatId, FINISH_POINT_SELECTION_TITLE + " ДУ");
        currentTicket.setFinishPoint("ДУ");

        SendMessage request = currentDialog.getDialogMessages().get(AVAILABLE_SEATS);
        request.setChatId(chatId);

        try {
            du2UnikBot.execute(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
