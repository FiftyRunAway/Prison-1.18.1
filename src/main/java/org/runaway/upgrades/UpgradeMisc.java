package org.runaway.upgrades;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.UpgradeProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeMisc {

    public static Material getType(String section) {
        return Material.valueOf(EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".type"));
    }

    public static HashMap<UpgradeProperty, String> getProperties(String section) {
        String prop = EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".properties");
        prop = prop.replace("{", "").replace("}", "");
        String[] mas = prop.split(";");
        HashMap<UpgradeProperty, String> map = new HashMap<>();
        for (String str : mas) {
            String[] tempmas = str.split(":");
            map.put(UpgradeProperty.valueOf(tempmas[0].toUpperCase()), tempmas[1]);
        }
        return map;
    }

    public static String getNext(String section) {
        String nxt = EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".nextitem");
        try {
            if (nxt.equalsIgnoreCase("null")) {
                return null;
            }
            return nxt;
        } catch (Exception e) { return null; }
    }


    public static HashMap<Enchantment, Integer> getEnchants(String ench) {
        HashMap<Enchantment, Integer> map = new HashMap<>();
        if (ench == null) {
            return map;
        }
        ench = ench.replace("{", "").replace("}", "");
        if (!ench.contains(";")) {
            String[] tempmas = ench.split(":");
            map.put(Enchantment.getByName(tempmas[0]), Integer.valueOf(tempmas[1]));
        } else {
            String[] split = ench.split(";");
            for (String str : split) {
                String[] tempmas = str.split(":");
                map.put(Enchantment.getByName(tempmas[0]), Integer.valueOf(tempmas[1]));
            }
        }
        return map;
    }

    private static HashMap<Enchantment, Integer> getEnchantments(String section) {
        HashMap<Enchantment, Integer> map = new HashMap<>();
        String ench = EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".enchants");
        if (ench.equalsIgnoreCase("null")) {
            return null;
        }
        ench = ench.replace("{", "").replace("}", "");
        if (!ench.contains(";")) {
            String[] tempmas = ench.split(":");
            map.put(Enchantment.getByName(tempmas[0]), Integer.valueOf(tempmas[1]));
        } else {
            String[] split = ench.split(";");
            for (String str : split) {
                String[] tempmas = str.split(":");
                map.put(Enchantment.getByName(tempmas[0]), Integer.valueOf(tempmas[1]));
            }
        }
        return map;
    }

    public static String getName(final String section) {
        return ChatColor.translateAlternateColorCodes('&', EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".name"));
    }

    public static ItemStack buildItem(String section, boolean toGUI, Player player, boolean menuItems) {
        ItemStack itemstack = new ItemStack(getType(section), 1);
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(getName(section));
        HashMap<Enchantment, Integer> enchantments = getEnchantments(section);
        if (enchantments != null) {
            for (Enchantment enchantment : enchantments.keySet()) {
                meta.addEnchant(enchantment, enchantments.get(enchantment), true);
            }
        }
        HashMap<UpgradeProperty, String> properties = getProperties(section);
        ArrayList<String> lores = new ArrayList<>();
        int level = Integer.parseInt(EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".lorelevel"));
        lores.add(Utils.colored("&7Минимальный уровень: &f" + EConfig.UPGRADE.getConfig().getInt("upgrades." + section + ".min_level")));
        lores.add("  ");
        lores.add(Utils.colored(" &fУровень предмета &7&l- &6" + level));
        if (toGUI) {
            lores.add("    ");
            lores.add(ChatColor.GRAY + "Требования:");
            if (!menuItems) {
                Gamer gamer = GamerManager.getGamer(player);
                Map<UpgradeProperty, String> data = Upgrade.getData(gamer);
                for (UpgradeProperty up : properties.keySet()) {
                    int dat = Integer.parseInt(data.get(up));
                    int prop = Integer.parseInt(properties.get(up));
                    lores.add(Utils.colored("  &f• " + up.getName() + " &7&l>> " + ((dat < prop) ? "&c" : "&a") + dat + "&f/&6" + prop));
                }
            } else {
                for (UpgradeProperty up : properties.keySet()) {
                    int prop = Integer.parseInt(properties.get(up));
                    lores.add(Utils.colored("  &f• " + up.getName() + " &7&l>> " + "&6" + prop));
                }
            }
        }
        meta.setUnbreakable(true);
        meta.setLore(lores);
        itemstack.setItemMeta(meta);
        return itemstack;
    }

    public static String getSection(Player player) {
        if(player.getInventory().getItemInMainHand() == null) return null;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!item.hasItemMeta()) return null;
        if(!item.getItemMeta().hasDisplayName()) return null;
        String type = item.getType().name();
        String name = item.getItemMeta().getDisplayName().replace(" ", "_");
        String enchants = item.getEnchantments().isEmpty() ? "null" : getench(item);
        for (String section : EConfig.UPGRADE.getConfig().getConfigurationSection("upgrades").getKeys(false)) {
            String type2 = getType(section).name();
            String name2 = ChatColor.translateAlternateColorCodes('&', EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".name")).replace(" ", "_");
            String enchants2 = EConfig.UPGRADE.getConfig().getString("upgrades." + section + ".enchants");
            name = ChatColor.stripColor(name).toLowerCase();
            name2 = ChatColor.stripColor(name2).toLowerCase();
            if (type.equalsIgnoreCase(type2) && name.equalsIgnoreCase(name2)) {
                if (enchants2 == null) {
                    if (!item.getEnchantments().isEmpty()) continue;
                }
                else if (!enchants.equalsIgnoreCase(enchants2)) continue;
                return section;
            }
        }
        return null;
    }

    private static String getench(ItemStack item) {
        if (item.getEnchantments().size() == 1) {
            StringBuilder a = new StringBuilder("{");
            for (Enchantment ench : item.getEnchantments().keySet()) {
                a.append(ench.getName()).append(":").append(item.getEnchantments().get(ench));
            }
            a.append("}");
            return a.toString();
        }
        StringBuilder a = new StringBuilder("{");
        for (Enchantment ench : item.getEnchantments().keySet()) {
            a.append(ench.getName()).append(":").append(item.getEnchantments().get(ench)).append(";");
        }
        StringBuilder b = new StringBuilder();
        for (int d = 0; d < a.toString().toCharArray().length - 1; ++d) {
            b.append(a.charAt(d));
        }
        b.append("}");
        return b.toString();
    }
}
