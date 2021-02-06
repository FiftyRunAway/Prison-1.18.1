package org.runaway.entity.skills;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.entity.Player;
import org.runaway.entity.IMobController;
import org.runaway.tasks.SyncRepeatTask;

import java.util.function.BiConsumer;

public class DamageSkill extends CustomSkill {

    public DamageSkill(BiConsumer<Entity, Player> consumer) {
        super(consumer);
    }
}
