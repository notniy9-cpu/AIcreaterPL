package org.Main.aiCreatorPL;

import org.Main.aiCreatorPL.ai.AIProvider;
import org.Main.aiCreatorPL.ai.OpenAIProvider;
import org.Main.aiCreatorPL.ai.OllamaProvider;
import org.Main.aiCreatorPL.commands.AICommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class AICreatorPL extends JavaPlugin {
    private static AICreatorPL instance;
    private AIProvider aiProvider;

    @Override
    public void onEnable() {
        // Сохраняем instance для доступа из других классов
        instance = this;

        // Сохраняем стандартный конфиг, если его нет
        saveDefaultConfig();

        // Перезагружаем конфиг
        reloadConfig();

        // Получаем тип провайдера из конфига
        String provider = getConfig().getString("provider", "openai");

        getLogger().info("========== AICreatorPL Загрузка ==========");
        getLogger().info("Выбран провайдер: " + provider);

        // Инициализируем нужного провайдера
        if (provider.equalsIgnoreCase("openai")) {
            aiProvider = new OpenAIProvider();
            getLogger().info("✓ Провайдер OpenAI успешно загружен");
        } else if (provider.equalsIgnoreCase("ollama")) {
            aiProvider = new OllamaProvider();
            getLogger().info("✓ Провайдер Ollama успешно загружен");
        } else {
            getLogger().severe("✗ Неизвестный провайдер: " + provider);
            getLogger().severe("Плагин будет выключен. Используйте 'openai' или 'ollama'");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Регистрируем команду
        if (getCommand("ai") != null) {
            getCommand("ai").setExecutor(new AICommandExecutor(this));
            getLogger().info("✓ Команда /ai зарегистрирована");
        } else {
            getLogger().warning("✗ Команда 'ai' не найдена в plugin.yml");
        }

        getLogger().info("========================================");
        getLogger().info("AICreatorPL v1.0.0 успешно запущен!");
        getLogger().info("Автор: notniy9");
        getLogger().info("========================================");
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