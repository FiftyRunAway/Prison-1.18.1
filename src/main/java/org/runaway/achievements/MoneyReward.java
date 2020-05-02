package org.runaway.achievements;

import org.bukkit.entity.Player;
import org.runaway.Main;
import org.runaway.enums.MoneyType;

public class MoneyReward implements Reward {

    private int reward = 0;

    @Override
    public Reward setReward(Object object) {
        this.reward = (int) object;
        return this;
    }

    @Override
    public Object getReward() {
        return reward;
    }

    @Override
    public Class getTypes() {
        return Integer.class;
    }

    @Override
    public void giveReward(Player player) {
        Main.gamers.get(player.getUniqueId()).depositMoney((int)getReward());
    }

    @Override
    public String rewardTitle() {
        return "&a" + reward + " " + MoneyType.RUBLES.getShortName() + "&r";
    }
}
