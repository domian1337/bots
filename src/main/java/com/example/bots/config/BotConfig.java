package com.example.bots.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * конфигурационный файл с зависимостями
 */
@Data
@Configuration
@PropertySource("application.properties")
public class BotConfig {
    // @param groupId данные переменной группы
    @Value("${groupId}")
    Long groupId;
    // @param token данные переменной токена
    @Value("${token}")
    String token;
}
