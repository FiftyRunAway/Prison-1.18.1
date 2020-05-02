package org.runaway.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by _RunAway_ on 13.5.2019
 */

public class TipCommand extends CommandManager {

    public TipCommand() {
        super("tip", "prison.commands", Collections.singletonList("спасибо"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = Main.gamers.get(p.getUniqueId());
        int is = 0;
        List<Gamer> owners = new ArrayList<>();
        if (Main.gBlocks.isActive() && !Main.THXersBlocks.contains(p.getName())) {
            is++;
            Main.THXersBlocks.add(p.getName());
            owners.add(Main.gamers.get(Bukkit.getPlayer(Main.gBlocks.getOwner()).getUniqueId()));
        }
        if (Main.gMoney.isActive() && !Main.THXersMoney.contains(p.getName())) {
            is++;
            Main.THXersMoney.add(p.getName());
            owners.add(Main.gamers.get(Bukkit.getPlayer(Main.gMoney.getOwner()).getUniqueId()));
        }
        if (is > 0) {
            int money = 5;
            for (Gamer owns : owners) {
                owns.depositMoney((int)gamer.getStatistics(EStat.LEVEL) * money);
            }
            gamer.depositMoney(money * is);
            gamer.sendActionbar(ChatColor.GREEN + "+" + (money * is) + " " + MoneyType.RUBLES.getShortName());
            gamer.sendMessage(EMessage.TIP);
        } else {
            gamer.sendMessage(EMessage.NOACTIVEBOOSTERS);
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
