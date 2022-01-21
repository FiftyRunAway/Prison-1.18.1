package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.commands.completers.Tab;
import org.runaway.commands.completers.TabBuilder;
import org.runaway.commands.completers.TabCompletion;
import org.runaway.donate.features.FractionDiscount;
import org.runaway.enums.*;
import org.runaway.inventories.FractionMenu;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by _RunAway_ on 4.2.2019
 */

public class FractionCommand extends CommandManager {

    public FractionCommand() {
        super("faction", "prison.commands", Arrays.asList("fraction", "фракция"), false);
    }

    private ArrayList<String> toLeave = new ArrayList<>();

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        String name = p.getName();
        if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            if (gamer.getFaction().equals(FactionType.DEFAULT)) {
                gamer.sendMessage(EMessage.NOFACTION);
                new FractionMenu(p);
                return;
            }
            if (toLeave.contains(name)) {
                toLeave.remove(name);
                gamer.leaveFraction();
            } else {
                toLeave.add(name);
                int mon = EConfig.CONFIG.getConfig().getInt("costs.FractionLeave") * gamer.getIntStatistics(EStat.LEVEL);
                int discount = 0;
                Object obj = gamer.getPrivilege().getValue(new FractionDiscount());
                if (obj != null) {
                    discount += Integer.parseInt(obj.toString());
                    gamer.getPlayer().sendMessage(Utils.colored(EMessage.FLEAVECONFIRM.getMessage().
                            replace("%money%", (1 - discount / 100) * mon + " " + MoneyType.RUBLES.getShortName()).
                            replace("%discount%", discount == 0 ? "" : "&7(&bСкидка -" + discount + "%&7)")));
                } else {
                    gamer.getPlayer().sendMessage(Utils.colored(EMessage.FLEAVECONFIRM.getMessage()
                            .replace("%money%", mon + " " + MoneyType.RUBLES.getShortName())
                            .replace("%discount%", "")));
                }
                Bukkit.getServer().getScheduler().runTaskLater(Prison.getInstance(), () -> {
                    if (toLeave.contains(name)) {
                        toLeave.remove(name);
                        gamer.sendMessage(EMessage.ERRORCONFIRMATION);
                    }
                }, 300L);
            }
        } else {
            gamer.chooseFactionMenu();
        }
    }

    @Override
    public TabCompletion getTabCompletion() {
        return new FactionTab();
    }

    public static class FactionTab extends TabCompletion {

        public FactionTab() {
            super("faction", new TabBuilder()
                    .addTab(new Tab().arg(1).addVariant("leave"))
                    .getResult());
        }
    }


    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
