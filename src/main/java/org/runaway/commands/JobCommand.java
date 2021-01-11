package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.FishSellMenu;
import org.runaway.inventories.JobUpgradeMenu;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Collections;

public class JobCommand extends CommandManager {

    public JobCommand() {
        super("job", "prison.admin", Collections.singletonList("prisonfisher"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length != 2) {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player> <job>");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        Gamer gamer = GamerManager.getGamer(player);
        Job job = EJobs.valueOf(args[1].toUpperCase()).getJob();
        if (gamer.getIntStatistics(EStat.LEVEL) < job.getLevel()) {
            gamer.sendMessage(Utils.colored(EMessage.JOBLEVEL.getMessage().replace("%level%", job.getLevel() + "")));
            return;
        }
        player.openInventory(JobUpgradeMenu.getMenu(
                gamer, job).build());
    }
}
