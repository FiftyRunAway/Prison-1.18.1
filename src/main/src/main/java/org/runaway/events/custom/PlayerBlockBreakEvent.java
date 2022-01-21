package org.runaway.events.custom;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.runaway.events.BlockBreak;
import org.runaway.mines.Mine;

public class PlayerBlockBreakEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Block block;
    private boolean cancelled;

    public PlayerBlockBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
        this.cancelled = false;
    }

    public boolean fromMine(Mine mine) {
        Location end = new Location(mine.getMineLocation().getWorld(),
                mine.getMineLocation().getBlockX() - mine.getDiametr(),
                mine.getMineLocation().getBlockY() - mine.getHeight(),
                mine.getMineLocation().getBlockZ() - mine.getDiametr());
        return BlockBreak.isInRegion(mine.getMineLocation(), end, getBlock().getLocation());
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() { return block; }

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
