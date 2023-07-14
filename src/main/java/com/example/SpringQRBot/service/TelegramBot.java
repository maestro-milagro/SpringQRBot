package com.example.SpringQRBot.service;

import com.example.SpringQRBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String textMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (textMessage){
                case "/start":
                    startCommandReceived(update.getMessage().getChat().getFirstName(), chatId);
                    break;
                default:
                    sendMessage("Sorry, command not recognaizd", chatId);
            }
        }
    }
    public void startCommandReceived(String firstName, Long id) {
        String answer = "Hi " + firstName + ", nice to meet you!";
        log.info("Replied to user " + firstName);
        sendMessage(answer, id);
    }
    public void sendMessage(String text, long id) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(id));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
