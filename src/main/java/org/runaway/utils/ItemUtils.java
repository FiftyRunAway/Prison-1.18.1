package org.runaway.utils;

import lombok.NonNull;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemStack addLore(@NonNull ItemStack item, String... lore) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore2 = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList();
        lore2.addAll(Arrays.stream(lore).collect(Collectors.toList()));
        itemMeta.setLore(lore2);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack addItemTag(ItemStack item, String stringTag, int value) {
        return addItemTag(item, stringTag, value + "");
    }

    public static ItemStack addItemTag(@NonNull ItemStack item, String stringTag, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        } else {
            tag = nmsStack.getTag();
        }
        tag.remove(stringTag);
        tag.set(stringTag, new NBTTagString(value));
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    public static ItemStack addItemTag(ItemStack item, String stringTag) {
        return addItemTag(item, stringTag, "1");
    }

    public static boolean containsNbtTag(ItemStack itemStack, String tag) {
        return containsNbtTag(itemStack, tag, "1");
    }

    public static String getNbtTag(@NonNull ItemStack itemStack, String tag) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound;
        if (!nmsStack.hasTag()) {
            nbtTagCompound = new NBTTagCompound();
            nmsStack.setTag(nbtTagCompound);
            return "";
        } else {
            nbtTagCompound = nmsStack.getTag();
        }
        if (nbtTagCompound.getString(tag) == null || nbtTagCompound.getString(tag).isEmpty()) {
            return "";
        } else {
            return nbtTagCompound.getString(tag);
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
        return nbtTagCompound.get(tag) != null && nbtTagCompound.getString(tag).equalsIgnoreCase(value);
    }

}
