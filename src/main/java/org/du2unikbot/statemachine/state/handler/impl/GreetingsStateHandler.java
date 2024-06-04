package org.du2unikbot.statemachine.state.handler.impl;

import org.du2unikbot.entities.User;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.du2unikbot.web.bot.constant.Strings;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.du2unikbot.statemachine.event.BotEvent.START;
import static org.du2unikbot.util.TicketStringFormatter.bold;
import static org.du2unikbot.web.bot.constant.Strings.*;

public class GreetingsStateHandler implements StateHandler {
    private Du2UnikBot du2UnikBot;

    @Override
    public void handleMessage(
            Du2UnikBot du2UnikBot,
            Message message, StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister
    ) {
        this.du2UnikBot = du2UnikBot;
        final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();

        long chatId = message.getChatId();

        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        String userName = message.getFrom().getUserName();
        String greeting = String.format(Strings.BOT_GREETING, userName);
        sm.setText(greeting);
        sm.setReplyMarkup(du2UnikBot.getStartCommandKeyboard());

        final long userId = message.getFrom().getId();

        User user = new User(message.getFrom().getId(), message.getFrom().getUserName(), chatId, 0);
        //Сохранение пользователя в бд
        du2UnikBot.getUserRepository().save(user);

        try {
            du2UnikBot.execute(sm);
            du2UnikBot.sendMessage(chatId, BOT_DESCRIPTION);
            du2UnikBot.sendMessage(chatId, bold(BOT_TERMS_TITLE) + "\n" + BOT_TERMS);
            du2UnikBot.sendMessage(chatId, bold(BOT_RULES_TITLE) + "\n" + BOT_RULES);
            stateMachine.sendEvent(START);
            persister.persist(stateMachine, userId);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleCallbackQuery(Du2UnikBot du2UnikBot, CallbackQuery callbackQuery, StateMachineFactory<BotState, BotEvent> stateMachineFactory, StateMachinePersister<BotState, BotEvent, Long> persister) {
        /* Пока данное состояние не поддерживает обработку коллбеков */
    }
}
