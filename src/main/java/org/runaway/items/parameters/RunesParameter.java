package org.runaway.items.parameters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.formatters.Formatter;
import org.runaway.runes.Rune;
import org.runaway.runes.RuneManager;
import org.runaway.utils.ItemUtils;

import java.util.List;
import java.util.function.UnaryOperator;

@Builder @Getter
public class RunesParameter extends DefaultParameter {
    int amount, priority;
    List<String> defaultRunes;


    @Override
    public UnaryOperator<ItemStack> getInitialParameterApplier() {
        return (itemStack -> {
            ItemUtils.addLore(itemStack, "&r", String.format(getParameterManager().getRunesAmountString(), getAmount()));
            ItemUtils.addItemTag(itemStack, "runesAmount", getAmount());
            for (int i = 1; i < (getAmount() + 1); i++) {
                if (getDefaultRunes() != null && getDefaultRunes().size() > (i - 1)) {
                    String runeString = getDefaultRunes().get(i - 1);
                    if (runeString == null) continue;
                    Rune rune1 = RuneManager.getRune(runeString);
                    if (rune1 == null) continue;
                    ItemUtils.addItemTag(itemStack, "rune" + i, rune1.getTechName());
                    ItemUtils.addLore(itemStack, String.format(getParameterManager().getRunesAmountString(), i, rune1.getName()));
                } else {
                    ItemUtils.addItemTag(itemStack, "rune" + i, "");
                    ItemUtils.addLore(itemStack, String.format(getParameterManager().getRunesAmountString(), i, "-"));
                }
            }
            return itemStack;
        });
    }

    @Override
    public String getNbtString() {
        return "rune%d";
    }
}
