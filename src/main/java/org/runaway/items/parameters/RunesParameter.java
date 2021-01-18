package org.runaway.items.parameters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Prison;
import org.runaway.items.formatters.Formatter;
import org.runaway.runes.Rune;
import org.runaway.runes.RuneManager;
import org.runaway.utils.ItemUtils;

import java.util.List;
import java.util.function.UnaryOperator;

@Getter
public class RunesParameter extends DefaultParameter {
    int amount;
    List<String> defaultRunes;

    RunesParameter(String nbtString, int priority, int amount, List<String> defaultRunes) {
        super(null, nbtString, null, null, priority);
        this.amount = amount;
        this.defaultRunes = defaultRunes;
        this.priority = priority;
    }

    @Override
    public UnaryOperator<ItemStack> getInitialParameterApplier() {
        ParameterManager parameterManager = Prison.getInstance().getItemManager().getParameterManager();
        return (itemStack -> {
            ItemUtils.addLore(itemStack, "&r", String.format(parameterManager.getRunesAmountString(), getAmount()));
            ItemUtils.addItemTag(itemStack, "runesAmount", getAmount());
            for (int i = 1; i < (getAmount() + 1); i++) {
                if (getDefaultRunes() != null && getDefaultRunes().size() > (i - 1)) {
                    String runeString = getDefaultRunes().get(i - 1);
                    if (runeString == null) continue;
                    Rune rune1 = RuneManager.getRune(runeString);
                    if (rune1 == null) continue;
                    ItemUtils.addItemTag(itemStack, "rune" + i, rune1.getTechName());
                    ItemUtils.addLore(itemStack, String.format(parameterManager.getRuneInfoString(), i, rune1.getName()));
                } else {
                    ItemUtils.addItemTag(itemStack, "rune" + i, "");
                    ItemUtils.addLore(itemStack, String.format(parameterManager.getRuneInfoString(), i, "-"));
                }
            }
            ItemUtils.addLore(itemStack, "&r");
            return itemStack;
        });
    }
}
