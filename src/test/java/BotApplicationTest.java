import org.du2unikbot.Du2UnikApplication;
import org.du2unikbot.web.bot.Du2UnikBot;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Du2UnikApplication.class)
public class BotApplicationTest {

    @Autowired
    private Du2UnikBot du2UnikBot;

    @BeforeEach
    public void setUp() {
        // Reset the du2UnikBot mock before each test
        reset(du2UnikBot);
    }

    @Test
    public void testSendMessage() throws TelegramApiException {
        // Mock message and user objects
        Message message = mock(Message.class);
        User user = mock(User.class);

        long chatId = 123456789L;
        when(message.getChatId()).thenReturn(chatId);
        when(message.getFrom()).thenReturn(user);
        when(user.getUserName()).thenReturn("testuser");
        when(user.getId()).thenReturn(987654321L);

        // Create the SendMessage object to send
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Hello, this is a test message!");

        // Call sendMessage on du2UnikBot
        du2UnikBot.sendMessage(chatId, "Hello, this is a test message!");

        // Verify that execute method is called on du2UnikBot with the correct SendMessage object
        verify(du2UnikBot, times(1)).execute(any(SendMessage.class));

        // Optionally, verify the content of the SendMessage object
        verify(du2UnikBot).sendMessage(eq(chatId), eq("Hello, this is a test message!"));
    }
}
