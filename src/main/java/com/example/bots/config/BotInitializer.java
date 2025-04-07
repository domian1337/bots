package com.example.bots.config;

import com.example.bots.service.VKBotService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Класс с одним статическим методом для инициализации бота на сервере.
 * Все переменные передаются в сервис и оперируются в нем.
 */

@Getter
@Setter
@Component
public class BotInitializer {

    private static final Logger LOGGER = Logger.getLogger(BotInitializer.class.getName());
    private static BotConfig botConfig;
    private static VkApiClient api;
    private static GroupActor actor;
    private static Integer ts;

    /**
     * Можно было сразу написать значения groupId и token,
     * но я решил их поместить в папку с ресурсами и от туда их уже брать.
     */
    @Autowired
    public BotInitializer(BotConfig botConfig) {
        BotInitializer.botConfig = botConfig;
    }

    public BotInitializer() {
    }

    /**
     * Метод инициализации. Все сделано по документации VK API.
     */
    @PostConstruct
    public static void init() {
        try {
            api = new VkApiClient(new HttpTransportClient());
            actor = new GroupActor(botConfig.getGroupId(), botConfig.getToken());
            api.groups()
                    .setLongPollSettings(actor, botConfig.getGroupId())
                    .enabled(true)
                    .wallPostNew(true)
                    .messageNew(true)
                    .execute();
            api.groupsLongPoll().getLongPollServer(actor, botConfig.getGroupId()).execute();
            ts = api.messages().getLongPollServer(actor).execute().getTs();
            LOGGER.info("Бот инициализировался");
            new VKBotService(api, actor, ts);
        } catch (ApiException | ClientException e) {
            LOGGER.info("нет подключения к вк");
        }
    }
}