package org.runaway.entity.skills;

import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class DamageSkill extends CustomSkill {

    public DamageSkill(BiConsumer<Entity, Player> consumer) {
        super(consumer);
    }

    @Override
    public BiConsumer<Entity, Player> getConsumer() {
        return super.getConsumer();
    }
}
