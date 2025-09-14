package com.mastermindcms.ai.gpt.services;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DalleServiceImpl extends InitialOpenAiImpl implements DalleService {

    @Override
    public Image generateImage(String text, String userName) throws Exception {
        if (StringUtils.isEmpty(text)) throw new RuntimeException("Please send a text prompt to generate a picture");

        if(isOpenAiKeyDefined() && isOpenAiModelDefined()){
            CreateImageRequest request = CreateImageRequest.builder()
                    .n(1)
                    .user(userName)
                    .prompt(text)
                    .build();
            return getOpenAiService().createImage(request).getData().get(0);
        } else {
            throw new RuntimeException("Please define API key and model for OpenAI account");
        }
    }
}
