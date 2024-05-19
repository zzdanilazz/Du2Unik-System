package org.du2unikbot.statemachine.listener;

import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.state.BotState;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

public class BotStateMachineApplicationListener implements StateMachineListener<BotState, BotEvent> {
    @Override
    public void stateChanged(final State<BotState, BotEvent> from, final State<BotState, BotEvent> to) {
        if (from != null && from.getId() != null) {
            System.out.println("Переход из статуса " + from.getId() + " в статус " + to.getId());
        } else {
            System.out.println("Начальное состояние или неинициализированное состояние. Переход в статус " + to.getId());
        }
    }

    @Override
    public void stateEntered(final State<BotState, BotEvent> state) {

    }

    @Override
    public void stateExited(final State<BotState, BotEvent> state) {

    }

    @Override
    public void eventNotAccepted(final Message<BotEvent> event) {
        System.out.println("Событие не принято " + event);
    }

    @Override
    public void transition(final Transition<BotState, BotEvent> transition) {

    }

    @Override
    public void transitionStarted(final Transition<BotState, BotEvent> transition) {

    }

    @Override
    public void transitionEnded(final Transition<BotState, BotEvent> transition) {

    }

    @Override
    public void stateMachineStarted(final StateMachine<BotState, BotEvent> stateMachine) {
        System.out.println("Machine started");
    }

    @Override
    public void stateMachineStopped(final StateMachine<BotState, BotEvent> stateMachine) {

    }

    @Override
    public void stateMachineError(final StateMachine<BotState, BotEvent> stateMachine, Exception exception) {
    }

    @Override
    public void extendedStateChanged(final Object key, final Object value) {

    }

    @Override
    public void stateContext(final StateContext<BotState, BotEvent> stateContext) {

    }
}
