package org.Main.aiCreatorPL.ai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.Main.aiCreatorPL.AICreatorPL;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class OllamaProvider implements AIProvider {

    private final String endpoint;
    private final String model;

    public OllamaProvider() {
        this.endpoint = AICreatorPL.getInstance().getConfig().getString("ollama.endpoint", "http://localhost:11434/api/generate");
        this.model = AICreatorPL.getInstance().getConfig().getString("ollama.model", "llama3");
    }

    private String sendRequest(String jsonBody) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(120000);

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
            return "{\"error\": \"HTTP " + responseCode + "\"}";
        }

        return response.toString();
    }

    @Override
    public CompletableFuture<String> chat(String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);
                requestBody.addProperty("prompt", message);
                requestBody.addProperty("stream", false);

                String response = sendRequest(requestBody.toString());

                try {
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                    if (json.has("error")) {
                        return "§cОшибка: " + json.get("error").getAsString();
                    }

                    if (json.has("response")) {
                        return json.get("response").getAsString();
                    } else {
                        return "§cНеожиданный ответ от Ollama";
                    }

                } catch (Exception e) {
                    return "§cОшибка парсинга ответа от Ollama";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "§cОшибка соединения с Ollama: " + e.getMessage();
            }
        });
    }

    @Override
    public CompletableFuture<String> generateStructure(String description) {
        String prompt = AICreatorPL.getInstance().getConfig().getString("building.prompt-prefix", "") +
                "\n\nОписание постройки: " + description +
                "\n\nОтветь ТОЛЬКО JSON объектом, без лишнего текста.";

        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", model);
                requestBody.addProperty("prompt", prompt);
                requestBody.addProperty("stream", false);

                String response = sendRequest(requestBody.toString());

                try {
                    JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                    if (json.has("error")) {
                        return "{\"error\": \"" + json.get("error").getAsString() + "\"}";
                    }

                    if (json.has("response")) {
                        return json.get("response").getAsString();
                    } else {
                        return "{\"error\": \"Пустой ответ от Ollama\"}";
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