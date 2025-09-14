package com.mastermindcms.modules.gcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:gcp.properties")
@ConfigurationProperties(prefix = "gcp")
@Data
public class GCPConfiguration {

    private String fcmServiceAccountKey;

    private String fcmScopeUrl;
}
