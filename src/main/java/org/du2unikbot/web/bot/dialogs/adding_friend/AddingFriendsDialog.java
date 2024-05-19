package org.du2unikbot.web.bot.dialogs.adding_friend;

import org.du2unikbot.web.bot.constant.CallbackButton;
import org.du2unikbot.web.bot.dialogs.Dialog;
import org.du2unikbot.web.bot.dialogs.DialogKey;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;

import static org.du2unikbot.web.bot.constant.CallbackButton.*;
import static org.du2unikbot.web.bot.constant.Strings.ADDING_FRIENDS_TITLE;
import static org.du2unikbot.web.bot.dialogs.adding_friend.AddingFriendsDialogKey.THREE_FREE_SEATS;
import static org.du2unikbot.web.bot.dialogs.adding_friend.AddingFriendsDialogKey.TWO_FREE_SEATS;

public class AddingFriendsDialog extends Dialog {
    @Override
    public Map<DialogKey, SendMessage> initializeDialogMessages() {
        Map<DialogKey, SendMessage> replyOptions = new HashMap<>();

        CallbackButton[] forTwoFreeSeatsCallbackButtons = new CallbackButton[]{FRIENDS_0, FRIENDS_1};
        CallbackButton[] forThreeFreeSeatsCallbackButtons = new CallbackButton[]{FRIENDS_0, FRIENDS_1, FRIENDS_2};

        // Создание вариантов ответа для двух свободных мест
        replyOptions.put(TWO_FREE_SEATS, createMessage(ADDING_FRIENDS_TITLE, forTwoFreeSeatsCallbackButtons));

        // Создание вариантов ответа для трех свободных мест
        replyOptions.put(THREE_FREE_SEATS, createMessage(ADDING_FRIENDS_TITLE, forThreeFreeSeatsCallbackButtons));

        return replyOptions;
    }
}
