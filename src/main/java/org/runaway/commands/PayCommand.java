package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.MoneyType;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Arrays;

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
            Gamer get = Main.gamers.get(pl.getUniqueId());
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
            if (send <= 0) {
                gamer.sendMessage(EMessage.VALUEBAD);
                return;
            }
            if (send > (double)gamer.getStatistics(EStat.MONEY)) {
                gamer.sendMessage(EMessage.MONEYNEEDS);
                return;
            }
            gamer.withdrawMoney(send);
            gamer.sendMessage(EMessage.SENDMONEY);
            get.depositMoney(send);
            pl.sendMessage(EMessage.GETMONEY.getMessage().replaceAll("%money%", send + " " + MoneyType.RUBLES.getShortName()).replaceAll("%player%", gamer.getGamer()));
        } else {
            p.sendMessage(Utils.colored("&cИспользуйте: /" + cmdName + " <Игрок> <Сумма>"));
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
