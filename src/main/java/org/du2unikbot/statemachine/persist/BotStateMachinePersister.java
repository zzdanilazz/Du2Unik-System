package org.du2unikbot.statemachine.persist;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;

public class BotStateMachinePersister implements StateMachinePersist<BotState, BotEvent, Long> {

    private final HashMap<Long, StateMachineContext<BotState, BotEvent>> contexts = new HashMap<>();

    @Override
    public void write(final StateMachineContext<BotState, BotEvent> context, final Long contextObj) {
        contexts.put(contextObj, context);
    }

    @Override
    public StateMachineContext<BotState, BotEvent> read(final Long contextObj) {
        return contexts.get(contextObj);
    }
}