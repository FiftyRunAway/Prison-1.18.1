package org.runaway.achievements;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.managers.GamerManager;

public class ItemReward implements Reward {

    private ItemStack reward = null;

    @Override
    public Reward setReward(Object object) {
        this.reward = (ItemStack) object;
        return this;
    }

    @Override
    public Object getReward() {
        return reward;
    }

    @Override
    public Class getTypes() {
        return ItemStack.class;
    }

    @Override
    public void giveReward(Player player) {
        GamerManager.getGamer(player).addItem((ItemStack) getReward());
    }

    @Override
    public String rewardTitle() {
        return reward.getItemMeta().getDisplayName() + " &7(&a" + reward.getAmount() + " шт.&7) &r";
    }
}
