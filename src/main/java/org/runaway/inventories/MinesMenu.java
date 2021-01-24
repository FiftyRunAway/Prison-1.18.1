package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.items.ItemManager;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mines;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class MinesMenu implements IMenus {

    public MinesMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);
        Mines.icons.forEach((mines, mineIcon) -> {
            IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(mines.getPrisonIcon(gamer))
                    .setSlot(mines.getMinLevel() - 1);
            bt.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if (g.getIntStatistics(EStat.LEVEL) >= mines.getMinLevel()) {
                    if (mines.needPerm()) {
                        if (g.getLocations().contains(mines.getLocName()) || p.isOp()) {
                            g.teleport(mines.getSpawn());
                        } else g.sendMessage(EMessage.MINENEEDPERM);
                    } else g.teleport(mines.getSpawn());
                    p.closeInventory();
                } else {
                    gamer.sendMessage(Utils.colored(EMessage.MINELEVEL.getMessage().replaceAll("%level%", mines.getMinLevel() + "")));
                }
            });
            menu.addButton(bt);
        });
        for (EJobs job : EJobs.values()) {
            Job j = job.getJob();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(j.getButton(gamer).item()).setSlot(j.getLevel() - 1);
            btn.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if (g.getIntStatistics(EStat.LEVEL) >= j.getLevel()) {
                    g.teleport(j.getLocation(j));
                    p.closeInventory();
                } else {
                    gamer.sendMessage(Utils.colored(EMessage.MINELEVEL.getMessage().replaceAll("%level%", j.getLevel() + "")));
                }
            });
            menu.addButton(btn);
        }/*
        ConfigurationSection levels = EConfig.CONFIG.getConfig().getConfigurationSection("levels");
        int maxLevel = levels.getKeys(false).size();
        for (int s = 0; s < maxLevel; s++) {
            if (menu.getInventory().getItem(s) == null) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STAINED_GLASS_PANE)
                        .data((short) 12)
                        .name("&cСкоро").lore(new Lore.BuilderLore()
                                .addString("&7Совсем скоро эта шахта")
                                .addString("&7&nпоявится&r&7 на сервере!").build()).build().item()).setSlot(s));
            }
        }*/

        IMenuButton base = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.NETHER_STAR).name("&6База")
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Требования к доступу:")
                        .addString("&7• " + (gamer.getIntStatistics(EStat.LEVEL) < 5 ? ChatColor.RED : ChatColor.GREEN)  + "Минимальный уровень • 5")
                        .addString("&7• " + (gamer.getFaction().equals(FactionType.DEFAULT) ? ChatColor.RED : ChatColor.GREEN)  + "Вступление во фракцию")
                        .build())
                .build().item()).setSlot(4);
        base.setClickEvent(event -> GamerManager.getGamer(event.getWhoClicked()).teleportBase());
        menu.addButton(base);

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(ItemManager.getPrisonItem("back").getItemStack()).setSlot(44);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Список шахт";
    }
}
