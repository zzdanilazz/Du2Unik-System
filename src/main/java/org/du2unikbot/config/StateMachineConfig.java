package org.du2unikbot.config;

import org.du2unikbot.statemachine.action.ConfirmedAction;
import org.du2unikbot.statemachine.action.ErrorAction;
import org.du2unikbot.statemachine.event.BotEvent;
import org.du2unikbot.statemachine.listener.BotStateMachineApplicationListener;
import org.du2unikbot.statemachine.persist.BotStateMachinePersister;
import org.du2unikbot.statemachine.state.BotState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

import static org.du2unikbot.statemachine.event.BotEvent.*;
import static org.du2unikbot.statemachine.state.BotState.*;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<BotState, BotEvent> {

    @Override
    public void configure(final StateMachineConfigurationConfigurer<BotState, BotEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(new BotStateMachineApplicationListener())
        ;
    }

    @Override
    public void configure(final StateMachineStateConfigurer<BotState, BotEvent> states) throws Exception {
        states
            .withStates()
            .initial(GREETINGS)
            .end(END)
            .states(EnumSet.allOf(BotState.class));
    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<BotState, BotEvent> transitions) throws Exception {
        transitions
                // Переход на главное меню после приветствия
                .withExternal()
                .source(GREETINGS)
                .target(MAIN)
                .event(START)
                .action(errorAction())

                // Переход к добавлению друга в тикет после выбора тикета
                .and()
                .withExternal()
                .source(MAIN)
                .target(ADDING_FRIENDS)
                .event(SELECTED_TICKET)
                .action(errorAction())

                // Переход на главное меню с добавлением 1 друга в тикет
                .and()
                .withExternal()
                .source(ADDING_FRIENDS)
                .target(MAIN)
                .event(ADDED_ONE_FRIEND)
                .action(errorAction())

                // Переход на главное меню с добавлением 2 друзей в тикет
                .and()
                .withExternal()
                .source(ADDING_FRIENDS)
                .target(MAIN)
                .event(ADDED_TWO_FRIENDS)
                .action(errorAction())

                // Переход на главное меню без добавлением друга в тикет
                .and()
                .withExternal()
                .source(ADDING_FRIENDS)
                .target(MAIN)
                .event(NOT_ADDED_FRIEND)
                .action(errorAction())

                // Переход на главное меню после отмены добавления друзей в тикет
                .and()
                .withExternal()
                .source(ADDING_FRIENDS)
                .target(MAIN)
                .event(CANCELLED_ADDING_FRIENDS)
                .action(errorAction())

                // Переход к выбору точки старта при создании тикета
                .and()
                .withExternal()
                .source(MAIN)
                .target(START_POINT_SELECTION)
                .event(CREATED_TICKET)
                .action(errorAction())

                // Переход к точке финиша при выборе точки старта "ДУ"
                .and()
                .withExternal()
                .source(START_POINT_SELECTION)
                .target(FINISH_POINT_SELECTION)
                .event(SELECTED_START_DU)
                .action(errorAction())

                // Переход к выбору зарезервированных мест при выборе точки старта "Двойка"
                .and()
                .withExternal()
                .source(START_POINT_SELECTION)
                .target(RESERVED_SEATS_SELECTION)
                .event(SELECTED_START_DVOYKA)
                .action(errorAction())

                // Переход к выбору зарезервированных мест при выборе точки старта "ФФ"
                .and()
                .withExternal()
                .source(START_POINT_SELECTION)
                .target(RESERVED_SEATS_SELECTION)
                .event(SELECTED_START_FF)
                .action(errorAction())

                // Переход на главное меню после отмены выбора точки старта
                .and()
                .withExternal()
                .source(START_POINT_SELECTION)
                .target(MAIN)
                .event(CANCELLED_START_POINT_SELECTION)
                .action(errorAction())

                // Переход к выбору зарезервированных мест при выборе точки финиша "Двойка"
                .and()
                .withExternal()
                .source(FINISH_POINT_SELECTION)
                .target(RESERVED_SEATS_SELECTION)
                .event(SELECTED_FINISH_DVOYKA)
                .action(errorAction())

                // Переход к выбору зарезервированных мест при выборе точки финиша "ФФ"
                .and()
                .withExternal()
                .source(FINISH_POINT_SELECTION)
                .target(RESERVED_SEATS_SELECTION)
                .event(SELECTED_FINISH_FF)
                .action(errorAction())

                // Переход на главное меню после отмены выбора точки финиша
                .and()
                .withExternal()
                .source(FINISH_POINT_SELECTION)
                .target(MAIN)
                .event(CANCELLED_FINISH_POINT_SELECTION)
                .action(errorAction())

                // Переход к выбору времени встречи после выбора одного зарезервированного места
                .and()
                .withExternal()
                .source(RESERVED_SEATS_SELECTION)
                .target(MEETING_TIME_SELECTION)
                .event(RESERVED_ONE_SEAT)
                .action(errorAction())

                // Переход к выбору времени встречи после выбора двух зарезервированных мест
                .and()
                .withExternal()
                .source(RESERVED_SEATS_SELECTION)
                .target(MEETING_TIME_SELECTION)
                .event(RESERVED_TWO_SEATS)
                .action(errorAction())

                // Переход к выбору времени встречи после выбора трех зарезервированных мест
                .and()
                .withExternal()
                .source(RESERVED_SEATS_SELECTION)
                .target(MEETING_TIME_SELECTION)
                .event(RESERVED_THREE_SEATS)
                .action(errorAction())

                // Переход на главное меню после отмены выбора зарезервированных мест
                .and()
                .withExternal()
                .source(RESERVED_SEATS_SELECTION)
                .target(MAIN)
                .event(CANCELLED_RESERVED_SEATS_SELECTION)
                .action(errorAction())

                // Переход к подтверждению тикета после выборе времени встречи
                .and()
                .withExternal()
                .source(MEETING_TIME_SELECTION)
                .target(TICKET_CONFIRMATION)
                .event(SELECTED_TIME)
                .action(errorAction())

                // Переход на главное меню после отмены выбора времени встречи
                .and()
                .withExternal()
                .source(MEETING_TIME_SELECTION)
                .target(MAIN)
                .event(CANCELLED_MEETING_TIME_SELECTION)
                .action(errorAction())

                // Переход на главное меню после подтверждения тикета
                .and()
                .withExternal()
                .source(TICKET_CONFIRMATION)
                .target(MAIN)
                .event(CONFIRMED_TICKET)
                .action(confirmedAction(), errorAction())

                // Переход на главное меню после не подтверждения тикета
                .and()
                .withExternal()
                .source(TICKET_CONFIRMATION)
                .target(MAIN)
                .event(NOT_CONFIRMED_TICKET)
                .action(errorAction())

                // Переход на главное меню после отмены подтверждения тикета
                .and()
                .withExternal()
                .source(TICKET_CONFIRMATION)
                .target(MAIN)
                .event(CANCELLED_TICKET_CONFIRMATION)
                .action(errorAction())
        ;
    }

    @Bean
    public Action<BotState, BotEvent> confirmedAction() {
        return new ConfirmedAction();
    }

    @Bean
    public Action<BotState, BotEvent> errorAction() {
        return new ErrorAction();
    }

    @Bean
    public StateMachinePersister<BotState, BotEvent, Long> persister() {
        return new DefaultStateMachinePersister<>(new BotStateMachinePersister());
    }
}
