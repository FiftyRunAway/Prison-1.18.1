package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.donate.Donate;
import org.runaway.enums.EMessage;
import org.runaway.enums.TypeMessage;
import org.runaway.inventories.DonateMenu;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.Collections;

public class DonateCommand extends CommandManager {

    public DonateCommand() {
        super("donate", "prison.commands", Collections.singletonList("донат"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new DonateMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            String name = String.valueOf(args[1]);
            int add = 0;
            try {
                add = Integer.parseInt(String.valueOf(args[2]));
            } catch (IllegalArgumentException ex) {
                Vars.sendSystemMessage(TypeMessage.ERROR, "Use only integer");
            }
            if (!Bukkit.getOfflinePlayer(name).isOp()) {
                Donate.depositTotalDonateMoney(name, add);
                Donate.depositDonateMoney(name, add, true);
                if (Utils.getPlayers().contains(name)) {
                    Player player = Bukkit.getPlayer(name);
                    player.sendMessage(Utils.colored(EMessage.DEPOSITDONATE.getMessage()
                            .replace("%money%", add + " ₽")));
                    player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1, 1);
                }
            } else {
                Donate.depositDonateMoney(name, add, true);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Use: /donate add <Player> <Money>");
        }
    }
}
