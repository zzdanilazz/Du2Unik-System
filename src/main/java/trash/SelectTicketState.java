package trash;

import org.example.Du2UnikBot;
import org.example.State;
import org.example.Ticket;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class SelectTicketState extends State {
    public SelectTicketState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        // Инициализируем переменные
        super.handleCallbackQuery(callbackQuery);

        int ticketId = extractIdFromMessage(messageText);
        Ticket selectedTicket = du2UnikBot.getTickets().get(ticketId);

        if (selectedTicket != null) {
            if (messageText.contains(username)) {
                du2UnikBot.sendMessage(chatId, "Вы уже записаны в тикет с ID: " + ticketId + "!");
            } else {
                // Добавление пользователя в тикет
                selectedTicket.getUsernames().add(username);

                du2UnikBot.sendMessage(chatId, "Вы успешно выбрали тикет с ID: " + ticketId);

                // Изменение числа забронированных мест в тикете
                selectedTicket.setReservedSeats(selectedTicket.getReservedSeats() + 1);

                addUsernameToMessage(chatId, messageId, messageText, username, selectedTicket.getReservedSeats());
            }
        } else {
            du2UnikBot.sendMessage(chatId, "Тикета с ID: " + ticketId + " уже не существует!");
        }
    }
}
