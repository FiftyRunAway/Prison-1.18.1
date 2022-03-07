package org.runaway.rewards;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.runes.utils.Rune;
import org.runaway.utils.ItemBuilder;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Builder
public class RuneReward implements IReward {
    private float chance;
    private int probability;
    private Rune rune;

    @Override
    public ItemStack getItemStack() {
        return getRunePi().getItemStack(1);
    }

    @Override
    public void giveReward(Gamer gamer) {
        gamer.addItem(getRunePi().getItemStack(1), "REWARD");
    }

    @Override
    public String getName() {
        return getRunePi().getName();
    }

    private PrisonItem getRunePi() {
        return ItemManager.getPrisonItem(getRune().getTechName() + "Rune");
    }
}
