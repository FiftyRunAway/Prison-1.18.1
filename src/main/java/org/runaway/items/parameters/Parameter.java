package org.runaway.items.parameters;

import org.bukkit.inventory.ItemStack;
import org.runaway.items.formatters.Formatter;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface Parameter {
    int getPriority();

    UnaryOperator<ItemStack> getInitialParameterApplier();

    ItemStack applyNewValues(ItemStack itemStack, Formatter... formatter);

    BiFunction<ItemStack, Object[], Object> getParameterGetter();

}
