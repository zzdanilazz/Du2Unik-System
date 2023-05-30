package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ConfirmTicketCreationState extends State {
    public ConfirmTicketCreationState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        switch (callbackData){
            case "confirm_yes" -> handleTicketConfirmation(true);
            case "confirm_no" -> handleTicketConfirmation(false);
        }
    }

    public void handleTicketConfirmation(boolean confirmed) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        du2UnikBot.setCurrentState("main_menu");

        if (confirmed) {
            message.setText("Ваш тикет успешно создан");
            du2UnikBot.getTickets().put(du2UnikBot.getCurrentTicket().getId(), du2UnikBot.getCurrentTicket());
            du2UnikBot.setCurrentTicket(new Ticket());
        } else {
            message.setText("Ваш тикет не создан!");
        }
        message.setReplyMarkup(du2UnikBot.getStartCommandKeyboard());
        try {
            du2UnikBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        // Удаление после подтверждения
        du2UnikBot.deleteMessage(chatId, messageId);
    }

}
