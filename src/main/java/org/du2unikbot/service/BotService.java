package org.du2unikbot.service;

import org.du2unikbot.web.bot.Du2UnikBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotService {
    void handleUpdate(Du2UnikBot du2UnikBot, Update update);
}
