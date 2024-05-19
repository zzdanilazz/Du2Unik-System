package org.du2unikbot.web.bot.dialogs.ticket_creation;

import org.du2unikbot.web.bot.constant.CallbackButton;
import org.du2unikbot.web.bot.dialogs.Dialog;
import org.du2unikbot.web.bot.dialogs.DialogKey;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

import java.util.HashMap;
import java.util.Map;

import static org.du2unikbot.web.bot.constant.CallbackButton.*;
import static org.du2unikbot.web.bot.constant.Strings.*;
import static org.du2unikbot.web.bot.dialogs.ticket_creation.TicketCreationDialogKey.*;

public class TicketCreationDialog extends Dialog {
    @Override
    public Map<DialogKey, SendMessage> initializeDialogMessages() {
        Map<DialogKey, SendMessage> replyOptions = new HashMap<>();

        CallbackButton[] startPointsCallbackButtons = new CallbackButton[]{START_DU, START_DVOYKA, START_FF};
        CallbackButton[] finishPointsCallbackButtons = new CallbackButton[]{FINISH_DVOYKA, FINISH_FF};
        CallbackButton[] availableSeatsCallbackButtons = new CallbackButton[]{SEATS_1, SEATS_2, SEATS_3};
        CallbackButton[] confirmationCallbackButtons = new CallbackButton[]{CONFIRM_TICKET_CREATION, NOT_CONFIRM_TICKET_CREATION};

        // Создание вариантов ответа для точки старта
        replyOptions.put(START_POINT, createMessage(START_POINT_SELECTION_TITLE, startPointsCallbackButtons));

        // Создание вариантов ответа для точки финиша
        replyOptions.put(FINISH_POINT, createMessage(FINISH_POINT_SELECTION_TITLE, finishPointsCallbackButtons));

        // Создание вариантов ответа для количества забронированных мест
        replyOptions.put(AVAILABLE_SEATS, createMessage(RESERVED_SEATS_SELECTION_TITLE, availableSeatsCallbackButtons));

        // Запрос ввода времени встречи
        SendMessage meetingTimeRequest = new SendMessage();
        meetingTimeRequest.setText(MEETING_TIME_SELECTION_TITLE);
        // Создание клавиатуры для ответа на сообщение
        meetingTimeRequest.setReplyMarkup(new ForceReplyKeyboard());
        replyOptions.put(MEETING_TIME, meetingTimeRequest);

        // Создание вариантов ответа для подтверждения
        replyOptions.put(CONFIRMATION, createMessage(TICKET_CONFIRMATION_TITLE, confirmationCallbackButtons));

        return replyOptions;
    }
}
