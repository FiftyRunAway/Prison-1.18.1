package org.runaway.rewards;

import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;

public interface IReward {
    ItemStack getItemStack();

    void giveReward(Gamer gamer);

    String getName();

    default float getChance() {
        throw new UnsupportedOperationException();
    }

    default int getProbability() {
        throw new UnsupportedOperationException();
    }
}
