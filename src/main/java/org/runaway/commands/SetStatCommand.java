package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.managers.GamerManager;
import org.runaway.sqlite.DoVoid;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.enums.StatType;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by _RunAway_ on 19.1.2019
 */

public class SetStatCommand extends CommandManager {

    public SetStatCommand() {
        super("setstat", "prison.admin", Arrays.asList("setstat", "setstatistics", "setstats", "статистика"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (p.isOp()) {
            ArrayList<String> st = new ArrayList<>();
            Arrays.stream(EStat.values()).forEach(stats -> st.add(stats.name().toLowerCase()));
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " <игрок> " + st.toString() + " <значение>");
                return;
            }
            boolean isOnline = true;
            Player target = Bukkit.getPlayerExact(args[0]);
            if (!Utils.getPlayers().contains(args[0])) {
                isOnline = false;
            }
            EStat type;
            try {
                type = EStat.valueOf(args[1].toUpperCase());
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Введёный вами вид статистики " + args[1] + " не существует");
                p.sendMessage(ChatColor.RED + "Доступные: " + st.toString());
                return;
            }
            if (isAllow(args[2], type, p)) {
                Object obj = args[2];
                if (isOnline) {
                    addStat(target.getName(), type, obj, true);
                } else {
                    addStat(args[0], type, obj, false);
                }
                p.sendMessage(ChatColor.WHITE + "Тип статистики " + ChatColor.YELLOW + Utils.upCurLetter(type.toString(), 1) + " (" + Utils.upCurLetter(type.getStatType().toString(), 1) + ")" + ChatColor.WHITE + " у игрока " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + " был установлен на " + ChatColor.YELLOW + Utils.upCurLetter(obj.toString(), 1));
            }
        }
    }

    public boolean isBoolean(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    private boolean isAllow(Object value, EStat stat, CommandSender cs) {
        try {
            if (stat.getStatType().equals(StatType.INTEGER)) {
                Integer.parseInt(value.toString());
            } else if (stat.getStatType().equals(StatType.DOUBLE)) {
                Double.parseDouble(value.toString());
            } else if (stat.getStatType().equals(StatType.BOOLEAN)) {
                if (isBoolean(value.toString().toLowerCase())) {
                    Boolean.parseBoolean(value.toString());
                } else {
                    Double.parseDouble(value.toString());
                }
            } else if (stat.getStatType().equals(StatType.STRING)) {
                String.valueOf(value.toString());
            }
        } catch (Exception ex) {
            cs.sendMessage(ChatColor.RED + "Тип значения введён не верно, нужен - " + Utils.upCurLetter(stat.getStatType().toString(), 1));
            return false;
        }
        return true;
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        ArrayList<String> st = new ArrayList<>();
        Arrays.stream(EStat.values()).forEach(stats -> st.add(stats.name().toLowerCase()));
        if (args.length != 3) {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player> " + st.toString() + " <value>");
            return;
        }
        boolean isOnline = true;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            isOnline = false;
        }
        EStat type;
        try {
            type = EStat.valueOf(args[1].toUpperCase());
        } catch (Exception ex) {
            cs.sendMessage(ChatColor.RED + "Typed " + Utils.upCurLetter(args[1], 1) + " isn`t real");
            cs.sendMessage(ChatColor.RED + "Available: " + st.toString());
            return;
        }
        if (isAllow(args[2], type, cs)) {
            Object obj = args[2];
            if (isOnline) {
                addStat(target.getName(), type, obj, true);
            } else {
                addStat(args[0], type, obj, false);
            }
            cs.sendMessage(ChatColor.WHITE + "Type of stat " + Utils.upCurLetter(type.toString(), 1) + " for player " + args[0] + " was set on " + obj.toString());
        }
    }

    private void addStat(String player, EStat type, Object obj, boolean online) {
        if (online) {
            Gamer gamer = GamerManager.getGamer(player);
            gamer.setStatistics(type, obj);
        } else {
            Main.getInstance().getPreparedRequests().voidRequest(DoVoid.UPDATE, player, type.getStatName(), obj);
        }
    }
}
