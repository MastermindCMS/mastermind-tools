package com.mastermindcms.ai.gpt.services;

import com.mastermindcms.ai.gpt.config.AiConfiguration;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;

@Service
public class InitialOpenAiImpl implements InitialOpenAi {

    @Autowired
    private AiConfiguration aiConfiguration;

    private OpenAiService openAiservice;

    @PostConstruct
    public void init() {
        if(isOpenAiKeyDefined() && isOpenAiModelDefined()){
            this.openAiservice = new OpenAiService(aiConfiguration.getOpenAiToken(), Duration.ofSeconds(60L));
        }
    }

    @Override
    public boolean isOpenAiKeyDefined() {
        String key = aiConfiguration.getOpenAiToken();
        return Objects.nonNull(key) && !key.isEmpty();
    }

    @Override
    public boolean isOpenAiModelDefined() {
        String model = aiConfiguration.getOpenAiChatModel();
        return Objects.nonNull(model) && !model.isEmpty();
    }

    @Override
    public OpenAiService getOpenAiService() {
        return this.openAiservice;
    }

    @Override
    public AiConfiguration getConfiguration() {
        return this.aiConfiguration;
    }

}
