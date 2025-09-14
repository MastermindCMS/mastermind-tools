package com.mastermindcms.modules.services;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.image.Image;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface AnswerService {

    /**
     * This method send an update for a simple message
     * @param update bot api object
     * @param msg message text
     * @return message to answer
     */
    SendMessage createSimpleMsg(Update update, String msg);

    /**
     * This method send a simple message
     * @param chatId chat id
     * @param msg message text
     * @return message to answer
     */
    SendMessage createSimpleMsg(String chatId, String msg);

    /**
     * This method send an update for a greeting message
     * @param update bot api object
     * @return message to answer
     */
    SendMessage completionGreeting(Update update);

    /**
     * This method send an update for a greeting message
     * @param update bot api object
     * @return message to answer
     */
    SendMessage chatGreeting(Update update);

    /**
     * This method send an update for a generated picture
     * @param update bot api object
     * @return message to answer
     */
    SendMessage picGreeting(Update update);

    /**
     * This method send a simple message
     * @param choices chat gpt choices object
     * @param chatId chat id
     * @return message to answer
     */
    SendMessage gptCompletion(List<CompletionChoice> choices, Long chatId);

    /**
     * This method send a simple message
     * @param choices chat gpt choices object
     * @param chatId chat id
     * @return message to answer
     */
    SendMessage gptChatCompletion(List<ChatCompletionChoice> choices, Long chatId);

    /**
     * This method send a picture URL
     * @param image generated image
     * @param chatId chat id
     * @return url for the generated image
     * @throws Exception any exception
     */
    SendPhoto dallePicURL(Image image, Long chatId) throws Exception;

    /**
     * This method send an error text
     * @param errText error text
     * @param chatId chat id
     * @return message to answer
     */
    SendMessage getErrorText(String errText, Long chatId);

}
