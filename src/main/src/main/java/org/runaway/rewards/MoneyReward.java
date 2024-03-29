package org.runaway.rewards;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.enums.MoneyType;
import org.runaway.items.Item;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Builder
public class MoneyReward implements IReward {
    private double amount, minAmount, maxAmount;
    private float chance;
    private int probability;
    private boolean dependOnLevel;

    @Override
    public ItemStack getItemStack() {
        return new Item.Builder(Material.EMERALD).name("&a" + (getMaxAmount() == 0 ? getAmount() : getMinAmount() + "-" + getMaxAmount()) + MoneyType.RUBLES.getShortName()).build().item();
    }

    @Override
    public void giveReward(Gamer gamer) {
        double amount = getAmount();
        if(getMaxAmount() != 0) {
            amount = ThreadLocalRandom.current().nextDouble(getMinAmount(), getMaxAmount());
        }
        gamer.depositMoney(dependOnLevel ? (amount * gamer.getLevel()) : amount, true);
    }

    @Override
    public String getName() {
        return "&a" + (getMaxAmount() == 0 ? getAmount() : getMinAmount() + "-" + getMaxAmount()) + MoneyType.RUBLES.getShortName();
    }
}
