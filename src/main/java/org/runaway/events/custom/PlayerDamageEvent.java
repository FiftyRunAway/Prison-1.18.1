package org.runaway.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDamageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player source;
    private Player damaged;
    private boolean cancelled;

    public PlayerDamageEvent(Player source, Player damaged) {
        this.source = source;
        this.damaged = damaged;
        this.cancelled = false;
    }

    public Player getPlayerSource() {
        return source;
    }

    public Player getPlayerDamaged() { return damaged; }

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
