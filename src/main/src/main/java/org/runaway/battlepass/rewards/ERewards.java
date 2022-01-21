package org.runaway.battlepass.rewards;

import org.runaway.battlepass.IReward;

public enum ERewards {
    KEYS(KeysReward.class),
    MONEY(MoneyReward.class),
    GMONEY(GMoneyReward.class),
    BOOSTER(BoosterReward.class),
    ITEM(ItemReward.class);

    private Class<? extends IReward> reward;

    ERewards(Class<? extends IReward> reward) {
        this.reward = reward;
    }

    public Class<? extends IReward> getRewardClass() {
        return this.reward;
    }
}
