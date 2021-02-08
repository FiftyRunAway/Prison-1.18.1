package org.runaway.cases;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.menu.IMenu;
import org.runaway.menu.type.StandardMenu;
import org.runaway.rewards.IReward;
import org.runaway.utils.ItemUtils;

import java.util.List;
import java.util.Random;

public interface CaseRefactored {
    List<IReward> getRewardItems();

    String getTechName();

    String getName();

    int getRollTimes();

    int getMinLevel();

    Random getRandom();

    default IReward generateReward() {
        return getRewardItems().get(getRandom().nextInt(getRewardItems().size()));
    }

    default void open(Gamer gamer) {
        //TODO
    }

    default IMenu getChancesMenu(Gamer gamer) {
        List<IReward> rewards = getRewardItems();
        IMenu standardMenu = StandardMenu.create((rewards.size() / 9) + 1, getName() + " Шансы");
        for (int i = 0; i < rewards.size(); i++) {
            ItemStack rewardItem = rewards.get(i).getItemStack();
            ItemStack itemChance = rewardItem.clone();
            ItemUtils.addLore(itemChance, "&r", "&bШанс выпадения: " + String.format("§a%.2f", (rewards.get(i).getProbability() / rewards.size()) * 100) + "%");
            standardMenu.getInventory().setItem(i, itemChance);
        }
        standardMenu.open(gamer);
        return standardMenu;
    }
}
