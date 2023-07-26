package com.example.SpringQRBot.service;

import com.example.SpringQRBot.QRTools.ReadQRCode;
import com.example.SpringQRBot.config.BotConfig;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String helpText = "Функции чат-бота: \\n\" +\n" +
            "                \"- считывание QR-кода: для считывания QR-кода сфотографируйте код и отправьте изображение в чат \\n\" +\n" +
            "                \"- генерация QR-кода: для генерации QR-кода отправьте текст или ссылку в чат";
    BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "get a welcome message"));
        commands.add(new BotCommand("/mydata", "get your data stored"));
        commands.add(new BotCommand("/deletedata", "delete my data"));
        commands.add(new BotCommand("/help", "info how to use this bot"));
        commands.add(new BotCommand("/settings", "set your preferences"));
        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
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
                case "/help":
                    sendMessage(helpText, chatId);
                    break;
                default:
                    try {
                        String ans1 = ReadQRCode.encodeText(textMessage,512,512);
                        sendPhoto(ans1, chatId);
                    } catch (WriterException | IOException e) {
                        log.error("Error occurred: " + e.getMessage());
                    }
//                    sendMessage("Sorry, command not recognized", chatId);
            }
        } else if(update.hasMessage() && update.getMessage().hasPhoto()){
            long chat_id = update.getMessage().getChatId();
            try {
                String s = ReadQRCode.readQR(getPhotoPath(update));
                sendMessage(s, chat_id);
            } catch (IOException | NotFoundException e) {
                log.error("Error occurred: " + e.getMessage());
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
    public void sendPhoto(String path, long chatId){
        try{
            InputFile file = new InputFile(new java.io.File(path));
            SendPhoto msg = new SendPhoto();
            msg.setPhoto(file);
            msg.setChatId(String.valueOf(chatId));
            execute(msg);
        }catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    public String getPhotoPath(Update update){
        List<PhotoSize> photos = update.getMessage().getPhoto();
        Message message = update.getMessage();
        int count = 1;
        if (message.hasPhoto())
        {
            for (PhotoSize photo : photos) {
                GetFile getFile = new GetFile(photo.getFileId());
                try {
                    File file = execute(getFile); //tg file obj
                    downloadFile(file, new java.io.File("photos/photo" + count + ".png"));
                    count++;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        return "photos/photo" + 3 + ".png";
    }

}
