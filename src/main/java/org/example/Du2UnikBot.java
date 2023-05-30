package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import trash.DeleteTicketState;
import trash.SelectTicketState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Du2UnikBot extends TelegramLongPollingBot {
    private Map<Integer, Ticket> tickets;
    private Ticket currentTicket = new Ticket();
    private Form ticketCreationForm;
    private final Map<String, State> states;
    private State currentState;

    public Du2UnikBot() {
        tickets = Ticket.initializeTickets();  // Создание тикетов
        states = new HashMap<>();

        // Добавление состояний
        states.put("start", new StartState(this));
        states.put("main_menu", new MainMenuState(this));

        states.put("select_ticket", new SelectTicketState(this));
        states.put("delete_ticket", new DeleteTicketState(this));

        states.put("select_start_point", new SelectStartPointState(this));
        states.put("select_finish_point", new SelectFinishPointState(this));
        states.put("select_reserved_seats", new SelectReservedSeatsState(this));
        states.put("enter_meeting_time", new EnterMeetingTimeState(this));
        states.put("confirm_ticket_creation", new ConfirmTicketCreationState(this));

        currentState = states.get("start");
    }

    public Map<Integer, Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Map<Integer, Ticket> tickets) {
        this.tickets = tickets;
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        this.currentTicket = currentTicket;
    }

    public Form getTicketCreationForm() {
        return ticketCreationForm;
    }

    public void setTicketCreationForm(Form ticketCreationForm) {
        this.ticketCreationForm = ticketCreationForm;
    }


    public void setCurrentState(String stateName) {
        State state = states.get(stateName);
        if (state != null) {
            currentState = state;
        } else {
            throw new IllegalArgumentException("Не существует состояния \"" + stateName + "\"!");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(currentState);
        currentState.handleUpdate(update);

        // Проверяем тикеты на наличие 4 человек
//        checkTicketsForFull(update.getMessage().getChatId(), update.getMessage().getFrom().getUserName());
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

    @Override
    public String getBotUsername() {
        // Возвращаем имя бота
        return "Du2UnikBot";
    }

    @Override
    public String getBotToken() {
        // Возвращаем токен бота
        return "6082124596:AAE0Zqq7cwgJ9p7nJHCLdd7HnQVnyHFZZ3Q";
    }

}
