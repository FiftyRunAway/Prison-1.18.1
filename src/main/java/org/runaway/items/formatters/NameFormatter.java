package org.runaway.items.formatters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Utils;

@Getter @Builder
public class NameFormatter implements Formatter {
    Object finalValue;
    Object[] replaceObjects;

    public String format(String string) {
        return String.format(string, getReplaceObjects());
    }

    public ItemStack apply(ItemStack itemStack, String string) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Utils.colored(format(string) + (getFinalValue() == null ? "" : getFinalValue().toString())));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
