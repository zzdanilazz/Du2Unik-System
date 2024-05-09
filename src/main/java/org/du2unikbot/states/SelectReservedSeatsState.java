package org.du2unikbot.states;

import org.du2unikbot.Du2UnikBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SelectReservedSeatsState extends State {
    public SelectReservedSeatsState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        switch (callbackData){
            case "seats_1" -> handleReservedSeatsSelection(1, messageText, chatId, messageId);
            case "seats_2" -> handleReservedSeatsSelection(2, messageText, chatId, messageId);
            case "seats_3" -> handleReservedSeatsSelection(3, messageText, chatId, messageId);
            default -> throw new IllegalStateException("Unexpected value: " + callbackData);
        }
    }
    public void handleReservedSeatsSelection(int reservedSeats, String messageText, long chatId, int messageId) {
        du2UnikBot.setCurrentState("enter_meeting_time");

        // Изменение сообщения с учетом выбора
        addChosenAndRemoveKeyboard(chatId, messageText, String.valueOf(reservedSeats), messageId);

        du2UnikBot.getCurrentTicket().setReservedSeats(reservedSeats);

        SendMessage request = du2UnikBot.getCurrentDialog().getDialogMessages().get("meeting_time");
        request.setChatId(chatId);
        try {
            du2UnikBot.execute(request);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
