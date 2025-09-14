package com.mastermindcms.ai.gpt.services;

import com.theokanning.openai.image.Image;

public interface DalleService {

    /**
     * This method does call to GPT-model
     *
     * @param text prompt for GPT-model
     * @param userName name of the user
     * @return generated image from GPT-model
     * @throws Exception any exception
     */
    Image generateImage(String text, String userName) throws Exception;

}
