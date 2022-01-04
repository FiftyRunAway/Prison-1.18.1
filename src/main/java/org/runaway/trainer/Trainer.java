package org.runaway.trainer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.achievements.Achievement;
import org.runaway.enums.*;
import org.runaway.events.custom.TrainerUpEvent;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.upgrades.Upgrade;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Created by _RunAway_ on 17.5.2019
 */

public class Trainer {

    private TypeTrainings type;
    private String name;
    private Material icon;
    private Lore lore;
    private int levels;
    private ArrayList<Double> values;

    public Trainer(String type, String name, Material icon, Lore lore, List<String> values) {
        this.type = TypeTrainings.valueOf(type);
        this.name = Utils.colored(name);
        this.icon = icon;
        this.lore = lore;
        this.levels = EConfig.TRAINER.getConfig().getStringList(type.toLowerCase() + ".levels").size();
        this.values = new ArrayList<>();
        values.forEach(s -> this.values.add(Double.parseDouble(s)));
    }

    //Config settings {price value requirements}
    public IMenuButton getMenuIcon(Player player) {
        ItemStack stack = new ItemStack(this.icon);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(this.name);
        Lore.BuilderLore lore = new Lore.BuilderLore().addLore(this.lore).addSpace().addString("&dУровни прокачки:");
        Gamer gamer = GamerManager.getGamer(player);
        int plevel = gamer.getTrainingLevel(type.name());
        ConfigurationSection section = EConfig.TRAINER.getConfig().getConfigurationSection(type.name().toLowerCase());
        AtomicBoolean canget = new AtomicBoolean(true);
        HashMap<UpgradeProperty, Integer> map = new HashMap<>();
        for (int i = 0; i < this.levels; i++) {
            String lvl = section.getStringList("levels").get(i);
            String[] tempmas = lvl.split(" ");
            lore.addString(getColorLevel(plevel, (i + 1)) +
                    "- " + tempmas[1].replaceAll("_", " ") + ".");
            if (getColorLevel(plevel, i + 1) == ChatColor.GREEN) {
                lore.addString(" &7Требования:");
                if (Integer.parseInt(tempmas[0]) < gamer.getMoney()) {
                    lore.addString("&a  • Цена: " + tempmas[0] + " " + MoneyType.RUBLES.getShortName());
                } else {
                    lore.addString("&c  • Цена: " + tempmas[0] + " " + MoneyType.RUBLES.getShortName());
                    canget.set(false);
                }
                Arrays.stream(tempmas[2].split("&")).forEach(str -> {
                    UpgradeProperty pr = UpgradeProperty.valueOf(str.split(":")[0].toUpperCase());
                    int need = Integer.parseInt(str.split(":")[1]);
                    int has = Integer.parseInt(Upgrade.getProp(pr, gamer));
                    String format = (has >= need ? ChatColor.GREEN : ChatColor.RED) + "  • " + pr.getName() + ": ";
                    if (pr.name().equals("LEVEL")) {
                        format = format.concat((has < 30 ? has : ("&e" + gamer.getDisplayRebirth() + "&a " + has % 30)) + "/" + (need < 30 ? need : ("&e" + gamer.getDisplayRebirth() + "&a " + need % 30)));
                    } else format = format.concat(has + "/" + need);
                    lore.addString(format);
                    if (has < need) canget.set(false);
                    map.put(pr, need);
                });
            }
        }
        meta.setLore(lore.build().getList());
        stack.setItemMeta(meta);
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(stack);
        boolean finalCanget = canget.get();
        btn.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(p);
            int l = gamer.getTrainingLevel(type.name());
            if (l >= section.getStringList("levels").size()) {
                g.sendMessage(EMessage.TRAINERFULL);
                p.closeInventory();
                return;
            }
            if (finalCanget) {
                String lvl = section.getStringList("levels").get(l);
                String[] tempmas = lvl.split(" ");
                g.withdrawMoney(Integer.parseInt(tempmas[0]), true);
                g.sendMessage(EMessage.TRAINERSUCCESS);
                g.getTrainings().put(type.name(), g.getTrainingLevel(type.name()) + 1);
                Achievement.FIRST_TRAINER.get(p);
                p.closeInventory();

                Bukkit.getServer().getPluginManager().callEvent(new TrainerUpEvent(p, type));
            } else {
                g.sendMessage(EMessage.TRAINERNEED);
                p.closeInventory();
            }
        });
        return btn;
    }

    public double getValue(Player player) {
        return this.values.get(GamerManager.getGamer(player).getTrainingLevel(type.name()));
    }

    private ChatColor getColorLevel(int plevel, int level) {
        if (plevel >= level) {
            return ChatColor.YELLOW;
        } else if (level == plevel + 1) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.RED;
        }
    }

    public TypeTrainings getType() {
        return type;
    }
}
