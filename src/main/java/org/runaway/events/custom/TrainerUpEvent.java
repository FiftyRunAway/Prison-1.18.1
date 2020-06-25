package org.runaway.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.runaway.trainer.TypeTrainings;

public class TrainerUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private boolean cancelled;
    private TypeTrainings typeTrainings;

    public TrainerUpEvent(Player player, TypeTrainings type) {
        this.player = player;
        this.typeTrainings = type;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public TypeTrainings getTypeTraining() {
        return typeTrainings;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
