package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.donate.Donate;
import org.runaway.donate.DonateStat;

public class MoneyReward extends IReward {

    private int value;

    @Override
    protected void init() {
        this.value = this.getIntValue(0);
        this.setValue(this.value);
    }

    @Override
    protected void getReward(Gamer gamer) {
        gamer.increaseIntStatistics(DonateStat.BPOINT, this.value);
    }

    @Override
    public String getName() {
        return "&eОчки Боевого пропуска";
    }

    @Override
    protected String getDescription() {
        return "Можно потратить на новый боевой пропуск";
    }

    @Override
    protected Material getType() {
        return Material.SUNFLOWER;
    }

    @Override
    public String getArgumentsString() {
        return "money_value";
    }
}
