package org.runaway.events.custom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerFishingEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Entity entity;
    private boolean cancelled;

    public PlayerFishingEvent(Player player, Entity entity) {
        this.player = player;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getEntity() { return entity; }

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
