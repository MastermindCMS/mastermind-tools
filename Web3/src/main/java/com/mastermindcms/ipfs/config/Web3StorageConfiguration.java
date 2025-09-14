package com.mastermindcms.ipfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:web3storage.properties")
@ConfigurationProperties(prefix = "web3storage")
@Data
public class Web3StorageConfiguration {

    private String apiKey;

    private String proof;
}
