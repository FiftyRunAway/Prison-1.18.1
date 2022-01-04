package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.items.ItemManager;

public class ItemReward extends IReward {

    private String value;

    private String name;
    private Material type;

    @Override
    protected void init() {
        this.value = this.getStringValue(0);
        this.setStringValue(true);
        this.setValue(this.value);

        ItemStack item = ItemManager.getPrisonItem(this.value).getItemStack();
        this.name = item.getItemMeta().getDisplayName();
        this.type = item.getType();

    }

    @Override
    protected void getReward(Gamer gamer) {
        gamer.addItem(this.value);
    }

    @Override
    public String getName() {
        return "&eПредмет &7• " + name;
    }

    @Override
    protected String getDescription() {
        return "Этот предмет, скорее всего, ускорит ваше развитие";
    }

    @Override
    protected Material getType() {
        return this.type;
    }

    @Override
    public String getArgumentsString() {
        return "techname_from_upgrades.yml";
    }
}
