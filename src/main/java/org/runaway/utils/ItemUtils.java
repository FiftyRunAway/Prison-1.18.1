package org.runaway.utils;

import com.gmail.filoghost.holographicdisplays.util.nbt.NBTTag;
import lombok.NonNull;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
        List<String> itemLore = itemMeta.getLore();
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
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        } else {
            tag = nmsStack.getTag();
        }
        NBTBase nbtBase = new NBTTagString(value.toString());
        if(value instanceof Integer) {
            nbtBase = new NBTTagInt((int) value);
        } else if(value instanceof Double) {
            nbtBase = new NBTTagDouble((double) value);
        } else if(value instanceof Long) {
            nbtBase = new NBTTagLong((long) value);
        } else if(value instanceof Short) {
            nbtBase = new NBTTagShort((short) value);
        } else if(value instanceof Boolean) {
            nbtBase = new NBTTagByte((byte)((boolean) value ? 1 : 0));
        }
        tag.set(stringTag, nbtBase);
        nmsStack.setTag(tag);
        ItemStack result = CraftItemStack.asCraftMirror(nmsStack);
        item.setItemMeta(result.getItemMeta());
        nmsStack.a();
        return result;
    }

    public static ItemStack addItemTag(ItemStack item, String stringTag) {
        return addItemTag(item, stringTag, "1");
    }

    public static boolean containsNbtTag(ItemStack itemStack, String tag) {
        return containsNbtTag(itemStack, tag, "1");
    }

    public static Object getNbtTag(@NonNull ItemStack itemStack, String tag, StatType statType) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound;
        if (!nmsStack.hasTag()) {
            nbtTagCompound = new NBTTagCompound();
            nmsStack.setTag(nbtTagCompound);
            return "";
        } else {
            nbtTagCompound = nmsStack.getTag();
        }
        if (!nbtTagCompound.hasKey(tag) || nbtTagCompound.get(tag).isEmpty()) {
            return "";
        } else {
            if(statType == StatType.STRING || statType == null) {
                return nbtTagCompound.getString(tag);
            } else if(statType == StatType.INTEGER) {
                return nbtTagCompound.getInt(tag);
            } else if(statType == StatType.DOUBLE) {
                return nbtTagCompound.getDouble(tag);
            } else if(statType == StatType.BOOLEAN) {
                return nbtTagCompound.getBoolean(tag);
            } else {
                return nbtTagCompound.get(tag).toString();
            }
        }
    }

    public static boolean containsNbtTag(@NonNull ItemStack itemStack, String tag, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound;
        if (!nmsStack.hasTag()) {
            nbtTagCompound = new NBTTagCompound();
            nmsStack.setTag(nbtTagCompound);
            return false;
        } else {
            nbtTagCompound = nmsStack.getTag();
        }
        return nbtTagCompound.get(tag) != null && nbtTagCompound.get(tag).toString().equalsIgnoreCase(value);
    }

}
