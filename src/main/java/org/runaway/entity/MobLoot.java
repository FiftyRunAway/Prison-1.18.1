package org.runaway.entity;

import org.bukkit.Location;
import org.runaway.Gamer;
import org.runaway.rewards.LootItem;

import java.util.List;
import java.util.Map;

public interface MobLoot {
    void drop(Map<Gamer, Double> damageList, Location location, Attributable attributable);

    List<LootItem> getLootItems();

    int getMinMoney();

    int getMaxMoney();

    List<String> getLootLore();

}
