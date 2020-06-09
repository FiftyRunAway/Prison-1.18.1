package org.runaway.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TreasureFindEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private boolean cancelled;

    public TreasureFindEvent(Player player) {
        this.player = player;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
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
