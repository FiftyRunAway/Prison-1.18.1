package org.runaway.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

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
        Gamer gamer = GamerManager.getGamer(p);
        int is = 0;
        List<String> owners = new ArrayList<>();
        if (Prison.gBlocks.isActive() && !Prison.THXersBlocks.contains(p.getName())) {
            is++;
            Prison.THXersBlocks.add(p.getName());
            owners.add(Prison.gBlocks.getOwner());
        }
        if (Prison.gMoney.isActive() && !Prison.THXersMoney.contains(p.getName())) {
            is++;
            Prison.THXersMoney.add(p.getName());
            owners.add(Prison.gMoney.getOwner());
        }
        if (is > 0) {
            int money = 5;
            for (String owns : owners) {
                if (Utils.getPlayers().contains(owns)) {
                    Prison.gamers.get(Bukkit.getPlayer(owns).getUniqueId()).depositMoney(gamer.getIntStatistics(EStat.LEVEL) * money);
                } else {
                    EStat.MONEY.setInConfig(owns, (double)EStat.MONEY.getFromConfig(owns) + (money * (int)EStat.LEVEL.getFromConfig(owns)));
                }
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
