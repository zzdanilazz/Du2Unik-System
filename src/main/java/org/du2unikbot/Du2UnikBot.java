package org.du2unikbot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.du2unikbot.config.BotProperties;
import org.du2unikbot.dialogs.Dialog;
import org.du2unikbot.entities.Ticket;
import org.du2unikbot.repositories.TicketRepository;
import org.du2unikbot.repositories.UserRepository;
import org.du2unikbot.states.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Getter
@Component
public class Du2UnikBot extends TelegramLongPollingBot {
    public final BotProperties botProperties;

    private Dialog currentDialog;
    private final Map<String, State> states;
    private State currentState;
    private Ticket currentTicket;

    protected final UserRepository userRepository;
    protected final TicketRepository ticketRepository;

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
            TicketRepository ticketRepository
    ) {
        this.botProperties = botProperties;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;

        states = new HashMap<>();

        // Добавление состояний
        states.put("start", new StartState(this));
        states.put("main_menu", new MainMenuState(this));

        states.put("select_start_point", new SelectStartPointState(this));
        states.put("select_finish_point", new SelectFinishPointState(this));
        states.put("select_reserved_seats", new SelectReservedSeatsState(this));
        states.put("enter_meeting_time", new EnterMeetingTimeState(this));
        states.put("confirm_ticket_creation", new ConfirmTicketCreationState(this));

        currentState = states.get("start");
    }

    public Dialog getCurrentDialog() {
        return currentDialog;
    }

    public void setCurrentDialog(Dialog currentDialog) {
        this.currentDialog = currentDialog;
    }

    public void setCurrentState(String stateName) {
        State state = states.get(stateName);
        if (state != null) {
            currentState = state;
        } else {
            throw new IllegalArgumentException("Не существует состояния \"" + stateName + "\"!");
        }
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(currentState);
        currentState.handleUpdate(update);
    }


    public ReplyKeyboardMarkup getStartCommandKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        // Создание одной строки для кнопок
        KeyboardRow row = new KeyboardRow();

        row.add(createButton("Все тикеты"));
        row.add(createButton("Создать тикет"));
        row.add(createButton("Мои тикеты"));

        keyboardMarkup.setKeyboard(List.of(row));

        return keyboardMarkup;
    }
    public KeyboardButton createButton(String text) {
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
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }
}
