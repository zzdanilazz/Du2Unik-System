package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import trash.CallbackHandler;

public class SelectStartPointState extends State {
    public SelectStartPointState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        switch (callbackData){
            case "start_du" -> handleStartPointSelection("ДУ", messageText, chatId, messageId);
            case "start_2" -> handleStartPointSelection("Двойка", messageText, chatId, messageId);
            case "start_ff" -> handleStartPointSelection("ФФ", messageText, chatId, messageId);
        }
    }
    public void handleStartPointSelection(String startPoint, String messageText, long chatId, int messageId) {
        System.out.println(du2UnikBot.getCurrentTicket().getStartPoint());
        // Изменение сообщения с учетом выбора
        addChosenAndRemoveKeyboard(chatId, messageText, startPoint, messageId);

        du2UnikBot.getCurrentTicket().setStartPoint(startPoint);

        if (startPoint.equals("ДУ")){
            // Если точка старта ДУ, то точка финиша может быть любым университетом
            du2UnikBot.setCurrentState("select_finish_point");

            SendMessage request = du2UnikBot.getTicketCreationForm().getFormMessages().get("finish_point");
            request.setChatId(chatId);
            try {
                du2UnikBot.execute(request);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Если точка старта не ДУ, то точка финиша точно будет ДУ
            du2UnikBot.setCurrentState("select_reserved_seats");

            du2UnikBot.sendMessage(chatId, "Укажите точку финиша: ДУ");
            du2UnikBot.getCurrentTicket().setFinishPoint("ДУ");

            SendMessage request = du2UnikBot.getTicketCreationForm().getFormMessages().get("available_seats");

            request.setChatId(chatId);
            try {
                du2UnikBot.execute(request);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
