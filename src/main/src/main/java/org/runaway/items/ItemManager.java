package org.runaway.items;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.runaway.Gamer;
import org.runaway.enums.StatType;
import org.runaway.items.parameters.Parameter;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Utils;

import java.util.*;

public class ItemManager {
    @Getter
    private static Map<String, PrisonItem> prisonItemMap = new HashMap<>();

    public static void addPrisonItem(PrisonItem prisonItem) {
        prisonItem.setTechName(prisonItem.getVanillaName() + (prisonItem.getItemLevel() == 0 ? "" : "_" + prisonItem.getItemLevel()));
        ItemStack finalItem = prisonItem.getVanillaItem().clone();
        if(finalItem.getItemMeta().hasLore()) {
            ItemUtils.addLore(finalItem, "&r");
        }
        prisonItem.setItemStack(finalItem);
        prisonItem.setMutableParameters(new ArrayList<>());
        prisonItem.setParameters(new ArrayList<>(prisonItem.getParameters()));
        Map<Integer, Parameter> parameterMap = new TreeMap<>();
        prisonItem.getParameters().forEach(parameter ->
                parameterMap.put(parameter.getPriority(), parameter));
        if(prisonItem.getItemLevel() != 0) {
            parameterMap.put(0, ParameterManager.getItemLevelParameter(prisonItem.getItemLevel()));
        }
        for (Parameter parameter : parameterMap.values()) {
            parameter.getInitialParameterApplier().apply(prisonItem);
            if(parameter.isMutable()) {
                prisonItem.getMutableParameters().add(parameter);
            }
        }
        finalItem = prisonItem.getItemStack();
        finalItem = ItemUtils.addItemTag(finalItem, "techName", prisonItem.getTechName());
        ItemMeta itemMeta = finalItem.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.setUnbreakable(true);
        finalItem.setItemMeta(itemMeta);
        prisonItem.setItemStack(finalItem);
        prisonItem.setName(itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemMeta.getLocalizedName());
        prisonItemMap.put(prisonItem.getTechName(), prisonItem);
    }

    public static PrisonItem getPrisonItem(ItemStack itemStack) {
        String techName = (String) ItemUtils.getNbtTag(itemStack, "techName", StatType.STRING);
        return getPrisonItem(techName);
    }

    public static PrisonItem getPrisonItem(String techName) {
        if(!prisonItemMap.containsKey(techName)) {
            PrisonItem prisonItem = PrisonItem.builder()
                    .itemStack(new ItemBuilder(Material.BARRIER).name("&4" + techName + " Не найдено").build())
                    .category(PrisonItem.Category.HIDDEN)
                    .techName(techName + "NF")
                    .name("&4" + techName + " Не найдено")
                    .build();
            prisonItemMap.put(techName + "NF", prisonItem);
            return prisonItem;
        }
        return prisonItemMap.get(techName);
    }

    public static Object getValueByNbt(ItemStack itemStack, String nbt, StatType statType) {
        return ItemUtils.getNbtTag(itemStack, nbt, statType);
    }

    public static ItemStack initValue(ItemStack itemStack, String nbt, String loreString, String loreValue, String nbtValue) {
        ItemUtils.addLore(itemStack, loreString + loreValue);
        itemStack = ItemUtils.addItemTag(itemStack, nbt, nbtValue);
        return itemStack;
    }
    public static ItemStack setValue(ItemStack itemStack, String nbt, String loreString, String loreValue, String nbtValue) {
        loreString = Utils.colored(loreString);
        String loreStringStripped = ChatColor.stripColor(loreString).trim();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        for(int index = 0; index < itemLore.size(); index++) {
            String stringStripped = ChatColor.stripColor(itemLore.get(index));
            if (stringStripped.contains(loreStringStripped)) {
                itemLore.set(index, Utils.colored(loreString + loreValue));
                itemMeta.setLore(itemLore);
                itemStack.setItemMeta(itemMeta);
                itemStack = ItemUtils.addItemTag(itemStack, nbt, nbtValue);
                return itemStack;
            }
        }
        return itemStack;
    }

    public static String getOwner(ItemStack itemStack) {
        PrisonItem prisonItem = getPrisonItem(itemStack);
        if(prisonItem == null) return null;
        return ParameterManager.getOwnerParameter().getParameterGetter().apply(itemStack, null).toString();
    }

    public static boolean isDropable(ItemStack itemStack) {
        PrisonItem prisonItem = getPrisonItem(itemStack);
        if (prisonItem == null) return true;
        String drop = ParameterManager.getNodropParameter().getParameterGetter().apply(itemStack, null).toString();
        if (drop.length() == 0) return true;
        return Boolean.parseBoolean(drop);
    }

    public static boolean isOwner(Gamer gamer, ItemStack itemStack) {
        return gamer.getName().equalsIgnoreCase(getOwner(itemStack));
    }

    public static ItemStack applyNewParameter(ItemStack itemStack, Parameter parameter, Object value) {
        return parameter.changeValues(itemStack, value);
    }

    public static ItemStack initItem(ItemStack itemStack, Gamer gamer) {
        itemStack = ParameterManager.getOwnerParameter().changeValues(itemStack, gamer.getName());
        return itemStack;
    }
}
