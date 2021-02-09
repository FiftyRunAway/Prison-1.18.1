package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.auctions.TrashAuction;
import org.runaway.enums.EMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

public class ItemCommand extends CommandManager {

    public ItemCommand() {
        super("item", "prison.admin", Collections.singletonList("getitem"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if(!p.hasPermission("prison.admin")) {
            gamer.sendMessage(EMessage.NOPERM);
            return;
        }
        switch (args.length) {
            case 0:
                gamer.sendMessage("Использование: /item <give/info/list/copy>");
                break;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "give": case "list":
                        ItemManager.getPrisonItemMap().forEach((techName, prisonItem) -> {
                            gamer.sendMessage("Тех. название: " + techName + ";Предмет: " + prisonItem.getItemStack());
                        });
                        break;
                    case "auction":
                        TrashAuction.startAuction();
                        break;
                    case "info":
                        gamer.sendMessage("&aИнформация о предмете:");
                        new ParameterMeta(p.getInventory().getItemInMainHand()).getParametersMap().forEach(((parameter, o) -> {
                            gamer.sendMessage("&d" + parameter.getDefaultNbtFormatter().getString() + " " + o.toString());
                        }));
                        break;
                    case "copy":
                        ParameterMeta parameterMeta = new ParameterMeta(p.getInventory().getItemInOffHand());
                        p.getInventory().setItemInMainHand(parameterMeta.applyTo(p.getInventory().getItemInMainHand()));
                        break;
                    case "name":
                        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand());
                        String result = nmsStack.a() + ".name";
                        p.sendMessage(Prison.getInstance().getKeys().getProperty(result));
                        //TODO кликабельные голограммы, ап предметов, магазин, квесты, мобы
                        break;
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "give":
                        String techName = args[1];
                        PrisonItem prisonItem = ItemManager.getPrisonItem(techName);
                        if(prisonItem == null) {
                            gamer.sendMessage("&4Предмет не найден.");
                            return;
                        }
                        p.getInventory().addItem(ItemManager.initItem(prisonItem.getItemStack(), gamer));
                        break;
                }
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}