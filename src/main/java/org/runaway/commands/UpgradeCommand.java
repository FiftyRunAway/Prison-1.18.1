package org.runaway.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.inventories.UpgradeMenu;
import org.runaway.upgrades.UpgradeMisc;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeCommand extends CommandManager {

    private ArrayList<String> confirm = new ArrayList<>();

    public UpgradeCommand() {
        super("upgrade", "prison.commands", Arrays.asList("upg", "апгрейд"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (args.length == 0) {
            if (!confirm.contains(p.getName())) {
                confirm.add(p.getName());
                Main.gamers.get(p.getUniqueId()).sendMessage(EMessage.UPGRADEATTENTION);
                return;
            }
            confirm.remove(p.getName());
            new UpgradeMenu(p);
        } else if (args.length == 1 && p.isOp()) {
            String item = String.valueOf(args[0]);
            if (EConfig.UPGRADE.getConfig().contains("upgrades." + item)) {
                p.getInventory().addItem(UpgradeMisc.buildItem(item, false, p, false));
                p.sendMessage(ChatColor.GREEN + "Вы получили " + ChatColor.YELLOW + item + ChatColor.GREEN + "!");
            } else p.sendMessage(ChatColor.RED + "Такого предмета нет в конфиге Upgrade.yml");
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
