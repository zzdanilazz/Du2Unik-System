package org.du2unikbot.statemachine.event;

public enum BotEvent {
   START,
   CREATED_TICKET,
   SELECTED_START_DU,
   SELECTED_START_DVOYKA,
   SELECTED_START_FF,
   SELECTED_FINISH_DVOYKA,
   SELECTED_FINISH_FF,
   RESERVED_ONE_SEAT,
   RESERVED_TWO_SEATS,
   RESERVED_THREE_SEATS,
   SELECTED_RESERVED_SEATS,
   SELECTED_TIME,
   CONFIRMED_TICKET,
   NOT_CONFIRMED_TICKET,
   SELECTED_TICKET,
   ADDED_ONE_FRIEND,
   ADDED_TWO_FRIENDS,
   NOT_ADDED_FRIEND,
   CANCELLED_START_POINT_SELECTION,
   CANCELLED_FINISH_POINT_SELECTION,
   CANCELLED_RESERVED_SEATS_SELECTION,
   CANCELLED_MEETING_TIME_SELECTION,
   CANCELLED_TICKET_CONFIRMATION,
   CANCELLED_ADDING_FRIENDS,
}
