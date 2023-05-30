package trash;

import org.example.Du2UnikBot;
import org.example.State;
import org.example.Ticket;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class DeleteTicketState extends State {
    public DeleteTicketState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        int ticketId = extractIdFromMessage(messageText);

        Ticket deletingTicket = du2UnikBot.getTickets().get(ticketId);

        if (deletingTicket != null) {
            // Удаление пользователя из тикета
            deletingTicket.getUsernames().remove(username);

            // Изменение числа забронированных мест в тикете
            deletingTicket.setReservedSeats(deletingTicket.getReservedSeats() - 1);

            if (deletingTicket.getUsernames().isEmpty()) {
                // Если в тикете больше нет пользователей, удаляем тикет и сообщение
                du2UnikBot.getTickets().remove(ticketId);
                du2UnikBot.deleteMessage(chatId, messageId);
            } else {
                // Удаление пользователя из сообщения
                deleteUsernameFromMessage(chatId, messageId, messageText, username, deletingTicket.getReservedSeats());
            }

            du2UnikBot.sendMessage(chatId, "Ваш тикет с ID: " + ticketId + " успешно удален");
        } else {
            du2UnikBot.sendMessage(chatId, "Тикет с ID: " + ticketId + " не найден");
        }
    }
}
