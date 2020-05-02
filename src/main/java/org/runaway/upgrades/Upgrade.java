package org.runaway.upgrades;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.utils.Utils;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.UpgradeProperty;
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;

import java.util.HashMap;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Upgrade {

    public static void upgrade(Player player) {
        try {
            if (player.isOnline()) {
                Gamer gamer = Main.gamers.get(player.getUniqueId());
                String next = UpgradeMisc.getNext(UpgradeMisc.getSection(player));
                HashMap<UpgradeProperty, String> data = Upgrade.getData(gamer);
                HashMap<UpgradeProperty, String> itemdata = UpgradeMisc.getProperties(next);
                for (UpgradeProperty prop : itemdata.keySet()) {
                    if (Double.parseDouble(data.get(prop)) < Double.parseDouble(itemdata.get(prop))) {
                        player.sendMessage(Utils.colored(EMessage.NOTENOUGHPROPERTY.getMessage()).replaceAll("%property%", prop.getForMessage()));
                        return;
                    }
                }
                for (UpgradeProperty prop : itemdata.keySet()) {
                    if (prop.isTaken()) {
                        Upgrade.take(prop, Integer.parseInt(itemdata.get(prop)), player);
                    }
                }
                ItemStack save = UpgradeMisc.buildItem(next, false, player, false);
                player.getInventory().setItemInMainHand(save);
                gamer.sendMessage(EMessage.SUCCESSFULUPGRADE);
                Achievement.FIRST_UPGRADE.get(player, false);
                if ((int)gamer.getStatistics(EStat.UPGRADE_TRAINER) > 0) {
                    Utils.trainer.forEach(trainer -> {
                        Trainer tr = (Trainer) trainer;
                        if (tr.getType() != TypeTrainings.UPGRADE) return;
                        if (UpgradeMisc.getSection(player) == null) return;
                        if (Math.random() < tr.getValue(player)) {
                            System.out.println(save.getItemMeta());
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
                return String.valueOf(Math.round((Double) gamer.getStatistics(EStat.MONEY)));
            }
            case LEVEL: {
                return String.valueOf(gamer.getStatistics(EStat.LEVEL));
            }
            case RATS: {
                return String.valueOf(gamer.getStatistics(EStat.RATS));
            }
            case KILLS: {
                return String.valueOf(gamer.getStatistics(EStat.KILLS));
            }
            case WOOD: {
                return String.valueOf(gamer.getCurrentBlocks("WOOD"));
            }
            case BLOCKS: {
                return String.valueOf(Math.round((Double) gamer.getStatistics(EStat.BLOCKS)));
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
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        switch (prop) {
            case COST: {
                gamer.withdrawMoney(p);
                break;
            }
            case BLOCKS: {
                gamer.setStatistics(EStat.BLOCKS, (double)gamer.getStatistics(EStat.BLOCKS) - p);
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
