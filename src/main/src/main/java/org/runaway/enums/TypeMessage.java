package org.runaway.enums;

import org.bukkit.ChatColor;

/*
 * Created by _RunAway_ on 15.1.2019
 */


public enum  TypeMessage {
    ERROR(ChatColor.DARK_RED),
    INFO(ChatColor.YELLOW),
    SUCCESS(ChatColor.GREEN);

    private ChatColor color;

    private TypeMessage(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
