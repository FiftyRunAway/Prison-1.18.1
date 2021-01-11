package org.runaway.fishing;

import org.bukkit.ChatColor;

public enum EFishType {
    ORDINARY(ChatColor.GRAY),
    RARE(ChatColor.GREEN),
    EPIC(ChatColor.LIGHT_PURPLE),
    LEGENDARY(ChatColor.GOLD);

    private ChatColor color;

    EFishType(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
