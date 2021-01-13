package org.runaway.upgrades;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.UpgradeProperty;
import org.runaway.events.custom.UpgradeEvent;
import org.runaway.inventories.Confirmation;
import org.runaway.managers.GamerManager;
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;
import org.runaway.utils.Utils;

import java.util.HashMap;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Upgrade {

    public static void upgrade(Player player, boolean fastGet) {
        try {
            if (player.isOnline()) {
                Gamer gamer = GamerManager.getGamer(player);
                String next = UpgradeMisc.getNext(UpgradeMisc.getSection(player));

                HashMap<UpgradeProperty, String> data = Upgrade.getData(gamer);
                HashMap<UpgradeProperty, String> itemdata = UpgradeMisc.getProperties(next);
                for (UpgradeProperty prop : itemdata.keySet()) {
                    if (Double.parseDouble(data.get(prop)) < Double.parseDouble(itemdata.get(prop))) {
                        gamer.sendMessage(Utils.colored(EMessage.NOTENOUGHPROPERTY.getMessage()).replaceAll("%property%", prop.getForMessage()));
                        player.closeInventory();
                        return;
                    }
                }
                ItemStack save = UpgradeMisc.buildItem(next, false, player, false);
                if (!fastGet && gamer.getLevelItem(save) > gamer.getIntStatistics(EStat.LEVEL)) {
                    new Confirmation(player, null, null, () ->
                            Upgrade.upgrade(player, true));
                    return;
                }
                for (UpgradeProperty prop : itemdata.keySet()) {
                    if (prop.isTaken()) {
                        Upgrade.take(prop, Integer.parseInt(itemdata.get(prop)), player);
                    }
                }
                player.getInventory().setItemInMainHand(save);
                gamer.sendMessage(EMessage.SUCCESSFULUPGRADE);
                Bukkit.getServer().getPluginManager().callEvent(new UpgradeEvent(player));
                player.closeInventory();
                Achievement.FIRST_UPGRADE.get(player, false);
                if (gamer.getIntStatistics(EStat.UPGRADE_TRAINER) > 0) {
                    Utils.trainer.forEach(trainer -> {
                        Trainer tr = (Trainer) trainer;
                        if (tr.getType() != TypeTrainings.UPGRADE) return;
                        if (UpgradeMisc.getSection(player) == null) return;
                        if (Math.random() < tr.getValue(player)) {
                            if (save.getItemMeta().equals(player.getInventory().getItemInMainHand().getItemMeta())) {
                                String next2 = UpgradeMisc.getNext(UpgradeMisc.getSection(player));
                                player.getInventory().setItemInMainHand(UpgradeMisc.buildItem(next2, false, player, false));
                                gamer.sendMessage(EMessage.TRAINERUPGRADE);
                            } else {
                                gamer.sendMessage(EMessage.TRAINERUPGRADEDUPE);
                            }
                        }
                    });
                }
            }
        } catch (Exception ex) {
            player.sendMessage(Utils.colored("&cОшибка при прокачке! Сообщите администрации об этом."));
            ex.printStackTrace();
        }
    }

    static HashMap<UpgradeProperty, String> getData(Gamer gamer) {
        HashMap<UpgradeProperty, String> map = new HashMap<>();
        for (UpgradeProperty up : UpgradeProperty.values()) {
            map.put(up, getProp(up, gamer));
        }
        return map;
    }

    public static String getProp(UpgradeProperty prop, Gamer gamer) {
        switch (prop) {
            case COST: {
                return String.valueOf(Math.round(gamer.getMoney()));
            }
            case LEVEL: {
                return String.valueOf(gamer.getStatistics(EStat.LEVEL));
            }
            case RATS: {
                return String.valueOf(gamer.getMobKills("rat"));
            }
            case KILLS: {
                return String.valueOf(gamer.getStatistics(EStat.KILLS));
            }
            case WOOD: {
                return String.valueOf(gamer.getCurrentBlocks("LOG_2"));
            }
            case BLOCKS: {
                return String.valueOf(Math.round(gamer.getDoubleStatistics(EStat.BLOCKS)));
            }
            case MINE_BLOCKS: {
                return String.valueOf(gamer.getCurrentBlocks("STONE") + gamer.getCurrentBlocks("COBBLESTONE"));
            }
            case SHOVEL_BLOCKS: {
                return String.valueOf(gamer.getCurrentBlocks("DIRT") + gamer.getCurrentBlocks("GRAVEL") + gamer.getCurrentBlocks("SAND"));
            }
            case STONE: {
                return String.valueOf(gamer.getCurrentBlocks("STONE"));
            }
            case NETHERRACK: {
                return String.valueOf(gamer.getCurrentBlocks("NETHERRACK"));
            }
            case IRON_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("IRON_ORE"));
            }
            case COAL_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("COAL_ORE"));
            }
            case GOLD_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("GOLD_ORE"));
            }
            case WEB: {
                return String.valueOf(gamer.getCurrentBlocks("WEB"));
            }
            case WOOL: {
                return String.valueOf(gamer.getCurrentBlocks("WOOL"));
            }
            case BOW_KILL: {
                return String.valueOf(gamer.getStatistics(EStat.BOW_KILL));
            }
            case DIRT: {
                return String.valueOf(gamer.getCurrentBlocks("DIRT"));
            }
            case SAND: {
                return String.valueOf(gamer.getCurrentBlocks("SAND"));
            }
            case GRAVEL: {
                return String.valueOf(gamer.getCurrentBlocks("GRAVEL"));
            }
            default: {
                return "null";
            }
        }
    }

    private static void take(UpgradeProperty prop, int p, Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        switch (prop) {
            case COST: {
                gamer.withdrawMoney(p);
                break;
            }
            case BLOCKS: {
                gamer.setStatistics(EStat.BLOCKS, gamer.getDoubleStatistics(EStat.BLOCKS) - p);
                break;
            }
        }
    }

    /*
    Зачарования:
    -
    -
    Уровень >> 5

     */

}
