package org.runaway.donate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.enums.*;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Donate {

    private String name;
    private Material icon;
    private int amount;
    private int price;
    private boolean temporary;
    private Lore lore;
    private int slot;
    private int sale;

    public static HashMap<Donate, DonateIcon> icons = new HashMap<>();

    public Donate(String name, Material icon, int amount, int price, boolean temporary, Lore lore, int slot, int sale) {
        this.name = name;
        this.icon = icon;
        this.amount = amount;
        this.price = price;
        this.temporary = temporary;
        this.lore = lore;
        this.slot = slot;
        this.sale = sale;
    }


    public ItemStack getIcon() {
        return new Item.Builder(icon).amount(amount).name(name + (temporary ? " &c&nВременный предмет!" : "")).lore(new Lore.BuilderLore()
                .addString("&7Описание:")
                .addLore(lore)
                .addSpace()
                .addString("&fЦена: " + (sale > 0 ? ("&b&m" + price + " &c&n" + sale) : ("&l&b&n" + price)) + " " + MoneyType.REAL_RUBLES.getShortName())
        .build()).build().item();
    }

    public int getSlot() { return slot; }

    public int getPrice() {
        return price;
    }

    public int getFinalPrice() {
        return this.sale > 0 ? sale : price;
    }

    public static void saveDonateLog(TypeMessage type, String message) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String time = new SimpleDateFormat("HH:mm zz").format(new Date());
        ArrayList<String> list = new ArrayList<>();
        String result = type.toString() + " > ["  + time + "] " + message;
        list.add(result);
        ConfigurationSection sec = EConfig.DONATE.getConfig().getConfigurationSection("donate-log");
        if (sec != null && sec.contains(date)) {
            list.addAll(sec.getStringList(date));
            sec.set(date, list);
        } else {
            EConfig.DONATE.getConfig().set("donate-log." + date, list);
        }
        EConfig.DONATE.saveConfig();
        Bukkit.getConsoleSender().sendMessage(type.getColor() + result);
    }

    public static boolean depositDonateMoney(String name, int money, boolean save) {
        try {
            ConfigurationSection sec = EConfig.DONATE.getConfig().getConfigurationSection("statistics");
            int set = money;
            if (sec != null && sec.contains(name)) {
                set += Integer.parseInt(sec.get(name).toString());
                sec.set(name, set);
            } else {
                EConfig.DONATE.getConfig().set("statistics." + name, set);
            }
            if (save) saveDonateLog(TypeMessage.SUCCESS, "Deposit money for " + name + " money: " + money + ". Total: " + set);
            return true;
        } catch (Exception ex) {
            saveDonateLog(TypeMessage.ERROR, "Deposit money for " + name + " money: " + money);
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean withdrawDonateMoney(String name, int money) {
        try {
            ConfigurationSection sec = EConfig.DONATE.getConfig().getConfigurationSection("statistics");
            if (sec == null || !sec.contains(name) || Integer.parseInt(sec.get(name).toString()) < money) return false;
            int set = Integer.parseInt(sec.get(name).toString()) - money;
            sec.set(name, set);
            saveDonateLog(TypeMessage.SUCCESS, "Withdraw money for " + name + " money: " + money + ". Total: " + set);
            return true;
        } catch (Exception ex) {
            saveDonateLog(TypeMessage.ERROR, "Withdraw money for " + name + " money: " + money);
            ex.printStackTrace();
            return false;
        }
    }

    public static int getDonateMoney(String name) {
        ConfigurationSection sec = EConfig.DONATE.getConfig().getConfigurationSection("statistics");
        if (sec == null || !sec.contains(name)) {
            return 0;
        }
        return Integer.parseInt(sec.get(name).toString());
    }

    public static boolean depositTotalDonateMoney(String name, int money) {
        try {
            ConfigurationSection sec = EConfig.DONATE.getConfig().getConfigurationSection("total");
            int set = money;
            if (sec != null && sec.contains(name)) {
                set += Integer.parseInt(sec.get(name).toString());
                sec.set(name, set);
            } else {
                EConfig.DONATE.getConfig().set("total." + name, set);
            }
            EConfig.DONATE.saveConfig();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static double getTotalDonateMoney(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if(player != null) {
            return GamerManager.getGamer(player).getDoubleStatistics(EStat.STREAMS);
        } else {
            return (double) EStat.STREAMS.getFromConfig(name);
        }
    }

    public static void getDonate(Material material, Gamer gamer) {
        try {
            switch (material) {
                case DIAMOND_BLOCK: {
                    gamer.addBooster(BoosterType.BLOCKS, 2.0, 1800, true);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case DIAMOND_ORE: {
                    gamer.addBooster(BoosterType.BLOCKS, 1.5, 1800, true);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case DIAMOND: {
                    gamer.addBooster(BoosterType.BLOCKS, 2.0, 3600, false);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case GOLD_BLOCK: {
                    gamer.addBooster(BoosterType.MONEY, 2.0, 1800, true);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case GOLD_ORE: {
                    gamer.addBooster(BoosterType.MONEY, 1.5, 1800, true);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case GOLD_INGOT: {
                    gamer.addBooster(BoosterType.MONEY, 2.0, 3600, false);
                    Utils.sendClickableMessage(gamer, "&aДля активации ускорителя перейдите в меню &e/booster", "booster");
                    break;
                }
                case PAPER: {
                    PermissionsEx.getUser(gamer.getGamer()).addPermission(getPex(material));
                    gamer.sendMessage("&aДля правильной работы купленной услуги, &eперезайдите на сервер!");
                    break;
                }
                case BOOK_AND_QUILL: {
                    PermissionsEx.getUser(gamer.getGamer()).addPermission(getPex(material));
                    gamer.sendMessage("&aДля активации купленной услуги введите &e/autosell");
                    break;
                }
                case TNT: {
                    gamer.sendMessage("&aДля правильной работы &6боевого пропуска&a, &eперезайдите на сервер!");
                    gamer.getNewBattlePass();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getPex(Material material) {
        switch (material) {
            case PAPER: {
                return "prison.toilet";
            }
            case BOOK_AND_QUILL: {
                return "prison.autosell";
            }
            default: {
                return null;
            }
        }
    }

}
