package com.mastermindcms.ai.gpt.services;

import com.mastermindcms.ai.gpt.config.AiConfiguration;
import com.theokanning.openai.service.OpenAiService;

public interface InitialOpenAi {

    /**
     * This method does return boolean result.
     *
     * @return value as boolean
     */
    boolean isOpenAiKeyDefined();

    /**
     * This method does return boolean result.
     *
     * @return value as boolean
     */
    boolean isOpenAiModelDefined();


    /**
     * This method contains initiated instance from OpenAi service
     *
     * @return initiated instance from OpenAi service
     */
    OpenAiService getOpenAiService();

    /**
     * This method contains configuration for OpenAi service
     *
     * @return configuration for OpenAi service
     */
    AiConfiguration getConfiguration();

}
