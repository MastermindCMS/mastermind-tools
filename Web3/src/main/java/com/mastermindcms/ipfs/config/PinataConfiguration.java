package com.mastermindcms.ipfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:pinata.properties")
@ConfigurationProperties(prefix = "pinata")
@Data
public class PinataConfiguration {

    private String apiKey;

    private String secretApiKey;

    private String baseApiUrl;

    private String ipfsGatewayUrl;

    private String baseUrl;
}
