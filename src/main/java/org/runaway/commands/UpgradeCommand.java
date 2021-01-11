package org.runaway.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.UpgradeMenu;
import org.runaway.managers.GamerManager;
import org.runaway.upgrades.UpgradeMisc;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeCommand extends CommandManager {

    private ArrayList<String> confirm = new ArrayList<>();

    public UpgradeCommand() {
        super("upgrade", "prison.commands", Arrays.asList("upg", "апгрейд", "up"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (args.length == 0) {
            String upgradeSection = UpgradeMisc.getSection(p);
            if(upgradeSection == null) {
                gamer.sendMessage("&cВы не можете улучшить данный предмет!");
                return;
            }
            if (gamer.getLevelItem(UpgradeMisc.buildItem(UpgradeMisc.getNext(upgradeSection), false, p, false)) > gamer.getIntStatistics(EStat.LEVEL)) {
                if (!confirm.contains(p.getName())) {
                    confirm.add(p.getName());
                    gamer.sendMessage(EMessage.UPGRADEATTENTION);

                    // Remove from confirm data
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            confirm.remove(p.getName());
                        }
                    }.runTaskLater(Main.getInstance(), 100L);

                    return;
                }
                confirm.remove(p.getName());
            }
            new UpgradeMenu(p);
        } else if (args.length == 1 && p.isOp()) {
            String item = String.valueOf(args[0]);
            if (EConfig.UPGRADE.getConfig().contains("upgrades." + item)) {
                p.getInventory().addItem(UpgradeMisc.buildItem(item, false, p, false));
                gamer.sendMessage(ChatColor.GREEN + "Вы получили " + ChatColor.YELLOW + item + ChatColor.GREEN + "!");
            } else gamer.sendMessage(ChatColor.RED + "Такого предмета нет в конфиге Upgrade.yml");
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
