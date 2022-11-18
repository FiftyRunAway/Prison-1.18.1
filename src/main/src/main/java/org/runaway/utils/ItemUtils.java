package org.runaway.utils;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.enums.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemStack addLore(@NonNull ItemStack item, String... lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore2 = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
        lore2.addAll(Utils.color(Arrays.stream(lore).collect(Collectors.toList())));
        itemMeta.setLore(lore2);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack addLore(@NonNull ItemStack item, List<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore2 = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
        lore2.addAll(Utils.color(lore));
        itemMeta.setLore(lore2);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack setLoreValue(ItemStack itemStack, String loreString, String loreValue) {
        loreString = Utils.colored(loreString);
        String loreStringStripped = ChatColor.stripColor(loreString).trim();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
        for(int index = 0; index < itemLore.size(); index++) {
            String stringStripped = ChatColor.stripColor(itemLore.get(index));
            if (stringStripped.startsWith(loreStringStripped)) {
                itemLore.set(index, Utils.colored(loreString + loreValue));
                itemMeta.setLore(itemLore);
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }
        }
        return itemStack;
    }

    public static ItemStack addItemTag(@NonNull ItemStack item, String stringTag, Object value) {
        return NBTEditor.set(item, value, stringTag);
    }

    public static ItemStack addItemTag(ItemStack item, String stringTag) {
        return addItemTag(item, stringTag, 1);
    }

    public static boolean containsNbtTag(ItemStack itemStack, String tag) {
        return containsNbtTag(itemStack, tag, 1);
    }

    public static Object getNbtTag(@NonNull ItemStack itemStack, String tag, StatType statType) {
        if (!NBTEditor.contains(itemStack, tag)) return "";

        if (statType == StatType.STRING || statType == null) {
            return NBTEditor.getString(itemStack, tag);
        } else if(statType == StatType.INTEGER) {
            return NBTEditor.getInt(itemStack, tag);
        } else if(statType == StatType.DOUBLE) {
            return NBTEditor.getDouble(itemStack, tag);
        } else if(statType == StatType.BOOLEAN) {
            return NBTEditor.getBoolean(itemStack, tag);
        } else {
            return NBTEditor.getString(itemStack, tag);
        }
    }

    public static boolean containsNbtTag(@NonNull ItemStack itemStack, String tag, Object value) {
        return NBTEditor.contains(itemStack, value, tag);
    }
}
