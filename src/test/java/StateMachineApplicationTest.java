import org.du2unikbot.Du2UnikApplication;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.du2unikbot.statemachine.event.BotEvent.*;
import static org.du2unikbot.statemachine.state.BotState.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Du2UnikApplication.class)
public class StateMachineApplicationTest {
    @Autowired
    private StateMachineFactory<BotState, BotEvent> factory;

    @Test
    public void testWhenAddingFriendsCancelled() throws Exception {
        StateMachine<BotState, BotEvent> machine = factory.getStateMachine();
        StateMachineTestPlan<BotState, BotEvent> plan =
                StateMachineTestPlanBuilder.<BotState, BotEvent>builder()
                        .defaultAwaitTime(2)
                        .stateMachine(machine)
                        .step().expectStates(GREETINGS).expectStateChanged(0)
                        .and().step().sendEvent(START).expectState(MAIN).expectStateChanged(1)
                        .and().step().sendEvent(SELECTED_TICKET).expectState(ADDING_FRIENDS).expectStateChanged(1)
                        .and().step().sendEvent(CANCELLED_ADDING_FRIENDS).expectState(MAIN).expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void testWhenStartPointSelectionCancelled() throws Exception {
        StateMachine<BotState, BotEvent> machine = factory.getStateMachine();
        StateMachineTestPlan<BotState, BotEvent> plan =
                StateMachineTestPlanBuilder.<BotState, BotEvent>builder()
                        .defaultAwaitTime(2)
                        .stateMachine(machine)
                        .step()
                        .expectStates(GREETINGS)
                        .expectStateChanged(0)
                        .and()
                        .step()
                        .sendEvent(START)
                        .expectState(MAIN)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(CREATED_TICKET)
                        .expectState(START_POINT_SELECTION)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(CANCELLED_START_POINT_SELECTION)
                        .expectState(MAIN)
                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void testWhenFinishPointSelectionCancelled() throws Exception {
        StateMachine<BotState, BotEvent> machine = factory.getStateMachine();
        StateMachineTestPlan<BotState, BotEvent> plan =
                StateMachineTestPlanBuilder.<BotState, BotEvent>builder()
                        .defaultAwaitTime(2)
                        .stateMachine(machine)
                        .step()
                        .expectStates(GREETINGS)
                        .expectStateChanged(0)
                        .and()
                        .step()
                        .sendEvent(START)
                        .expectState(MAIN)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(CREATED_TICKET)
                        .expectState(START_POINT_SELECTION)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(SELECTED_START_DU)
                        .expectState(FINISH_POINT_SELECTION)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(CANCELLED_FINISH_POINT_SELECTION)
                        .expectState(MAIN)
                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
    }
}
