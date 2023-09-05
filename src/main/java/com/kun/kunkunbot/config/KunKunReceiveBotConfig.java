package com.kun.kunkunbot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.kun-kun-receive-bot")
@Data
public class KunKunReceiveBotConfig {
    @Value("token")
    private String token;
    @Value("username")
    private String username;
}
