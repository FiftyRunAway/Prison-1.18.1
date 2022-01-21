package org.runaway.entity.skills;

import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.runaway.entity.IMobController;
import org.runaway.tasks.SyncRepeatTask;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RepetitiveSkill extends CustomSkill {

    private int ticks;

    public RepetitiveSkill(Consumer<Entity> consumer, int ticks) {
        super(((entity, player) -> {
            consumer.accept(entity);
        }));
        this.ticks = ticks;
    }

    @Override
    public void apply(IMobController iMobController) {
        iMobController.getMobTasks().add(new SyncRepeatTask(() -> {
            getConsumer().accept(iMobController.getNmsEntity(), null);
        }, ticks, ticks));
    }

    @Override
    public BiConsumer<Entity, Player> getConsumer() {
        return super.getConsumer();
    }
}