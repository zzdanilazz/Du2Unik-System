package org.du2unikbot.states;

import org.du2unikbot.Du2UnikBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SelectFinishPointState extends State {
    public SelectFinishPointState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        switch (callbackData){
            case "finish_2" -> handleFinishPointSelection("Двойка", messageText, chatId, messageId);
            case "finish_ff" -> handleFinishPointSelection("ФФ", messageText, chatId, messageId);
            default -> throw new IllegalStateException("Unexpected value: " + callbackData);
        }
    }
    public void handleFinishPointSelection(String finishPoint, String messageText, long chatId, int messageId) {
        du2UnikBot.setCurrentState("select_reserved_seats");

        // Изменение сообщения с учетом выбора
        addChosenAndRemoveKeyboard(chatId, messageText, finishPoint, messageId);

        du2UnikBot.getCurrentTicket().setFinishPoint(finishPoint);

        SendMessage request = du2UnikBot.getCurrentDialog().getDialogMessages().get("available_seats");
        request.setChatId(chatId);
        try {
            du2UnikBot.execute(request);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
