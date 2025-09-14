package com.mastermindcms.modules.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:telegram.properties")
@ConfigurationProperties(prefix = "tg")
@Data
public class TelegramConfiguration {

    private String token;

    private String botName;

    private Boolean reactiveUpdate;

}
