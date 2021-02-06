package org.runaway.entity.skills;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.entity.Player;
import org.runaway.entity.IMobController;

import java.util.function.BiConsumer;

@Getter
public class CustomSkill implements MobSkill {
    private BiConsumer<Entity, Player> consumer;

    public CustomSkill(BiConsumer<Entity, Player> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(IMobController iMobController) {

    }
}
