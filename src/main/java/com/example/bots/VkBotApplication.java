package com.example.bots;

import com.example.bots.config.BotInitializer;
import com.example.bots.service.VKBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class VkBotApplication  {
    public static void main(String[] args) {
        SpringApplication.run(VkBotApplication.class, args);
    }
}
