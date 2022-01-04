package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.items.ItemManager;

public class KeysReward extends IReward {

    private int value;

    @Override
    protected void init() {
        this.value = this.getIntValue(0);
        this.setValue(this.value);
    }

    @Override
    protected void getReward(Gamer gamer) {
        gamer.addItem(ItemManager.getPrisonItem("defaultKey").getItemStack(this.value));
    }

    @Override
    public String getName() {
        return "&7Ключ для обычного сундука";
    }

    @Override
    protected String getDescription() {
        return "Вы сможете открыть обычный сундук на спавне";
    }

    @Override
    protected Material getType() {
        return Material.GHAST_TEAR;
    }

    @Override
    public String getArgumentsString() {
        return "keys_value";
    }
}
