package org.Main.aiCreatorPL.commands;

import org.Main.aiCreatorPL.AICreatorPL;
import org.Main.aiCreatorPL.builder.StructureBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class AICommandExecutor implements CommandExecutor {

    private final AICreatorPL plugin;

    public AICommandExecutor(AICreatorPL plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aicreater.use")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав!");
            return true;
        }
        // Добавьте в метод onCommand перед остальными проверками:
        if (args[0].equalsIgnoreCase("test")) {
            sender.sendMessage("§aТест: API работает!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Только для игроков!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Использование: /ai chat <сообщение>");
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            Player player = (Player) sender;
            player.sendMessage(ChatColor.GRAY + "🤖 ИИ думает...");

            plugin.getAIProvider().chat(message.toString().trim())
                    .thenAccept(response -> {
                        player.sendMessage(ChatColor.GREEN + "🤖 ИИ: " + ChatColor.WHITE + response);
                    })
                    .exceptionally(throwable -> {
                        player.sendMessage(ChatColor.RED + "Ошибка: " + throwable.getMessage());
                        return null;
                    });

        } else if (args[0].equalsIgnoreCase("build")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Только для игроков!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Использование: /ai build <описание>");
                sender.sendMessage(ChatColor.GRAY + "Пример: /ai build дом 5x5 из дуба");
                return true;
            }

            StringBuilder description = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                description.append(args[i]).append(" ");
            }

            Player player = (Player) sender;
            player.sendMessage(ChatColor.GREEN + "🔨 Генерация: " + description.toString().trim());
            player.sendMessage(ChatColor.GRAY + "Подождите 10-30 секунд...");

            plugin.getAIProvider().generateStructure(description.toString().trim())
                    .thenAccept(jsonResponse -> {
                        try {
                            StructureBuilder.buildFromJSON(player, jsonResponse);
                        } catch (Exception e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    })
                    .exceptionally(throwable -> {
                        player.sendMessage(ChatColor.RED + "Ошибка: " + throwable.getMessage());
                        return null;
                    });
        } else {
            sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== AICreatorPL ===");
        sender.sendMessage(ChatColor.YELLOW + "/ai chat <текст>" + ChatColor.GRAY + " - Чат с ИИ");
        sender.sendMessage(ChatColor.YELLOW + "/ai build <описание>" + ChatColor.GRAY + " - Создать постройку");
    }
}