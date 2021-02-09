package org.runaway.rewards;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.PrisonItem;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Builder
public class LootItem implements IReward {
    private PrisonItem prisonItem;
    private float chance;
    private int minAmount, maxAmount, amount;
    private int probability;

    @Override
    public ItemStack getItemStack() {
        if(getAmount() == 0) {
            return getPrisonItem().getItemStack(getMaxAmount());
        } else {
            return getPrisonItem().getItemStack(getAmount());
        }
    }

    @Override
    public void giveReward(Gamer gamer) {
        int amount = getAmount();
        if(getMinAmount() != 0) {
            amount = ThreadLocalRandom.current().nextInt(getMinAmount(), getMaxAmount());
        }
        gamer.addItem(getPrisonItem().getItemStack(amount), "REWARD");
    }

    @Override
    public String getName() {
        return getPrisonItem().getName() + " &6x" + (getMaxAmount() == 0 ? getAmount() : getMinAmount() + "-" + getMaxAmount());
    }
}