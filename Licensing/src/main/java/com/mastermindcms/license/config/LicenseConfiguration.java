package com.mastermindcms.license.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:license.properties")
@ConfigurationProperties(prefix = "lc")
@Data
public class LicenseConfiguration {

    private String licenseAccountSlug;

    private String licenseKey;

    private String licenseValidationUrl;

}
