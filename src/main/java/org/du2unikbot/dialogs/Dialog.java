package org.du2unikbot.dialogs;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Dialog {
    protected Map<String, SendMessage> dialogMessage;

    public Map<String, SendMessage> getDialogMessages() {
        return dialogMessage;
    }

    public void setDialogMessages(Map<String, SendMessage> dialogMessages) {
        this.dialogMessage = dialogMessages;
    }

    protected Dialog() {
        this.dialogMessage = initializeDialogMessages();
    }

    public abstract Map<String, SendMessage> initializeDialogMessages();
    public InlineKeyboardMarkup createReplyOptionsKeyboard(String[] options, String[] callbacks) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создание одной строки для кнопок
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            String callback = callbacks[i];

            InlineKeyboardButton button = new InlineKeyboardButton(option);
            button.setCallbackData(callback);

            row.add(button);
        }

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public SendMessage createMessage(String messageText, String[] options, String[] callbacks) {
        InlineKeyboardMarkup keyboardMarkup = createReplyOptionsKeyboard(options, callbacks);

        SendMessage message = new SendMessage();
        message.setParseMode("HTML");
        message.setText(messageText);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
