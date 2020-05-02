package org.runaway.menu;

import org.bukkit.ChatColor;

public enum MenuLang {

    PAGE_NOT_FOUND("&cError! Page %name% was not found!");

    private String lang;
    MenuLang(String lang){
        this.lang = lang;
    }

    public String getLang() {
        return ChatColor.translateAlternateColorCodes('&', lang);
    }
}
