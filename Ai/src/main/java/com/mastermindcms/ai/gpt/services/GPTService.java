package com.mastermindcms.ai.gpt.services;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.reactivex.Flowable;

import java.util.List;

public interface GPTService {

    /**
     * This method does call to GPT-model
     *
     * @param messages message history for GPT-model
     * @param userName name of the user
     * @return generate answer from GPT-model
     * @throws Exception any exception
     */
    ChatCompletionResult ask(List<ChatMessage> messages, String userName) throws Exception;

    /**
     * This method does call to GPT-model
     *
     * @param messages message history for GPT-model
     * @param userName name of the user
     * @return generate answer from GPT-model
     * @throws Exception any exception
     */
    Flowable<ChatCompletionChunk> streamAsk(List<ChatMessage> messages, String userName) throws Exception;

}
