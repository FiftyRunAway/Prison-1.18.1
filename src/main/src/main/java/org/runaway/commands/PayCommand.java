package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.commands.completers.Tab;
import org.runaway.commands.completers.TabBuilder;
import org.runaway.commands.completers.TabCompletion;
import org.runaway.enums.EMessage;
import org.runaway.enums.MoneyType;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PayCommand extends CommandManager {

    public PayCommand() {
        super("pay", "prison.commands", Arrays.asList( "перевод", "sendmoney" ), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (args.length == 2) {
            Gamer gamer = GamerManager.getGamer(p);
            if (Bukkit.getPlayer(args[0]) == null) {
                gamer.sendMessage(EMessage.NOPLAYER);
                return;
            }
            Player pl = Bukkit.getPlayer(args[0]);
            Gamer get = GamerManager.getGamer(pl.getUniqueId());
            if (pl.getName().equals(p.getName())) {
                gamer.sendMessage(EMessage.VALUEBAD);
                return;
            }
            int send;
            try {
                send = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                gamer.sendMessage(EMessage.VALUEINT);
                return;
            }
            if (send < 1) {
                gamer.sendMessage(EMessage.VALUEBAD);
                return;
            }
            if (send > gamer.getMoney()) {
                gamer.sendMessage(EMessage.MONEYNEEDS);
                return;
            }
            gamer.withdrawMoney(send);
            gamer.sendMessage(EMessage.SENDMONEY);
            get.depositMoney(send);
            get.sendMessage(EMessage.GETMONEY.getMessage().replace("%money%", send + " " + MoneyType.RUBLES.getShortName())
                    .replace("%player%", gamer.getGamer()));
        } else {
            p.sendMessage(Utils.colored("&cИспользуйте: /" + cmdName + " <Игрок> <Сумма>"));
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }

    @Override
    public TabCompletion getTabCompletion() {
        return new PayTab();
    }

    public static class PayTab extends TabCompletion {

        public PayTab() {
            super("pay", new TabBuilder()
                    .addTab(new Tab().arg(1).addVariants(Utils.getPlayers()))
                    .getResult());
        }
    }
}
