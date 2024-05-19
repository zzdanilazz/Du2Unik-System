package org.du2unikbot.service;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class DefaultBotService implements BotService {
    private final StateMachinePersister<BotState, BotEvent, Long> persister;

    private final StateMachineFactory<BotState, BotEvent> stateMachineFactory;

    public DefaultBotService(
            StateMachinePersister<BotState, BotEvent, Long> persister,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory) {
        this.persister = persister;
        this.stateMachineFactory = stateMachineFactory;
    }

    @Override
    public void handleUpdate(Du2UnikBot du2UnikBot, Update update) {
        final StateMachine<BotState, BotEvent> stateMachine = stateMachineFactory.getStateMachine();

        try {
            if (update.hasCallbackQuery()) {
                final long userId = update.getCallbackQuery().getFrom().getId();
                persister.restore(stateMachine, userId);

                BotState botState = stateMachine.getState().getId();
                botState.handleCallbackQuery(du2UnikBot, update.getCallbackQuery(), stateMachineFactory, persister);
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                final long userId = update.getMessage().getFrom().getId();
                persister.restore(stateMachine, userId);

                BotState botState = stateMachine.getState().getId();
                botState.handleMessage(du2UnikBot, update.getMessage(), stateMachineFactory, persister);
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
