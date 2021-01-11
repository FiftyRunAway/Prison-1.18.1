package org.runaway.enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/*
 * Created by _RunAway_ on 25.1.2019
 */

public enum FactionType {
    DEFAULT(-1, "Нет", ChatColor.WHITE, "default", null),
    WHITE(0, "Белые", ChatColor.GRAY, "white", Material.IRON_SWORD),
    BLACK(1, "Негры", ChatColor.DARK_GRAY, "black", Material.STONE_SWORD),
    YELLOW(2, "Азиаты", ChatColor.YELLOW, "yellow", Material.GOLD_SWORD);

    private int id;
    private ChatColor color;
    private String name;
    private String inConfig;

    private Material icon;

    FactionType(int id, String name, ChatColor color, String inConfig, Material icon) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.inConfig = inConfig;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public ChatColor getColor() {
        return color;
    }


    public String getName() {
        return name;
    }

    public String getConfigName() { return inConfig; }

    public Material getIcon() {
        return icon;
    }
}
