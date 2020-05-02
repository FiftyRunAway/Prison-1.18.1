package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Gamer;
import org.runaway.utils.ExampleItems;
import org.runaway.Main;
import org.runaway.utils.Utils;
import org.runaway.enums.EMessage;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;

import java.util.*;

public class GiftCommand extends CommandManager {

    public GiftCommand() {
        super("gift", "prison.commands", Collections.singletonList("подарок"), false);
    }

    private static HashMap<Player, BukkitTask> list = new HashMap<>();

    private void accept(boolean is, Player consumer) {
        if (!Utils.getGifts().containsKey(consumer.getName())) {
            consumer.sendMessage(Utils.colored(EMessage.TIMELEFT.getMessage()));
            consumer.getPlayer().closeInventory();
            return;
        }
        Player owner = Bukkit.getPlayer(Utils.getGifters().get(consumer.getName()));
        if (owner == null) {
            consumer.sendMessage(Utils.colored(EMessage.NOPLAYER.getMessage()));
            return;
        }
        if (is) {
            consumer.getPlayer().getInventory().addItem(Utils.getGifts().get(consumer.getName()));
            consumer.sendMessage(EMessage.ACCEPTGIFT.getMessage().replaceAll("%player%", owner.getName()));
            owner.sendMessage(Utils.colored(EMessage.ACCEPTEDGIFT.getMessage()));
            consumer.getPlayer().closeInventory();
            Utils.getGifters().remove(consumer.getName());
        } else {
            owner.getInventory().addItem(Utils.getGifts().get(consumer.getName()));
            Utils.getGifters().remove(consumer.getName());
            Utils.getGifts().remove(consumer.getName());
            consumer.sendMessage(EMessage.CANCELGIFT.getMessage().replaceAll("%player%", owner.getName()));
            owner.sendMessage(Utils.colored(EMessage.CANCELEDGIFT.getMessage()));
            consumer.getPlayer().closeInventory();
        }
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = Main.gamers.get(p.getUniqueId());
        if (args.length == 0) {
            if (Utils.getGifts().containsKey(p.getName())) {
                StandardMenu menu = StandardMenu.create(1, "&cВы хотите получить этот подарок?");

                MenuButton accept = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(5, "&aПРИНЯТЬ"));
                accept.setClickEvent(event -> accept(true, p));
                for (int i = 0; i < 4; i++) {
                    menu.addButton(accept.setSlot(i).clone());
                }

                MenuButton cancel = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(14, "&cОТМЕНИТЬ"));
                cancel.setClickEvent(event -> accept(false, p));
                for (int i = 5; i < 9; i++) {
                    menu.addButton(cancel.setSlot(i).clone());
                }

                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(Utils.getGifts().get(p.getName())).setSlot(4));
                p.openInventory(menu.build());
                return;
            }
            p.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " [получатель]");
        } else if (args.length == 1) {
            if (p.getName().equals(args[0])) {
                p.sendMessage(Utils.colored(EMessage.SELFGIFT.getMessage()));
                return;
            }
            if (!Utils.getPlayers().contains(args[0])) {
                p.sendMessage(Utils.colored(EMessage.NOPLAYER.getMessage()));
                return;
            }
            if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getAmount() == 0 || p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getAmount() == 0 || !p.getInventory().getItemInMainHand().hasItemMeta()) {
                p.sendMessage(Utils.colored(EMessage.HANDSLEFT.getMessage()));
                return;
            }
            Player cons = Bukkit.getPlayer(String.valueOf(args[0]));
            if (Utils.getGifters().containsValue(p.getName())) {
                p.sendMessage(Utils.colored(EMessage.SENDERALREADYGIFT.getMessage()));
                return;
            }
            long timer = 20;
            if (Utils.getGifters().containsKey(cons.getName())) {
                p.sendMessage(EMessage.CONSUMERALREADYGIFT.getMessage().replaceAll("%time%", String.valueOf(timer)));
                return;
            }
            Utils.getGifters().put(cons.getName(), p.getName());
            Utils.getGifts().put(cons.getName(), p.getInventory().getItemInMainHand().clone());
            p.getInventory().getItemInMainHand().setAmount(0);
            gamer.sendMessage(EMessage.SENDGIFT);
            cons.sendMessage(EMessage.GIFTYOU.getMessage().replaceAll("%player%", p.getName()));
            cons.playSound(cons.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
            BukkitTask scheduler = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (Utils.getGifts().containsKey(args[0])) {
                    if (gamer.isInventory() && Utils.getPlayers().contains(gamer.getGamer())) {
                        p.getInventory().addItem(Utils.getGifts().get(cons.getName()));
                        gamer.sendMessage(EMessage.RETURNITEM);
                    }
                    Utils.getGifts().remove(cons.getName());
                    cons.sendMessage(EMessage.GIFTLEFTTIME.getMessage().replaceAll("%player%", p.getName()));
                    gamer.sendMessage(EMessage.TIMELEFTOWNER);
                }
            }, timer * 20);
        } else {
            p.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " [получатель]");
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
