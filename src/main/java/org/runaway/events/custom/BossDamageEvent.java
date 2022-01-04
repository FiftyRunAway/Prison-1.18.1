package org.runaway.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.runaway.entity.IMobController;

public class BossDamageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player source;
    private IMobController damaged;
    private double damage;
    private boolean cancelled;

    public BossDamageEvent(Player player, IMobController entity, double damage) {
        this.source = player;
        this.damaged = entity;
        this.cancelled = false;
        this.damage = damage;
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

    public double getDamage() {
        return damage;
    }

    public Player getSource() {
        return source;
    }
}
