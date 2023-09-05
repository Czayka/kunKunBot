package com.kun.kunkunbot.bot;

import com.kun.kunkunbot.config.KunKunSendBotConfig;
import com.kun.kunkunbot.constant.TelegramConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Objects;


@Component
public class KunKunSendBot extends TelegramLongPollingBot {

    @Autowired
    @Lazy
    private KunKunReceiveBot kunKunReceiveBot;

    @Autowired
    private KunKunSendBotConfig kunKunSendBotConfig;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {

        // 获取收到的信息
        Message message = update.getMessage();
        // 用户id
        Long chatId = message.getChatId();

        // 不是我自己,不做任何事情
        if (!Objects.equals(chatId, TelegramConstant.ADMIN_ID)){
            return;
        }

        // 输入内容
        String text = message.getText();
        List<MessageEntity> entities = message.getEntities();
        // 指令信息为机器人上面的/start一类
        // 非指令信息
        if (CollectionUtils.isEmpty(entities)) {
            // 原信息返回
            sendMessage(chatId,text);
            return;
        }
        // 非指令
        if (!"bot_command".equals(entities.get(0).getType())) {
            // 原信息返回
            sendMessage(chatId,text);
            return;
        }

        // 指令信息 此时为/send 用户id 发送的信息
        // 避免多个空格将2个以上空格替换为1个空格
        text = text.replaceAll(" +", " ");
        String[] commonAndParma = text.split(" ");
        // 假设发来的是/send将id和内容转发给机器人(KunKunSendBot)
        if ("/send".equals(commonAndParma[0])){
            kunKunReceiveBot.sendMessage(Long.parseLong(commonAndParma[1]),commonAndParma[2]);
        }
        // 友好提示,发送成功返回
        sendMessage(chatId,"发送成功");
    }

    public void sendMessage(Long chatId,String result) {
        SendMessage sendMessage = SendMessage.builder()
                .text(result)
                .chatId(chatId.toString())
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void clearWebhook() throws TelegramApiRequestException {

    }

    @Override
    public String getBotUsername() {
        return kunKunSendBotConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return kunKunSendBotConfig.getToken();
    }
}

