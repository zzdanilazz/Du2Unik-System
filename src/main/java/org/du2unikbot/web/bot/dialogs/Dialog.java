package org.du2unikbot.web.bot.dialogs;

import org.du2unikbot.web.bot.constant.CallbackButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.du2unikbot.web.bot.constant.Strings.HTML_PARSE_MODE;

public abstract class Dialog {
    protected Map<DialogKey, SendMessage> dialogMessages;

    public Map<DialogKey, SendMessage> getDialogMessages() {
        return dialogMessages;
    }

    public void setDialogMessages(Map<DialogKey, SendMessage> dialogMessages) {
        this.dialogMessages = dialogMessages;
    }

    protected Dialog() {
        this.dialogMessages = initializeDialogMessages();
    }

    public abstract Map<DialogKey, SendMessage> initializeDialogMessages();
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

    public SendMessage createMessage(String messageText, CallbackButton[] callbackButtons) {
        String[] options = Arrays.stream(callbackButtons)
                .map(CallbackButton::getOption)
                .toArray(String[]::new);

        String[] callbacks = Arrays.stream(callbackButtons)
                .map(CallbackButton::getCallback)
                .toArray(String[]::new);

        InlineKeyboardMarkup keyboardMarkup = createReplyOptionsKeyboard(options, callbacks);

        SendMessage message = new SendMessage();
        message.setParseMode(HTML_PARSE_MODE);
        message.setText(messageText);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
