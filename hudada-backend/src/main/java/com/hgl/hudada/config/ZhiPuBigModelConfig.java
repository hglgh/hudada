package com.hgl.hudada.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 请别把我整破防
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class ZhiPuBigModelConfig {
    private String apiKey;

    @Bean
    public ClientV4 clientV4() {
        return new ClientV4.Builder(apiKey).build();
    }
}
