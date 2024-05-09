package org.du2unikbot.dialogs;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.HashMap;
import java.util.Map;

public class TicketCreationDialog extends Dialog {

    @Override
    public Map<String, SendMessage> initializeDialogMessages() {
        Map<String, SendMessage> replyOptions = new HashMap<>();
        String[] startPoints = new String[]{"ДУ", "Двойка", "ФФ"};
        String[] finishPoints =  new String[]{"Двойка", "ФФ"};
        String[] availableSeats =  new String[]{"1", "2", "3"};
        String[] confirmation =  new String[]{"Да", "Нет"};

        String[] startPointsCallbacks = new String[]{"start_du", "start_2", "start_ff"};
        String[] finishPointsCallbacks = new String[]{"finish_2", "finish_ff"};
        String[] availableSeatsCallbacks = new String[]{"seats_1", "seats_2", "seats_3"};
        String[] confirmationCallbacks = new String[]{"confirm_yes", "confirm_no"};

        // Создание вариантов ответа для точки старта
        replyOptions.put("start_point", createMessage("Укажите точку старта:", startPoints, startPointsCallbacks));

        // Создание вариантов ответа для точки финиша
        replyOptions.put("finish_point", createMessage("Укажите точку финиша:", finishPoints, finishPointsCallbacks));

        // Создание вариантов ответа для количества забронированных мест
        replyOptions.put("available_seats", createMessage("Укажите количество забронированных мест:", availableSeats, availableSeatsCallbacks));

        // Запрос ввода времени встречи
        SendMessage meetingTimeRequest = new SendMessage();
        meetingTimeRequest.setText("Укажите время (ЧЧ:ММ):");

        // Создание клавиатуры с кнопками для выбора времени
        meetingTimeRequest.setReplyMarkup(new ForceReplyKeyboard());
        replyOptions.put("meeting_time", meetingTimeRequest);

        // Создание вариантов ответа для подтверждения
        replyOptions.put("confirmation", createMessage("Подтвердить введенные данные?", confirmation, confirmationCallbacks));

        return replyOptions;
    }
}
