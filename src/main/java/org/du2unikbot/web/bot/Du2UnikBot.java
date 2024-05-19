package org.du2unikbot.web.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.du2unikbot.config.BotProperties;
import org.du2unikbot.repositories.TicketRepository;
import org.du2unikbot.repositories.UserRepository;
import org.du2unikbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinAllChatMessages;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.du2unikbot.web.bot.constant.Strings.*;


@Slf4j
@Getter
@Component
public class Du2UnikBot extends TelegramLongPollingBot {
    public final BotProperties botProperties;
    protected final UserRepository userRepository;
    protected final TicketRepository ticketRepository;
    private final BotService botService;

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Autowired
    public Du2UnikBot(
            BotProperties botProperties,
            UserRepository userRepository,
            TicketRepository ticketRepository,
            BotService botService) {
        this.botProperties = botProperties;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.botService = botService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        botService.handleUpdate(this, update);
    }

    public ReplyKeyboardMarkup getStartCommandKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        // Создание одной строки для кнопок
        KeyboardRow row = new KeyboardRow();
        row.add(createButton(ALL_TICKETS));
        row.add(createButton(CREATE_TICKET));
        row.add(createButton(MY_TICKETS));

        keyboardMarkup.setKeyboard(List.of(row));

        return keyboardMarkup;
    }

    private KeyboardButton createButton(String text) {
        KeyboardButton button = new KeyboardButton();
        button.setText(text);

        return button;
    }

    public void deleteMessage(long chatId, int messageId){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode(HTML_PARSE_MODE);
        sendMessage.setReplyMarkup(getStartCommandKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendAndPinMessage(long chatId, String messageText) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(messageText);
            sendMessage.setParseMode(HTML_PARSE_MODE);

            Message sentMessage = execute(sendMessage);

            unpinAllMessages(chatId);
            pinMessage(chatId, sentMessage.getMessageId());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void pinMessage(long chatId, int messageId) {
        PinChatMessage pinMessage = new PinChatMessage();
        pinMessage.setChatId(chatId);
        pinMessage.setMessageId(messageId);

        try {
            execute(pinMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void unpinAllMessages(long chatId) {
        try {
            UnpinAllChatMessages unpinAllChatMessages = new UnpinAllChatMessages();
            unpinAllChatMessages.setChatId(chatId);
            execute(unpinAllChatMessages);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessageText(long chatId, int oldMessageId, String newText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newText);
        message.setReplyToMessageId(oldMessageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessageAndRemoveKeyboard(long chatId, String messageText, String buttonText, int messageId) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(messageText + " " + buttonText);

        // Удаление клавиатуры
        editMessageText.setReplyMarkup(null);

        try {
            // Отправка запроса на изменение сообщения
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
