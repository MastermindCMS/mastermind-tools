package com.mastermindcms.ai.gpt.services;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatGPTService extends InitialOpenAiImpl implements GPTService {

    @Override
    public ChatCompletionResult ask(List<ChatMessage> messages, String userName) throws Exception {
        if (messages.isEmpty()) throw new RuntimeException("Send me a text if you have a question");

        if(isOpenAiKeyDefined() && isOpenAiModelDefined()){
            ChatCompletionRequest completionRequest = ChatCompletionRequest
                    .builder()
                    .user(userName)
                    .messages(messages)
                    .model(getConfiguration().getOpenAiChatModel())
                    .build();
            return getOpenAiService().createChatCompletion(completionRequest);
        } else {
            throw new RuntimeException("Please define API key and model for OpenAI account");
        }
    }

    @Override
    public Flowable<ChatCompletionChunk> streamAsk(List<ChatMessage> messages, String userName) throws Exception {
        if (messages.isEmpty()) throw new RuntimeException("Send me a text if you have a question");

        if(isOpenAiKeyDefined() && isOpenAiModelDefined()){
            ChatCompletionRequest completionRequest = ChatCompletionRequest
                    .builder()
                    .user(userName)
                    .messages(messages)
                    .model(getConfiguration().getOpenAiChatModel())
                    .build();
            return getOpenAiService().streamChatCompletion(completionRequest);
        } else {
            throw new RuntimeException("Please define API key and model for OpenAI account");
        }
    }
}
