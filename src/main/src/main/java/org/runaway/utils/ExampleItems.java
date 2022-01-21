package org.runaway.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;

import java.util.ArrayList;
import java.util.Collections;

public class ExampleItems {

    public static ItemStack glass(Material pane, String name) {
        return new Item.Builder(pane).name(name).build().item();
    }

    public static ItemStack glass(Material pane) {
        return glass(pane, " ");
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
}
