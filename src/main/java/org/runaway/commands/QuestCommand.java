package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.enums.TypeMessage;
import org.runaway.inventories.MinesQuestMenu;
import org.runaway.quests.MinesQuest;
import org.runaway.utils.Vars;

import java.util.Collections;

public class QuestCommand extends CommandManager {

    public QuestCommand() {
        super("prisonquest", "prison.admin", Collections.singletonList("prisonquestsadmin"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {

    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length < 2) {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player> <quest_name>");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        MinesQuest quest = MinesQuest.getByName(args[1]);


        try {
            player.openInventory(MinesQuestMenu.getMenu(player, quest).build());
        } catch (Exception e) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Problem with loading quest menu for " + player.getName());
            e.printStackTrace();
        }
    }
}
