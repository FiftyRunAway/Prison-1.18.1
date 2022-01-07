package org.runaway.events.custom;

import net.minecraft.server.v1_12_R1.EntityMonster;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.runaway.utils.Utils;

public class BossSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String name;
    private EntityMonster em;
    private boolean cancelled;

    public BossSpawnEvent(String name) {
        this.name = Utils.colored(name);
        this.em = em;
        this.cancelled = false;
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

    public EntityMonster getEntityMonster() {
        return em;
    }

    public String getName() {
        return name;
    }
}
