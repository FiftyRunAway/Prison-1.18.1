package org.runaway.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.UpgradeMenu;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
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
            ItemStack itemStack = p.getInventory().getItemInMainHand();
            PrisonItem prisonItem = ItemManager.getPrisonItem(itemStack);
            if(prisonItem == null) {
                gamer.sendMessage("&cПредмета не существует.");
                return;
            }
            if(!gamer.isOwner()) {
                gamer.sendMessage("&4Не ваш предмет!");
                return;
            }
            if(prisonItem.getNextPrisonItem() == null || prisonItem.getUpgradeRequireList() == null) {
                gamer.sendMessage("&cДанный предмет не имеет улучшений!");
                return;
            }
            if (gamer.getLevelItem(itemStack) > gamer.getIntStatistics(EStat.LEVEL)) {
                if (!confirm.contains(p.getName())) {
                    confirm.add(p.getName());
                    gamer.sendMessage(EMessage.UPGRADEATTENTION);

                    // Remove from confirm data
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            confirm.remove(p.getName());
                        }
                    }.runTaskLater(Prison.getInstance(), 100L);
                    return;
                }
                confirm.remove(p.getName());
            }
            new UpgradeMenu(p, prisonItem.getNextPrisonItem(), ItemManager.getPrisonItem(prisonItem.getNextPrisonItem()).getUpgradeRequireList());
        } else if (args.length == 1 && p.isOp()) {
            String item = String.valueOf(args[0]);
            if (EConfig.UPGRADE.getConfig().contains("upgrades." + item)) {
                gamer.addItem(item);
                gamer.sendMessage(ChatColor.GREEN + "Вы получили " + ChatColor.YELLOW + item + ChatColor.GREEN + "!");
            } else gamer.sendMessage(ChatColor.RED + "Такого предмета нет в конфиге Upgrade.yml");
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
