package org.runaway.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.runaway.enums.TypeMessage;

import java.text.SimpleDateFormat;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public final class Vars {

    private static String prefix;
    private static String site;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy '-' HH:mm");

    public static String getPrefix() { return Utils.colored(prefix); }

    public static String getSite() {
        return site;
    }

    public static void sendSystemMessage(TypeMessage typeMessage, String text) {
        Bukkit.getConsoleSender().sendMessage(typeMessage.getColor() + "[" + Bukkit.getServer().getPluginManager().getPlugin(namePlugin()).getName() + "] " + text);
    }

    public static String namePlugin() {
        return "Prison";
    }

    private static String bold() {
        return ChatColor.BOLD + "";
    }

    public static void setPrefix(String prefix) {
        Vars.prefix = prefix;
    }

    public static void setSite(String site) {
        Vars.site = site;
    }
}
