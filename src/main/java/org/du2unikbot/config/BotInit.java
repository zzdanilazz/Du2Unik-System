package org.du2unikbot.config;

import lombok.extern.slf4j.Slf4j;
import org.du2unikbot.Du2UnikBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotInit {

    private final Du2UnikBot du2UnikBot;

    @Autowired
    public BotInit(Du2UnikBot du2UnikBot) {
        this.du2UnikBot = du2UnikBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(du2UnikBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
