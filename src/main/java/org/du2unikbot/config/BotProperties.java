package org.du2unikbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram-bot")
@Data
@PropertySource("classpath:application.properties")
public class BotProperties {
    String name;
    String token;
}
