package org.Main.aiCreatorPL.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.Main.aiCreatorPL.AICreatorPL;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StructureBuilder {

    public static void buildFromJSON(Player player, String jsonResponse) throws Exception {
        String cleanJson = jsonResponse.replaceAll("```json\\n?", "")
                .replaceAll("```", "")
                .trim();

        int startBrace = cleanJson.indexOf("{");
        int endBrace = cleanJson.lastIndexOf("}");

        if (startBrace == -1 || endBrace == -1) {
            throw new Exception("Не найден JSON в ответе");
        }

        String jsonString = cleanJson.substring(startBrace, endBrace + 1);

        JsonObject json;
        try {
            json = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            throw new Exception("Ошибка парсинга JSON: " + e.getMessage());
        }

        if (json.has("error")) {
            throw new Exception("Ошибка ИИ: " + json.get("error").getAsString());
        }

        if (!json.has("blocks")) {
            throw new Exception("Нет поля 'blocks' в ответе");
        }

        JsonArray blocks = json.getAsJsonArray("blocks");
        int maxBlocks = AICreatorPL.getInstance().getConfig().getInt("building.max-blocks", 5000);

        if (blocks.size() > maxBlocks) {
            throw new Exception("Слишком много блоков! Максимум: " + maxBlocks);
        }

        Location center = player.getLocation().getBlock().getLocation();
        int built = 0;

        for (int i = 0; i < blocks.size(); i++) {
            JsonObject block = blocks.get(i).getAsJsonObject();

            if (!block.has("x") || !block.has("y") || !block.has("z") || !block.has("material")) {
                continue;
            }

            int x = block.get("x").getAsInt();
            int y = block.get("y").getAsInt();
            int z = block.get("z").getAsInt();
            String materialName = block.get("material").getAsString().toUpperCase();

            Material material = Material.getMaterial(materialName);
            if (material == null) {
                if (materialName.contains("PLANKS")) material = Material.OAK_PLANKS;
                else if (materialName.contains("LOG")) material = Material.OAK_LOG;
                else if (materialName.contains("GLASS")) material = Material.GLASS;
                else continue;
            }

            Location blockLoc = center.clone().add(x, y, z);

            if (blockLoc.getY() > 320 || blockLoc.getY() < -64) {
                continue;
            }

            blockLoc.getBlock().setType(material);
            built++;
        }

        player.sendMessage(ChatColor.GREEN + "✅ Построено блоков: " + built);
        if (built == 0) {
            throw new Exception("Не удалось построить ни одного блока");
        }
    }
}