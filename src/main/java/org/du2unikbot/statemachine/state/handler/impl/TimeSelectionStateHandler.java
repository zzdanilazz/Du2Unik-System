package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.dialogs.Dialog;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_DIALOG;
import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_TICKET;
import static org.du2unikbot.statemachine.event.BotEvent.CANCELLED_MEETING_TIME_SELECTION;
import static org.du2unikbot.statemachine.event.BotEvent.SELECTED_TIME;
import static org.du2unikbot.web.bot.constant.Strings.*;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.CONFIRMATION;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.MEETING_TIME;

public class TimeSelectionStateHandler implements StateHandler {
    private Du2UnikBot du2UnikBot;

    @Override
    public void handleMessage(Du2UnikBot du2UnikBot, Message message, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        try {
            this.du2UnikBot = du2UnikBot;
            final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();
            User currentUser = message.getFrom();
            final long userId = currentUser.getId();
            persister.restore(stateMachine, userId);

            String messageText = message.getText();
            long chatId = message.getChatId();

            Message replyMessage = message.getReplyToMessage();

            if (replyMessage != null) {
                String replyMessageText = replyMessage.getText();
                if (replyMessageText.equals(MEETING_TIME_SELECTION_TITLE)) {
                    handleMeetingTime(stateMachine, messageText, chatId, message);
                }
            } else {
                du2UnikBot.sendMessage(chatId, MEETING_TIME_SELECTION_CANCELLATION);
                stateMachine.sendEvent(CANCELLED_MEETING_TIME_SELECTION);
            }

            persister.persist(stateMachine, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleCallbackQuery(Du2UnikBot du2UnikBot, CallbackQuery callbackQuery, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        /* Пока данное состояние не поддерживает обработку коллбеков */
    }

    private void handleMeetingTime(StateMachine<BotState, BotEvent> stateMachine, String replyMessageText, long chatId, Message message) {
        StringBuilder errorMessage = new StringBuilder();

        User currentUser = message.getFrom();

        // Допустимое время встречи
        if (isValidMeetingTime(currentUser, replyMessageText, errorMessage)) {
            // Сохранение времени встречи в ticket
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);

            Ticket currentTicket = (Ticket) stateMachine.getExtendedState().getVariables().get(CURRENT_TICKET);
            currentTicket.setMeetingTime(LocalTime.parse(replyMessageText, formatter));

            // Получение сообщения, на которое был дан ответ
            Message replyMessage = message.getReplyToMessage();
            int messageRequestId = replyMessage.getMessageId();

            // Удаление ответа
            du2UnikBot.deleteMessage(chatId, message.getMessageId());

            // Отправка копии запроса
            du2UnikBot.editMessageText(chatId, messageRequestId, replyMessage.getText() + " " + replyMessageText);

            // Удаление оригинала запроса
            du2UnikBot.deleteMessage(chatId, messageRequestId);

            Dialog currentDialog = (Dialog) stateMachine.getExtendedState().getVariables().get(CURRENT_DIALOG);
            SendMessage request = currentDialog.getDialogMessages().get(CONFIRMATION);
            request.setChatId(chatId);

            try {
                du2UnikBot.execute(request);
                stateMachine.sendEvent(SELECTED_TIME);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            // Недопустимое время встречи
            du2UnikBot.sendMessage(chatId, errorMessage.toString());
            Dialog currentDialog = (Dialog) stateMachine.getExtendedState().getVariables().get(CURRENT_DIALOG);
            SendMessage request = currentDialog.getDialogMessages().get(MEETING_TIME);
            request.setChatId(chatId);
            try {
                du2UnikBot.execute(request);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidMeetingTime(User currentUser, String meetingTime, StringBuilder errorMessage) {
        try {
            // Проверка формата времени
            LocalTime inputTime = LocalTime.parse(meetingTime);
            LocalTime currentTime = LocalTime.now();

            List<Ticket> tickets = du2UnikBot.getTicketRepository().findAll();

            long userId = currentUser.getId();

            // Проход по значениям тикетов и проверка времени и пользователя
            for (Ticket ticket : tickets) {
                if (ticket.getMeetingTime().equals(LocalTime.parse(meetingTime))
                        && ticket.getUsers().stream().anyMatch(user -> userId == user.getId())) {
                    errorMessage.append(EXISTING_MEETING_TIME_ERROR);
                    return false;
                }
            }

            // Проверка, что введенное время на 10 минут больше текущего времени
            if (inputTime.isBefore(currentTime.plusMinutes(10))) {
                errorMessage.append(TOO_EARLY_MEETING_TIME_ERROR);
                return false;
            }

            // Время встречи валидно
            return true;
        } catch (DateTimeParseException e) {
            errorMessage.append(INVALID_MEETING_TIME_ERROR);
            return false;
        }
    }
}
