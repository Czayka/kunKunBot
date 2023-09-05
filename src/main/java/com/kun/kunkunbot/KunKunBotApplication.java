package com.kun.kunkunbot;

import com.kun.kunkunbot.bot.KunKunReceiveBot;
import com.kun.kunkunbot.bot.KunKunSendBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class KunKunBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(KunKunBotApplication.class, args);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new KunKunReceiveBot());
            telegramBotsApi.registerBot(new KunKunSendBot());
            // 没有魔法需要代理 应该是这样设置
            /*
            DefaultBotOptions options = new DefaultBotOptions();
            options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
            options.setProxyHost("127.0.0.1");
            options.setProxyPort(7890);
            telegramBotsApi.registerBot(new KunKunReceiveBot(options,"你的机器人token"));
            telegramBotsApi.registerBot(new KunKunSendBot(options,"你的机器人token"));

            Bot页删掉 public String getBotToken()方法
            然后生成
            public KunKunReceiveBot(DefaultBotOptions options, String botToken) {
                super(options, botToken);
            }
            */
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
