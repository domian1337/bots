package com.example.bots;

import com.example.bots.config.BotInitializer;
import com.example.bots.service.VKBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class VkBotApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(VkBotApplication.class, args);
    }

    /**
     * Так как программа не предназначена для веб сервиса, то мы должны переопределить метод run() и
     * написать свою логику запуска программы. Сначала мы инициализируем бота, затем запускаем его
     * бизнес логику.
     */
    @Override
    public void run(String... args) throws Exception {
        BotInitializer.init();
        VKBotService.start();
    }
}
