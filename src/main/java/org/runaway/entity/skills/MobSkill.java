package org.runaway.entity.skills;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.entity.Player;
import org.runaway.entity.IMobController;

import java.util.function.BiConsumer;

public interface MobSkill {
    BiConsumer<Entity, Player> getConsumer();

    void apply(IMobController iMobController);
}
