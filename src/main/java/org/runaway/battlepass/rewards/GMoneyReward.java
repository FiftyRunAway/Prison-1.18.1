package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.enums.EStat;

public class GMoneyReward extends IReward {

    private int value;

    @Override
    protected void init() {
        this.value = this.getIntValue(0);
        this.setValue(this.value);
    }

    @Override
    protected void getReward(Gamer gamer) {
        gamer.increaseDoubleStatistics(EStat.MONEY, this.value);
    }

    @Override
    public String getName() {
        return "&eИгровые деньги";
    }

    @Override
    protected String getDescription() {
        return "Можно потратить на любую прокачку и покупку за неигровые деньги";
    }

    @Override
    protected Material getType() {
        return Material.PAPER;
    }

    @Override
    public String getArgumentsString() {
        return "gmoney_value";
    }
}
