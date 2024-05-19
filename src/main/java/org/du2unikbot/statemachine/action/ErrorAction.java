package org.du2unikbot.statemachine.action;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static org.du2unikbot.web.bot.constant.Strings.ERROR_TRANSITION;

public class ErrorAction implements Action<BotState, BotEvent> {
    @Override
    public void execute(final StateContext<BotState, BotEvent> context) {
        BotState target = context.getTarget().getId();
        String errorMessage = String.format(ERROR_TRANSITION, target);
        System.out.println(errorMessage);
    }
}
