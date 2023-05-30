package trash;

import org.example.Du2UnikBot;
import org.example.Ticket;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class AfterShowTicketsState extends CallbackHandler {
    public AfterShowTicketsState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        int ticketId = extractIdFromMessage(messageText);

        switch (callbackData) {
            case "select" -> updateUserInTicket(du2UnikBot.getTickets().get(ticketId), ticketId, messageText, true);
            case "delete" -> updateUserInTicket(du2UnikBot.getTickets().get(ticketId), ticketId, messageText, false);
        }
    }
    private void updateUserInTicket(Ticket ticket, int ticketId, String messageText, boolean addingUser) {
        if (ticket != null) {
            if (addingUser) {
                if (messageText.contains(username)) {
                    du2UnikBot.sendMessage(chatId, "Вы уже записаны в тикет с ID: " + ticketId + "!");
                } else {
                    // Добавление пользователя в тикет
                    ticket.getUsernames().add(username);

                    du2UnikBot.sendMessage(chatId, "Вы успешно выбрали тикет с ID: " + ticketId);

                    // Изменение числа забронированных мест в тикете
                    ticket.setReservedSeats(ticket.getReservedSeats() + 1);

                    addUsernameToMessage(chatId, messageId, messageText, username, ticket.getReservedSeats());
                }
            } else {
                // Удаление пользователя из тикета
                ticket.getUsernames().remove(username);

                // Изменение числа забронированных мест в тикете
                ticket.setReservedSeats(ticket.getReservedSeats() - 1);

                if (ticket.getUsernames().isEmpty()) {
                    // Если в тикете больше нет пользователей, удаляем тикет и сообщение
                    du2UnikBot.getTickets().remove(ticketId);
                    du2UnikBot.deleteMessage(chatId, messageId);
                } else {
                    // Удаление пользователя из сообщения
                    deleteUsernameFromMessage(chatId, messageId, messageText, username, ticket.getReservedSeats());
                }

                du2UnikBot.sendMessage(chatId, "Ваш тикет с ID: " + ticketId + " успешно удален");
            }
        } else {
            du2UnikBot.sendMessage(chatId, "Тикет с ID: " + ticketId + " не найден");
        }
    }
}
