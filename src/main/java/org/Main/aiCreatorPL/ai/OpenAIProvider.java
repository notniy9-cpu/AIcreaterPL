package org.Main.aiCreatorPL.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.Main.aiCreatorPL.AICreatorPL;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class OpenAIProvider implements AIProvider {

    private final String apiKey;
    private final String endpoint;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    public OpenAIProvider() {
        this.apiKey = AICreatorPL.getInstance().getConfig().getString("openai.api-key", "");
        this.endpoint = AICreatorPL.getInstance().getConfig().getString("openai.endpoint", "https://api.openai.com/v1/chat/completions");
        this.model = AICreatorPL.getInstance().getConfig().getString("openai.model", "gpt-3.5-turbo");
        this.maxTokens = AICreatorPL.getInstance().getConfig().getInt("openai.max-tokens", 2000);
        this.temperature = AICreatorPL.getInstance().getConfig().getDouble("openai.temperature", 0.7);
    }

    private String sendRequest(String jsonBody) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        connection.disconnect();

        if (responseCode != 200) {
            return "{\"error\": \"HTTP " + responseCode + ": " + response.toString() + "\"}";
        }

        return response.toString();
    }

    @Override
    public CompletableFuture<String> chat(String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);

                JsonArray messages = new JsonArray();
                JsonObject userMessage = new JsonObject();
                userMessage.addProperty("role", "user");
                userMessage.addProperty("content", message);
                messages.add(userMessage);
                requestBody.add("messages", messages);

                requestBody.addProperty("max_tokens", 1000);
                requestBody.addProperty("temperature", temperature);

                String response = sendRequest(requestBody.toString());

                // ИСПРАВЛЕНО: Лучшая обработка JSON
                try {
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                    if (json.has("error")) {
                        return "§cОшибка API: " + json.get("error").getAsJsonObject().get("message").getAsString();
                    }

                    if (json.has("choices") && json.getAsJsonArray("choices").size() > 0) {
                        String content = json.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                        return content;
                    } else {
                        return "§cНеожиданный ответ от API";
                    }

                } catch (Exception e) {
                    // Если JSON не распарсился, возвращаем текст ошибки
                    return "§cОшибка: Неверный ответ от сервера. Попробуйте позже.";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "§cОшибка: " + e.getMessage();
            }
        });
    }

    @Override
    public CompletableFuture<String> generateStructure(String description) {
        String prompt = AICreatorPL.getInstance().getConfig().getString("building.prompt-prefix", "") +
                "\n\nОписание постройки: " + description +
                "\n\nОтветь ТОЛЬКО JSON объектом, без лишнего текста. Формат: {\"blocks\": [{\"x\": 0, \"y\": 0, \"z\": 0, \"material\": \"block_name\"}]}";

        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);

                JsonArray messages = new JsonArray();
                JsonObject userMessage = new JsonObject();
                userMessage.addProperty("role", "user");
                userMessage.addProperty("content", prompt);
                messages.add(userMessage);
                requestBody.add("messages", messages);

                requestBody.addProperty("max_tokens", maxTokens);
                requestBody.addProperty("temperature", temperature);

                String response = sendRequest(requestBody.toString());

                try {
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                    if (json.has("error")) {
                        return "{\"error\": \"" + json.get("error").getAsJsonObject().get("message").getAsString() + "\"}";
                    }

                    if (json.has("choices") && json.getAsJsonArray("choices").size() > 0) {
                        return json.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                    } else {
                        return "{\"error\": \"Пустой ответ от API\"}";
                    }

                } catch (Exception e) {
                    return "{\"error\": \"Ошибка парсинга ответа\"}";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        });
    }
}