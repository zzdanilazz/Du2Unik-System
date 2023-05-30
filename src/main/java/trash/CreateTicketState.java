package trash;

import org.example.Du2UnikBot;
import org.example.State;
import org.example.TicketCreationForm;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;

public class CreateTicketState extends State {
    public CreateTicketState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleMessage(Message message) {
        // Инициализируем переменные
        super.handleMessage(message);

        du2UnikBot.getCurrentTicket().setUsernames(new ArrayList<>(Collections.singleton((username))));
        du2UnikBot.setTicketCreationForm(new TicketCreationForm());
        SendMessage startPointsRequest = du2UnikBot.getTicketCreationForm().getFormMessages().get("start_point");
        startPointsRequest.setChatId(chatId);
        try {
            du2UnikBot.execute(startPointsRequest);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
