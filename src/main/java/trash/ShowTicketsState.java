package trash;

import org.example.Du2UnikBot;
import org.example.State;
import org.example.Ticket;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ShowTicketsState extends State {
    public ShowTicketsState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleMessage(Message message) {
        // Инициализируем переменные
        super.handleMessage(message);

        switch (messageText){
            case "Мои тикеты" -> showTickets(false);
            case "Все тикеты" -> showTickets(true);
        }
    }
    public void showTickets(boolean showAll) {
        boolean hasTickets = false; // Флаг для проверки наличия тикетов

        for (Ticket ticket : du2UnikBot.getTickets().values()) {
            boolean isUserTicket = ticket.getUsernames().contains(username);
            if (showAll || isUserTicket) {
                SendMessage message = ticket.createMessage(isUserTicket);
                message.setChatId(chatId);
                try {
                    du2UnikBot.execute(message);
                    hasTickets = true; // Устанавливаем флаг в true, если найдены тикеты
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!hasTickets) {
            String messageText = showAll ? "Пока не создан ни один тикет!" : "У вас нет тикетов!";
            du2UnikBot.sendMessage(chatId, messageText);
        }
    }

}
