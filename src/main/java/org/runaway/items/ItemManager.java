package org.runaway.items;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.enums.StatType;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.parameters.Parameter;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.runes.Rune;
import org.runaway.runes.RuneManager;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Utils;

import java.util.*;

public class ItemManager {
    @Getter
    private Map<String, PrisonItem> prisonItemMap;
    @Getter
    private ParameterManager parameterManager;

    public ItemManager() {
        this.prisonItemMap = new HashMap();
        this.parameterManager = new ParameterManager();
    }

    public void addPrisonItem(PrisonItem prisonItem) {
        prisonItem.setTechName(prisonItem.getVanillaName() + (prisonItem.getItemLevel() == 0 ? "" : "_" + prisonItem.getItemLevel()));
        ItemStack finalItem = prisonItem.getVanillaItem().clone();
        if(finalItem.getItemMeta().hasLore()) {
            ItemUtils.addLore(finalItem, "&r");
        }
        prisonItem.setItemStack(finalItem);
        prisonItem.setMutableParameters(new ArrayList());
        Map<Integer, Parameter> parameterMap = new TreeMap<>();
        prisonItem.getParameters().forEach(parameter -> parameterMap.put(parameter.getPriority(), parameter));
        if(prisonItem.getItemLevel() != 0) {
            parameterMap.put(0, parameterManager.getItemLevelParameter(prisonItem.getItemLevel()));
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

    public PrisonItem getPrisonItem(ItemStack itemStack) {
        String techName = (String) ItemUtils.getNbtTag(itemStack, "techName", StatType.STRING);
        return getPrisonItem(techName);
    }

    public PrisonItem getPrisonItem(String techName) {
        return prisonItemMap.get(techName);
    }

    public Object getValueByNbt(ItemStack itemStack, String nbt, StatType statType) {
        return ItemUtils.getNbtTag(itemStack, nbt, statType);
    }

    public ItemStack initValue(ItemStack itemStack, String nbt, String loreString, String loreValue, String nbtValue) {
        ItemUtils.addLore(itemStack, loreString + loreValue);
        itemStack = ItemUtils.addItemTag(itemStack, nbt, nbtValue);
        return itemStack;
    }
    public ItemStack setValue(ItemStack itemStack, String nbt, String loreString, String loreValue, String nbtValue) {
        loreString = Utils.colored(loreString);
        String loreStringStripped = ChatColor.stripColor(loreString).trim();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        for(int index = 0; index < itemLore.size(); index++) {
            String stringStripped = itemLore.get(index);
            if (stringStripped.startsWith(loreStringStripped)) {
                itemLore.set(index, Utils.colored(loreString + loreValue));
                itemMeta.setLore(itemLore);
                itemStack.setItemMeta(itemMeta);
                itemStack = ItemUtils.addItemTag(itemStack, nbt, nbtValue);
                return itemStack;
            }
        }
        return itemStack;
    }

    public String getOwner(ItemStack itemStack) {
        return parameterManager.getOwnerParameter().getParameterGetter().apply(itemStack, null).toString();
    }

    public boolean isOwner(Gamer gamer, ItemStack itemStack) {
        return gamer.getName().equalsIgnoreCase(getOwner(itemStack));
    }

    public ItemStack applyNewParameter(ItemStack itemStack, Parameter parameter, Object value) {
        return parameter.changeValues(itemStack, value);
    }

    public ItemStack initItem(ItemStack itemStack, Gamer gamer) {
        itemStack = getParameterManager().getOwnerParameter().changeValues(itemStack, gamer.getName());
        return itemStack;
    }
}
