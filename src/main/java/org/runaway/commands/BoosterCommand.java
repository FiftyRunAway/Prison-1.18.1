package org.runaway.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import org.runaway.enums.BoosterType;
import org.runaway.enums.EConfig;
import org.runaway.enums.TypeMessage;
import org.runaway.inventories.BoosterMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class BoosterCommand extends CommandManager {

    public BoosterCommand() {
        super("booster", "prison.commands", Collections.singletonList("бустер"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (p.isOp()) {
            if (args.length == 0) {
                new BoosterMenu(p);
                return;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("debug")) {
                    if (Main.gBlocks.isActive()) {
                        p.sendMessage(ChatColor.AQUA + "Активен глобальный бустер блоков");
                    }
                    if (Main.gMoney.isActive()) {
                        p.sendMessage(ChatColor.GREEN + "Активен глобальный бустер денег");
                    }
                    Utils.getlBlocksMultiplier().keySet().forEach(s -> p.sendMessage(ChatColor.DARK_AQUA + "- Локальный бустер блоков игрока " + s));
                    Utils.getlMoneyMultiplier().keySet().forEach(s -> p.sendMessage(ChatColor.DARK_GREEN + "- Локальный бустер денег игрока " + s));
                    return;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("stop")) {
                    if (args[1].equalsIgnoreCase("blocks") && Main.gBlocks.isActive()) {
                        Main.gBlocks.setTime(4);
                        p.sendMessage(ChatColor.DARK_GREEN + "Бустер блоков остановится через 3 секунды!");
                    } else if (args[1].equalsIgnoreCase("money") && Main.gMoney.isActive()) {
                        Main.gMoney.setTime(4);
                        p.sendMessage(ChatColor.DARK_GREEN + "Бустер денег остановится через 3 секунды!");
                    }
                    return;
                }
            }
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " <тип, stop, debug> [время в секундах] [множитель]");
                return;
            }
            BoosterType type = BoosterType.valueOf(args[0].toUpperCase());
            /*try {
                type = BoosterType.valueOf(args[0].toUpperCase());
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Введёный вами вид бустера " + Utils.upCurLetter(args[0], 1) + " не существует");
                return;
            }*/
            long seconds;
            try {
                seconds = Long.parseLong(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Введёное время неправильное");
                return;
            }
            double multiplier;
            try {
                multiplier = Double.parseDouble(args[2]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Введёный множитель неправильный");
                return;
            }
            String[] splitter = String.valueOf(multiplier).split("\\.");
            int i = splitter[1].length();
            if (i > 1) {
                p.sendMessage(ChatColor.RED + "Может быть только один знак после точки множителя");
                return;
            }
            double maxmultiplier = 50.0;
            if (multiplier <= maxmultiplier) {
                if (type.equals(BoosterType.MONEY) && !Main.gMoney.isActive()) {
                    Main.gMoney.start(p.getName(), seconds, multiplier);
                } else if (type.equals(BoosterType.BLOCKS) && !Main.gBlocks.isActive()) {
                    Main.gBlocks.start(p.getName(), seconds, multiplier);
                }
            } else {
                p.sendMessage(ChatColor.RED + "Множитель должен быть меньше " + maxmultiplier + "x");
            }
        } else {
            if (args.length == 0) {
                new BoosterMenu(p);
            }
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " add [player] [type] [range] [time seconds] [x]");
            return;
        }
        if (args.length == 6 && args[0].equalsIgnoreCase("add")) {
            String format = args[2].toUpperCase() + "-" + args[3].toLowerCase() + "-" + args[5] + "-" + args[4];
            String name = args[1];
            if (Utils.getPlayers().contains(name)) {
                Gamer gamer = GamerManager.getGamer(name);
                gamer.getBoosters().add(format);
                Vars.sendSystemMessage(TypeMessage.SUCCESS, "Added to " + name + " " + format);
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("debug")) {
                if (Main.gBlocks.isActive()) {
                    cs.sendMessage(ChatColor.AQUA + "Активен глобальный бустер блоков");
                }
                if (Main.gMoney.isActive()) {
                    cs.sendMessage(ChatColor.GREEN + "Активен глобальный бустер денег");
                }
                Utils.getlBlocksMultiplier().keySet().forEach(s -> cs.sendMessage(ChatColor.DARK_AQUA + "- Локальный бустер блоков игрока " + s));
                Utils.getlMoneyMultiplier().keySet().forEach(s -> cs.sendMessage(ChatColor.DARK_GREEN + "- Локальный бустер денег игрока " + s));
            }
        }
    }
}
