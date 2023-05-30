package trash;

import org.example.Du2UnikBot;
import org.example.Ticket;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import trash.Handleable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CallbackHandler implements Handleable {
    protected long chatId;
    protected String callbackData;
    protected String messageText;
    protected int messageId;
    protected String username;
    protected Du2UnikBot du2UnikBot;

    public CallbackHandler(Du2UnikBot du2UnikBot) {
        this.du2UnikBot = du2UnikBot;
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        this.chatId = callbackQuery.getMessage().getChatId();
        this.callbackData = callbackQuery.getData();
        this.messageText = callbackQuery.getMessage().getText();
        this.messageId = callbackQuery.getMessage().getMessageId();
        this.username = callbackQuery.getFrom().getUserName();
    }

    public int extractIdFromMessage(String messageText) {
        int id = 0;
        String regexPattern = "ID: " + "(\\d+)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.find()) {
            String idString = matcher.group(1);
            id = Integer.parseInt(idString);
        }

        return id;
    }

    public void addUsernameToMessage(long chatId, int messageId, String messageText, String username, int reservedSeats) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        // Добавляем username пользователя в текст сообщения
        String newMessageText = messageText + "\n" + "@" + username;

        // Изменяем количество свободных мест в сообщении
        newMessageText = updateFreeSeats(newMessageText, 4 - reservedSeats);
        editMessageText.setText(newMessageText);

        // Установка клавиатуры с кнопкой "Удалить"
        editMessageText.setReplyMarkup(Ticket.createDeleteButtonKeyboard());

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteUsernameFromMessage(long chatId, int messageId, String messageText, String username, int reservedSeats) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        // Удаляем username пользователя из сообщения
        String newMessageText = messageText.replace("\n" + "@" + username, "");

        // Изменяем количество свободных мест в сообщении
        newMessageText = updateFreeSeats(newMessageText, 4 - reservedSeats);
        editMessageText.setText(newMessageText);

        // Установка клавиатуры с кнопкой "Выбрать"
        editMessageText.setReplyMarkup(Ticket.createSelectButtonKeyboard());

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String updateFreeSeats(String messageText, int reservedSeats) {
        String regexPattern = "(?<=Свободных мест: )\\d+";
        String replacementString = String.valueOf(reservedSeats);
        return messageText.replaceAll(regexPattern, replacementString);
    }

    public void addChosenAndRemoveKeyboard(long chatId, String messageText, String buttonText, int messageId) {
        // Создание экземпляра класса EditMessageText
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(messageText + " " + buttonText);

        // Удаление клавиатуры (установка значения null)
        editMessageText.setReplyMarkup(null);

        try {
            // Отправка запроса на изменение сообщения
            du2UnikBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
