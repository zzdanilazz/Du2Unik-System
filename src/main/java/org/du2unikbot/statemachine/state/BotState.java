package org.du2unikbot.statemachine.state;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.handler.StateHandler;
import org.du2unikbot.statemachine.state.handler.impl.*;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public enum BotState implements StateHandler {
    GREETINGS(new GreetingsStateHandler()),
    MAIN(new MainStateHandler()),
    START_POINT_SELECTION(new StartPointSelectionStateHandler()),
    FINISH_POINT_SELECTION(new FinishPointSelectionStateHandler()),
    RESERVED_SEATS_SELECTION(new ReservedSeatsSelectionStateHandler()),
    MEETING_TIME_SELECTION(new TimeSelectionStateHandler()),
    TICKET_CONFIRMATION(new TicketConfirmationStateHandler()),
    ADDING_FRIENDS(new AddingFriendsStateHandler()),
    END(null);

    private final StateHandler handler;

    BotState(StateHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleMessage(
            Du2UnikBot du2UnikBot,
            Message message,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister) {
        handler.handleMessage(du2UnikBot, message, stateMachineFactory, persister);
    }

    @Override
    public void handleCallbackQuery(
            Du2UnikBot du2UnikBot,
            CallbackQuery callbackQuery,
            StateMachineFactory<BotState, BotEvent> stateMachineFactory,
            StateMachinePersister<BotState, BotEvent, Long> persister) {
        handler.handleCallbackQuery(du2UnikBot, callbackQuery, stateMachineFactory, persister);
    }
}

