package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.managers.GamerManager;
import org.runaway.utils.ExampleItems;
import org.runaway.Prison;
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

    private void accept(boolean is, Player consumer) {
        Gamer consumerGamer = GamerManager.getGamer(consumer);
        if (!Utils.getGifts().containsKey(consumer.getName())) {
            consumerGamer.sendMessage(Utils.colored(EMessage.TIMELEFT.getMessage()));
            consumer.getPlayer().closeInventory();
            return;
        }
        Player owner = Bukkit.getPlayer(Utils.getGifters().get(consumer.getName()));
        if (owner == null) {
            consumerGamer.sendMessage(Utils.colored(EMessage.NOPLAYER.getMessage()));
            return;
        }
        Gamer ownerGamer = GamerManager.getGamer(owner);
        if (is) {
            boolean taken = false;
            if (owner.getInventory().getContents().length > 0) {
                for (int i = 0; i < owner.getInventory().getContents().length; i++) {
                    if (owner.getInventory().getContents()[i].equals(Utils.getGifts().get(consumer.getName()))) {
                        owner.getInventory().getItem(i).setAmount(0);
                        taken = true;
                        break;
                    }
                }
            }
            if (!taken) {
                onCancel(owner, consumer, false);
                ownerGamer.sendMessage(EMessage.GIFTCHANGING.getMessage());
                return;
            }
            consumer.getPlayer().getInventory().addItem(Utils.getGifts().get(consumer.getName()));
            consumerGamer.sendMessage(EMessage.ACCEPTGIFT.getMessage().replace("%player%", owner.getName()));
            ownerGamer.sendMessage(EMessage.ACCEPTEDGIFT.getMessage());
            consumer.getPlayer().closeInventory();
            Utils.getGifters().remove(consumer.getName());
            Utils.getGifts().remove(consumer.getName());
        } else {
            onCancel(owner, consumer, true);
        }
    }

    private void onCancel(Player owner, Player consumer, boolean msgs) {
        Utils.getGifters().remove(consumer.getName());
        Utils.getGifts().remove(consumer.getName());
        if (msgs) {
            Gamer ownerGamer = GamerManager.getGamer(owner);
            Gamer consumerGamer = GamerManager.getGamer(consumer);
            consumerGamer.sendMessage(EMessage.CANCELGIFT.getMessage().replace("%player%", owner.getName()));
            ownerGamer.sendMessage(Utils.colored(EMessage.CANCELEDGIFT.getMessage()));
        }
        consumer.getPlayer().closeInventory();
    }


    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (args.length == 0) {
            if (Utils.getGifts().containsKey(p.getName())) {
                StandardMenu menu = StandardMenu.create(1, "&cВы хотите получить подарок?");

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
                menu.open(gamer);
                return;
            }
            gamer.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " [получатель]");
        } else if (args.length == 1) {
            if (p.getName().equals(args[0])) {
                gamer.sendMessage(Utils.colored(EMessage.SELFGIFT.getMessage()));
                return;
            }
            if (!Utils.getPlayers().contains(args[0])) {
                gamer.sendMessage(Utils.colored(EMessage.NOPLAYER.getMessage()));
                return;
            }
            if (p.getInventory().getItemInMainHand() == null ||
                    p.getInventory().getItemInMainHand().getAmount() == 0 ||
                    !p.getInventory().getItemInMainHand().hasItemMeta()) {
                gamer.sendMessage(Utils.colored(EMessage.HANDSLEFT.getMessage()));
                return;
            }
            Player cons = Bukkit.getPlayer(String.valueOf(args[0]));
            if (Utils.getGifters().containsValue(p.getName())) {
                gamer.sendMessage(Utils.colored(EMessage.SENDERALREADYGIFT.getMessage()));
                return;
            }
            long timer = 20;
            if (Utils.getGifters().containsKey(cons.getName())) {
                gamer.sendMessage(EMessage.CONSUMERALREADYGIFT.getMessage().replace("%time%", String.valueOf(timer)));
                return;
            }
            Gamer consGamer = GamerManager.getGamer(cons);
            Utils.getGifters().put(cons.getName(), p.getName());
            Utils.getGifts().put(cons.getName(), p.getInventory().getItemInMainHand().clone());
            gamer.sendMessage(EMessage.SENDGIFT);
            consGamer.sendMessage(EMessage.GIFTYOU.getMessage().replace("%player%", p.getName()));
            cons.playSound(cons.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
            Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                if (Utils.getGifts().containsKey(args[0])) {
                    Utils.getGifts().remove(cons.getName());
                    Utils.getGifters().remove(cons.getName());
                    consGamer.sendMessage(EMessage.GIFTLEFTTIME.getMessage().replace("%player%", p.getName()));
                    gamer.sendMessage(EMessage.TIMELEFTOWNER);
                }
            }, timer * 20);
        } else {
            gamer.sendMessage(ChatColor.RED + "Использование: /" + cmdName + " [получатель]");
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
