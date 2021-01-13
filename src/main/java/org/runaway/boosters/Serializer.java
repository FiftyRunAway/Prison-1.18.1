package org.runaway.boosters;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.enums.BoosterType;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by _RunAway_ on 30.1.2019
 */

public class Serializer {

    public String unserial(ItemStack itemStack, Gamer gamer, BoosterType types) {
        String num = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).replace("Ускоритель " + types.getName() + " #", "");
        int number = Integer.parseInt(num);
        String configString = gamer.getBoosters().get(number);
        String[] var = configString.split("-");
        double multiplier = Double.parseDouble(var[2]);
        long time = Long.parseLong(var[3]);
        gamer.getBoosters().remove(configString);
        return multiplier + "-" + time;
    }

    public ItemStack unserializeBooster(String string, int i, BoosterType types) {
        String[] var = string.split("-");
        String type = String.valueOf(var[1]).toLowerCase();
        double multiplier = Double.parseDouble(var[2]);
        long time = Integer.parseInt(var[3]);
        Lore lore = new Lore.BuilderLore()
                .addString("&fВремя: &e" + Utils.formatTime(time))
                .addString("&fМножитель: &e" + multiplier + "x")
                .addString("&fТип: &e" + getType(type))
                .build();
        return new Item.Builder("g".equalsIgnoreCase(type) ? (types == BoosterType.BLOCKS ?  Material.DIAMOND_BLOCK : Material.GOLD_BLOCK) : (types == BoosterType.BLOCKS ? Material.DIAMOND : Material.GOLD_INGOT)).name("&eУскоритель " + types.getName() + " " + ChatColor.DARK_GRAY + "#" + i)
                .lore(lore).build().item();
    }

    public static String getStringFromStack(ItemStack itemStack, Gamer gamer, BoosterType type) {
        int number = Integer.parseInt(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).replace("Ускоритель " + type.getName() + " #", ""));
        ArrayList<String> list = new ArrayList<>(gamer.getBoosters());
        String string = list.get(number);
        String[] var = string.split("-");
        double multiplier = Double.parseDouble(var[2]);
        long time = Integer.parseInt(var[3]);
        StringBuilder str = new StringBuilder(type.name()).append(" ")
                .append((itemStack.getType().isBlock() ? "g" : "l")).append(" ")
                .append(multiplier).append(" ")
                .append(time);
        return str.toString();
    }

    private static String getType(String string) {
        switch (string.toLowerCase()) {
            case "g":
                return "Глобальный";
            case "l":
                return "Локальный";
        }
        return null;
    }
}
