package org.runaway.items.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Prison;
import org.runaway.enums.StatType;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.Formatter;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NbtFormatter;
import org.runaway.utils.ItemUtils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter @Builder
public class DefaultParameter implements Parameter {
    String loreString, nbtString;
    @Setter
    Formatter defaultLoreFormatter, defaultNbtFormatter, defaultNameFormatter;
    int priority;
    boolean preSpace, mutable;
    StatType statType;

    @Override
    public Function<PrisonItem, ItemStack> getInitialParameterApplier() {
        return (prisonItem -> {
            ItemStack itemStack = prisonItem.getItemStack();
            if(getLoreString() != null && getDefaultLoreFormatter() == null) {
                setDefaultLoreFormatter(LoreFormatter.builder().loreString(getLoreString()).build());
            }
            if(preSpace) {
                itemStack = ItemUtils.addLore(itemStack, "&r");
            }
            if(getDefaultLoreFormatter() != null) {
                itemStack = getDefaultLoreFormatter().apply(itemStack);
            }
            if(getNbtString() != null && getDefaultNbtFormatter() == null) {
                setDefaultNbtFormatter(NbtFormatter.builder().nbtString(getNbtString()).build());
            }
            if(getDefaultNbtFormatter() != null) {
                itemStack = getDefaultNbtFormatter().apply(itemStack);
            }
            if(getDefaultNameFormatter() != null) {
                itemStack = getDefaultNameFormatter().apply(itemStack);
            }
            prisonItem.setItemStack(itemStack);
            return itemStack;
        });
    }

    @Override
    public BiFunction<ItemStack, Object[], Object> getParameterGetter() {
        return ((itemStack, objects) -> {
            return ItemManager.getValueByNbt(itemStack, objects == null ?
                    getDefaultNbtFormatter().getString() :
                    String.format(getDefaultNbtFormatter().getString(), objects), getStatType());
        });
    }

    @Override
    public ItemStack changeValues(ItemStack itemStack, Object value) {
        if(getDefaultLoreFormatter() != null) {
            getDefaultLoreFormatter().apply(itemStack, value);
        }
        if(getDefaultNbtFormatter() != null) {
            getDefaultNbtFormatter().apply(itemStack, value);
        }
        if(getDefaultNameFormatter() != null) {
            getDefaultNameFormatter().apply(itemStack, value);
        }
        return itemStack;
    }
}
