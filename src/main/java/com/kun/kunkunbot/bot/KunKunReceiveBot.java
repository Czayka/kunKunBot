package com.kun.kunkunbot.bot;

import com.kun.kunkunbot.config.KunKunReceiveBotConfig;
import com.kun.kunkunbot.constant.TelegramConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
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


@Component
public class KunKunReceiveBot extends TelegramLongPollingBot {

    @Autowired
    @Lazy
    private KunKunSendBot kunKunSendBot;

    @Autowired
    private KunKunReceiveBotConfig kunKunReceiveBotConfig;

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

        // 指令信息 一般为/start 123中间有空格
        // 避免多个空格将2个以上空格替换为1个空格
        text = text.replaceAll(" +", " ");
        String[] commonAndParma = text.split(" ");
        // 假设发来的是/send将id和内容转发给机器人(KunKunSendBot)
        if ("/send".equals(commonAndParma[0])){
            // tg://user?id="+chatId 一个可以点击的链接可以直接点开对方信息
            String result = "用户:"+" tg://user?id="+chatId+" 发送信息:"+commonAndParma[1];
            kunKunSendBot.sendMessage(TelegramConstant.ADMIN_ID,result);
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
        return kunKunReceiveBotConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return kunKunReceiveBotConfig.getToken();
    }
}
