package org.runaway.entity.skills;

import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.runaway.entity.IMobController;

import java.util.function.BiConsumer;

public interface MobSkill {
    BiConsumer<Entity, Player> getConsumer();

    void apply(IMobController iMobController);
}
