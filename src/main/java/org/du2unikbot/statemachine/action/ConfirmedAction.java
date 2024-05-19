package org.du2unikbot.statemachine.action;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.du2unikbot.web.bot.constant.Strings;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static org.du2unikbot.statemachine.StateMachineKey.CURRENT_TICKET;

public class ConfirmedAction implements Action<BotState, BotEvent> {
    @Override
    public void execute(final StateContext<BotState, BotEvent> context) {
        final Ticket ticket = context.getExtendedState().get(CURRENT_TICKET, Ticket.class);
        int id = ticket.getId();
        String actionMessage = String.format(Strings.TICKET_CONFIRMATION_SUCCESS, id);
        System.out.println(actionMessage);
    }
}
