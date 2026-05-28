package org.Main.aiCreatorPL.ai;

import java.util.concurrent.CompletableFuture;
import java.util.Random;

public class MockProvider implements AIProvider {

    private final Random random = new Random();

    // Список случайных ответов для чата
    private final String[] chatResponses = {
            "Привет! Я твой ИИ помощник в Minecraft!",
            "Отличный вопрос! Давай построим что-нибудь крутое!",
            "Я думаю, это хорошая идея для твоего сервера!",
            "Можешь попробовать команду /ai build для создания построек!",
            "У тебя отлично получается строить в Minecraft!",
            "Попробуй построить дом или фонтан, у тебя получится!",
            "Я здесь, чтобы помочь тебе с постройками!"
    };

    // Список ответов на конкретные вопросы
    private String getSmartResponse(String message) {
        String lowerMsg = message.toLowerCase();

        if (lowerMsg.contains("привет") || lowerMsg.contains("здравствуй")) {
            return "Приветствую! Рад помочь тебе с постройками!";
        }
        if (lowerMsg.contains("как дела")) {
            return "У меня всё отлично! А как твои приключения в Minecraft?";
        }
        if (lowerMsg.contains("помощь") || lowerMsg.contains("help")) {
            return "Используй /ai build <описание> чтобы создавать постройки! Пример: /ai build дом 5x5";
        }
        if (lowerMsg.contains("спасибо")) {
            return "Пожалуйста! Обращайся ещё!";
        }
        if (lowerMsg.contains("кто ты")) {
            return "Я Mock ИИ - тестовый помощник для создания построек в Minecraft!";
        }

        return null;
    }

    @Override
    public CompletableFuture<String> chat(String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Имитация задержки ИИ (1 секунда)
                Thread.sleep(1000);

                // Проверяем на конкретные вопросы
                String specificResponse = getSmartResponse(message);
                if (specificResponse != null) {
                    return specificResponse;
                }

                // Иначе случайный ответ
                return chatResponses[random.nextInt(chatResponses.length)];

            } catch (InterruptedException e) {
                return "Ошибка при генерации ответа!";
            }
        });
    }

    @Override
    public CompletableFuture<String> generateStructure(String description) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Имитация генерации (2 секунды)
                Thread.sleep(2000);

                String lowerDesc = description.toLowerCase();

                // Генерируем JSON для разных типов построек
                if (lowerDesc.contains("дом") || lowerDesc.contains("house")) {
                    return generateHouse();
                } else if (lowerDesc.contains("фонтан") || lowerDesc.contains("fountain")) {
                    return generateFountain();
                } else if (lowerDesc.contains("башня") || lowerDesc.contains("tower")) {
                    return generateTower();
                } else if (lowerDesc.contains("мост") || lowerDesc.contains("bridge")) {
                    return generateBridge();
                } else {
                    // Стандартная постройка
                    return generateDefault();
                }

            } catch (InterruptedException e) {
                return "{\"error\": \"Ошибка генерации\"}";
            }
        });
    }

    // Генерация дома
    private String generateHouse() {
        return "{\"blocks\": [" +
                "{\"x\":0,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":1,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":-1,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":0,\"y\":0,\"z\":1,\"material\":\"oak_planks\"}," +
                "{\"x\":0,\"y\":0,\"z\":-1,\"material\":\"oak_planks\"}," +
                "{\"x\":1,\"y\":0,\"z\":1,\"material\":\"oak_planks\"}," +
                "{\"x\":-1,\"y\":0,\"z\":1,\"material\":\"oak_planks\"}," +
                "{\"x\":1,\"y\":0,\"z\":-1,\"material\":\"oak_planks\"}," +
                "{\"x\":-1,\"y\":0,\"z\":-1,\"material\":\"oak_planks\"}," +
                "{\"x\":0,\"y\":1,\"z\":0,\"material\":\"oak_log\"}," +
                "{\"x\":0,\"y\":2,\"z\":0,\"material\":\"oak_log\"}," +
                "{\"x\":0,\"y\":1,\"z\":1,\"material\":\"glass_pane\"}," +
                "{\"x\":0,\"y\":1,\"z\":-1,\"material\":\"glass_pane\"}," +
                "{\"x\":1,\"y\":1,\"z\":0,\"material\":\"glass_pane\"}," +
                "{\"x\":-1,\"y\":1,\"z\":0,\"material\":\"glass_pane\"}" +
                "]}";
    }

    // Генерация фонтана
    private String generateFountain() {
        return "{\"blocks\": [" +
                "{\"x\":0,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":1,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":-1,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":0,\"z\":1,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":0,\"z\":-1,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":1,\"z\":0,\"material\":\"water\"}," +
                "{\"x\":1,\"y\":1,\"z\":0,\"material\":\"stone_brick_stairs\"}," +
                "{\"x\":-1,\"y\":1,\"z\":0,\"material\":\"stone_brick_stairs\"}," +
                "{\"x\":0,\"y\":1,\"z\":1,\"material\":\"stone_brick_stairs\"}," +
                "{\"x\":0,\"y\":1,\"z\":-1,\"material\":\"stone_brick_stairs\"}" +
                "]}";
    }

    // Генерация башни
    private String generateTower() {
        return "{\"blocks\": [" +
                "{\"x\":0,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":1,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":-1,\"y\":0,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":0,\"z\":1,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":0,\"z\":-1,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":1,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":2,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":3,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":4,\"z\":0,\"material\":\"stone_bricks\"}," +
                "{\"x\":0,\"y\":5,\"z\":0,\"material\":\"stone_bricks\"}" +
                "]}";
    }

    // Генерация моста
    private String generateBridge() {
        return "{\"blocks\": [" +
                "{\"x\":0,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":1,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":2,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":3,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":4,\"y\":0,\"z\":0,\"material\":\"oak_planks\"}," +
                "{\"x\":0,\"y\":0,\"z\":1,\"material\":\"oak_fence\"}," +
                "{\"x\":4,\"y\":0,\"z\":1,\"material\":\"oak_fence\"}" +
                "]}";
    }

    // Стандартная генерация
    private String generateDefault() {
        return "{\"blocks\": [" +
                "{\"x\":0,\"y\":0,\"z\":0,\"material\":\"stone\"}," +
                "{\"x\":1,\"y\":0,\"z\":0,\"material\":\"stone\"}," +
                "{\"x\":-1,\"y\":0,\"z\":0,\"material\":\"stone\"}," +
                "{\"x\":0,\"y\":0,\"z\":1,\"material\":\"stone\"}," +
                "{\"x\":0,\"y\":0,\"z\":-1,\"material\":\"stone\"}," +
                "{\"x\":0,\"y\":1,\"z\":0,\"material\":\"stone\"}," +
                "{\"x\":0,\"y\":2,\"z\":0,\"material\":\"stone\"}" +
                "]}";
    }
}
