package com.mastermindcms.ai.gpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:ai.properties")
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfiguration {

    private String openAiToken;

    private String openAiChatModel;

    private Map<String,Object> systemPrompts;

}
