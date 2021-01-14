package org.runaway.events.custom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.runaway.fishing.EFishType;

public class PlayerFishingEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private EFishType reward;
    private boolean cancelled;

    public PlayerFishingEvent(Player player, EFishType fishType) {
        this.player = player;
        this.cancelled = false;
        this.reward = fishType;
    }

    public Player getPlayer() {
        return player;
    }

    public EFishType getReward() {
        return reward;
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
