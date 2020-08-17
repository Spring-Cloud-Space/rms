//: com.falconcamp.cloud.rms.config.PropertiesConfiguration.java


package com.falconcamp.cloud.rms.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;


@Configuration
public class PropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "falconcamp.cloud", ignoreUnknownFields = false)
    public RmsProperties rmsProperties() {
        return new RmsProperties();
    }

    @Getter
    @Setter
    public static class RmsProperties {
        @NotBlank
        private String rmsServiceHost;
    }

}///:~