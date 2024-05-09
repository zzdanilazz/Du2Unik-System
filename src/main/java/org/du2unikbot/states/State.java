package org.du2unikbot.states;

import org.du2unikbot.Du2UnikBot;
import org.du2unikbot.entities.Ticket;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public abstract class State{
    protected long chatId;
    protected int messageId;
    protected String messageText;
    protected org.du2unikbot.entities.User userEntity;
    protected org.telegram.telegrambots.meta.api.objects.User currentUser;

    protected String callbackData;

    protected Du2UnikBot du2UnikBot;

    protected State(Du2UnikBot du2UnikBot) {
        this.du2UnikBot = du2UnikBot;
    }

    public void handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    public void handleMessage(Message message) {
        this.chatId = message.getChatId();
        this.messageText = message.getText();
        this.currentUser = message.getFrom();
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        this.chatId = callbackQuery.getMessage().getChatId();
        this.callbackData = callbackQuery.getData();
        this.messageText = callbackQuery.getMessage().getText();
        this.messageId = callbackQuery.getMessage().getMessageId();
        this.currentUser = callbackQuery.getFrom();
    }

    public int extractIdFromMessage(String messageText) {
        int id = 0;
        String regexPattern = "ID: " + "(\\d+)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.find()) {
            String idString = matcher.group(1);
            id = Integer.parseInt(idString);
        }

        return id;
    }

    public void addUsernameToMessage(long chatId, int messageId, String messageText, String username, int reservedSeats) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        // Добавляем username пользователя в текст сообщения
        String newMessageText = messageText + "\n" + "@" + username;

        // Изменяем количество свободных мест в сообщении
        newMessageText = updateFreeSeats(newMessageText, 4 - reservedSeats);
        editMessageText.setText(newMessageText);

        // Установка клавиатуры с кнопкой "Удалить"
        editMessageText.setReplyMarkup(Ticket.createDeleteButtonKeyboard());

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteUsernameFromMessage(long chatId, int messageId, String messageText, String username, int reservedSeats) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        // Удаляем username пользователя из сообщения
        String newMessageText = messageText.replace("\n" + "@" + username, "");

        // Изменяем количество свободных мест в сообщении
        newMessageText = updateFreeSeats(newMessageText, 4 - reservedSeats);
        editMessageText.setText(newMessageText);

        // Установка клавиатуры с кнопкой "Выбрать"
        editMessageText.setReplyMarkup(Ticket.createSelectButtonKeyboard());

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String updateFreeSeats(String messageText, int reservedSeats) {
        String regexPattern = "(?<=Свободных мест: )\\d+";
        String replacementString = String.valueOf(reservedSeats);
        return messageText.replaceAll(regexPattern, replacementString);
    }

    public void addChosenAndRemoveKeyboard(long chatId, String messageText, String buttonText, int messageId) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(messageText + " " + buttonText);

        // Удаление клавиатуры (установка значения null)
        editMessageText.setReplyMarkup(null);

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
