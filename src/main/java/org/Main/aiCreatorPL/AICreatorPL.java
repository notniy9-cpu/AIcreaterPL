package org.Main.aiCreatorPL;

import org.Main.aiCreatorPL.ai.AIProvider;
import org.Main.aiCreatorPL.ai.OpenAIProvider;
import org.Main.aiCreatorPL.ai.OllamaProvider;
import org.Main.aiCreatorPL.ai.MockProvider;
import org.Main.aiCreatorPL.commands.AICommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class AICreatorPL extends JavaPlugin {
    private static AICreatorPL instance;
    private AIProvider aiProvider;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        String provider = getConfig().getString("provider", "mock");

        getLogger().info("=========================================");
        getLogger().info("AICreatorPL v1.0.0");
        getLogger().info("=========================================");

        // Выбираем провайдера
        if (provider.equalsIgnoreCase("openai")) {
            aiProvider = new OpenAIProvider();
            getLogger().info("Режим: OpenAI (требуется API ключ)");
        } else if (provider.equalsIgnoreCase("ollama")) {
            aiProvider = new OllamaProvider();
            getLogger().info("Режим: Ollama (требуется установка)");
        } else {
            aiProvider = new MockProvider();
            getLogger().info("Режим: MOCK (тестовый, не требует настроек)");
            getLogger().info("Это тестовый режим, ИИ отвечает шаблонными фразами");
        }

        // Регистрируем команду
        if (getCommand("ai") != null) {
            getCommand("ai").setExecutor(new AICommandExecutor(this));
            getLogger().info("Команда /ai зарегистрирована");
        }

        getLogger().info("=========================================");
        getLogger().info("Плагин успешно загружен!");
        getLogger().info("Для теста введите: /ai chat привет");
        getLogger().info("=========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("AICreatorPL выключен");
    }

    public static AICreatorPL getInstance() {
        return instance;
    }

    public AIProvider getAIProvider() {
        return aiProvider;
    }
}