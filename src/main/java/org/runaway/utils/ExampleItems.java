package org.runaway.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;

import java.util.ArrayList;
import java.util.Collections;

public class ExampleItems {

    private static PrisonItem key = ItemManager.getPrisonItem("default_key");
    private static PrisonItem nether_star = ItemManager.getPrisonItem("star");

    public static ItemStack glass(int data, String name) {
        return new Item.Builder(Material.STAINED_GLASS_PANE).data((short) data).name(name).build().item();
    }

    public static ItemStack glass(int data) {
        return glass(data, " ");
    }

    public static PrisonItem getKeyBuilder() {
        return key;
    }

    public static PrisonItem getNetherStarBuilder() {
        return nether_star;
    }

    public static ItemStack unserializerString(String s) {
        String[] split = s.split("-");
        ArrayList<String> lores = null;
        if (!split[4].equals("null")) {
            String[] lore = split[4].split("/");
            lores = new ArrayList<>();
            Collections.addAll(lores, lore);
        }
        return new Item.Builder(Material.valueOf(split[0])).
                data(Short.parseShort(split[1])).
                amount(Integer.parseInt(split[3])).
                name(split[2]).lore(new Lore.BuilderLore().addList(lores).build())
                .build().item();
    }

    public static ItemStack unserializerLocationItem(String s) {
        String[] split = s.split("-");
        return new Item.Builder(Material.valueOf(split[0])).
                data(Short.parseShort(split[1])).
                name(split[2]).lore(new Lore.BuilderLore().addString("&dСпециальный пропуск")
                .addString("&7>> ПКМ чтобы открыть").build())
                .build().item();
    }
}
