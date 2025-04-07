package com.example.bots.service;

import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Класс сервис, который проверяет наличие сообщений в чате.
 * Содержит логику отправки сообщения, а также некоторые дополнительные методы.
 */
@Service
public class VKBotService {
    private static final Logger LOGGER = Logger.getLogger(VKBotService.class.getName());
    private static Random rand = new Random();
    private static VkApiClient api;
    private static GroupActor actor;
    private static Integer ts;

    public VKBotService(VkApiClient api, GroupActor actor, Integer ts) {
        VKBotService.api = api;
        VKBotService.actor = actor;
        VKBotService.ts = ts;
        start();
    }

    public VKBotService() {
    }

    /**
     * Логика приема и отправки сообщений. Я не смог разобраться как правильно запустить метод run() из инициализации,
     * чтобы не писать бесконечный цикл, потому что в документации VK API очень много ошибок.
     */
    public static void start() {
        VKBotService bot = new VKBotService();
        while (true) {
            try {
                MessagesGetLongPollHistoryQuery historyQuery = api.messages().getLongPollHistory(actor).ts(ts);
                List<Message> messages = historyQuery.execute().getMessages().getItems();
                messages.forEach(message -> {
                    try {
                        LOGGER.info("[NEW MESSAGE] " + message.getText());
                        Optional<String> textO = Optional.ofNullable(message.getText());
                        var text = textO.orElseThrow(() -> new RuntimeException("Текст сообщения не найден"));
                        var textForSwitch = text.toLowerCase();
                        var appeal = bot.getAppeal(message, message.getFromId());
                        switch (textForSwitch) {
                            case "привет" -> bot.sendMessage(message, "Добро пожаловть, " + appeal);
                            // case "новый аккаунт" -> createNewCRMClient();
                            default -> bot.sendMessage(message, "Вы написали " + text);
                        }
                    } catch (Exception e) {
                        LOGGER.warning("Ошибка отправки сообщения");
                    }
                });
                ts = api.messages().getLongPollServer(actor).execute().getTs();
                Thread.sleep(1000);
            } catch (ApiException | ClientException | InterruptedException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Информация о профилях нескольких профилей
     *
     * @param ids    список id
     * @param fields поля профиля пользователя
     * @return List c профилями
     */
    public List<GetResponse> getUsers(List<Long> ids, Fields... fields) {
        try {
            return api.users()
                    .get(actor)
                    .userIds(ids.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .fields(fields)
                    .lang(Lang.RU)
                    .execute();
        } catch (ApiException | ClientException e) {
            LOGGER.info("cant get user: " + ids);
            return null;
        }
    }

    /**
     * Информация о профиле одного пользователя
     *
     * @param id     id пользователя
     * @param fields поля профиля пользователя
     * @return профиль пользователя
     */
    public GetResponse getUser(long id, Fields... fields) {
        var users = getUsers(List.of(id), fields); // создаем список id с одним единствкенным id
        if (users == null) return null;
        return users.get(0);
    }

    /**
     * Создание обращения к профилю пользователя
     *
     * @param message полученное сообщение
     * @param userId  id отправителя сообщения
     * @return сформированное обращение
     */
    public String getAppeal(Message message, long userId) {
        var user = getUser(message.getFromId(), Fields.FIRST_NAME_NOM);
        if (user == null) {
            return null;
        }
        return String.format("[id%d|%s]", userId, user.getFirstName());
    }

    /**
     * Метод отправки сообщения в чат
     *
     * @param message     полученное сообщение
     * @param text        текст сообщения
     * @param attachments вложение (аудио, фото, видео)
     * @return возвращаем true если успешно
     */
    public boolean sendMessage(Message message, String text, String... attachments) {
        try {
            api.messages()
                    .sendDeprecated(actor)
                    .message(text)
                    .userId(message.getFromId())
                    .randomId(rand.nextInt(1000))
                    .execute();
            LOGGER.log(Level.INFO, "[Отправлено сообщение]: " + text);
        } catch (ApiException | ClientException e) {
            LOGGER.log(Level.WARNING, "Ошибка отправки сообщения");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}