package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.donate.Donate;
import org.runaway.enums.EStat;

public class MoneyReward extends IReward {

    private int value;

    @Override
    protected void init() {
        this.value = this.getIntValue(0);
        this.setValue(this.value);
    }

    @Override
    protected void getReward(Gamer gamer) {
        Donate.depositDonateMoney(gamer.getName(), this.value, false);
    }

    @Override
    protected String getName() {
        return "&eДеньги на счёт доната";
    }

    @Override
    protected String getDescription() {
        return "Можно потратить на новый боевой пропуск";
    }

    @Override
    protected Material getType() {
        return Material.DOUBLE_PLANT;
    }

    @Override
    public String getArgumentsString() {
        return "money_value";
    }
}
