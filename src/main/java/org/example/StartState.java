package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartState extends State {
    public StartState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleMessage(Message message) {
        // Инициализируем переменные
        super.handleMessage(message);

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Здравствуйте, " + username);
        sm.setReplyMarkup(du2UnikBot.getStartCommandKeyboard());
        try {
            du2UnikBot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        du2UnikBot.setCurrentState("main_menu");
    }
}
