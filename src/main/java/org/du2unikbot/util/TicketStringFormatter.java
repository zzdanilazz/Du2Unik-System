package org.du2unikbot.util;

import org.du2unikbot.entities.Ticket;
import org.du2unikbot.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.du2unikbot.web.bot.constant.Strings.*;

public class TicketStringFormatter {
    public static String bold(String text) {
        return "<b>" + text + "</b>";
    }

    public static String formatTicketMessage(Ticket ticket) {
        StringBuilder messageText = new StringBuilder();
        messageText.append(bold(ID_ITEM)).append(ticket.getId()).append("\n");
        messageText.append(bold(START_ITEM)).append(ticket.getStartPoint()).append("\n");
        messageText.append(bold(FINISH_ITEM)).append(ticket.getFinishPoint()).append("\n");
        if (ticket.getReservedSeats() != 4) {
            messageText.append(bold(FREE_SEATS_ITEM)).append(4 - ticket.getReservedSeats()).append("\n");
        }
        messageText.append(bold(MEETING_TIME_ITEM))
                .append(ticket.getMeetingTime().format(DateTimeFormatter.ofPattern(TIME_PATTERN))).append("\n");
        messageText.append(bold(PARTICIPANTS_ITEM)).append("\n");
        for (User user: ticket.getUsers()) {
            messageText
                    .append("\n").append("@")
                    .append(user.getUsername()).append(determineFriends(user.getFriendsCount()));
        }

        return messageText.toString();
    }

    private static String determineFriends(int count) {
        return switch (count) {
            case 0 -> "";
            case 1 -> " и 1 его друг";
            case 2 -> " и 2 его друга";
            default -> throw new IllegalStateException("Unexpected value: " + count);
        };
    }

    public static int extractIdFromMessage(String messageText) {
        int id = 0;
        String regexPattern = ID_ITEM + "(\\d+)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.find()) {
            String idString = matcher.group(1);
            id = Integer.parseInt(idString);
        }

        return id;
    }
}
