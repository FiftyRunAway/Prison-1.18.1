package org.runaway.fishing;

import org.bukkit.ChatColor;

public enum EFishType {
    LEGENDARY(ChatColor.GOLD, "&eУДАЧЛИВЫЙ", "&6Легендарная рыба"),
    EPIC(ChatColor.LIGHT_PURPLE, "&aНЕПЛОХО", "&dЭпическая рыба"),
    RARE(ChatColor.GREEN, "&dСРЕДНЕНЬКО", "&aРедкая рыба"),
    ORDINARY(ChatColor.GRAY, "&fХУДО", "&7Обычная рыба"),
    NONE_REWARD(ChatColor.DARK_GRAY, "&8Как-так то?", "&cПусто"),
    TRY_AGAIN(ChatColor.WHITE, "&4Не сегодня!", "&fЗавтра повезёт");

    private ChatColor color;
    private String name, rewardName;

    EFishType(ChatColor color, String name, String rewardName) {
        this.color = color;
        this.name = name;
        this.rewardName = rewardName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getRewardName() {
        return rewardName;
    }
}
