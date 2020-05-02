package org.runaway.achievements;

import org.bukkit.entity.Player;

public interface Reward {

    Reward setReward(Object object);
    Object getReward();
    Class getTypes();
    void giveReward(Player player);
    String rewardTitle();
}
