package org.du2unikbot.statemachine.state.handler;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface StateHandler {
    void handleMessage(
            Du2UnikBot du2UnikBot,
            Message message,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister);
    void handleCallbackQuery(
            Du2UnikBot du2UnikBot,
            CallbackQuery callbackQuery,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister);
}
