package org.runaway.enums;

import org.bukkit.ChatColor;

/*
 * Created by _RunAway_ on 11.2.2019
 */

public enum EMode {
    EASY(50, "Лёгкий", ChatColor.YELLOW),
    NORMAL(100, "Нормальный", ChatColor.GREEN),
    HARD(150, "Хардкорный", ChatColor.RED);

    private double percent;
    private String name;
    private ChatColor color;

    EMode(double percent, String name, ChatColor color) {
        this.percent = percent;
        this.name = name;
        this.color = color;
    }

    public double getPercent() {
        return percent;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }
}
