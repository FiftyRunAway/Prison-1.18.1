package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.managers.GamerManager;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mines;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class MinesMenu implements IMenus {

    public MinesMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);

        IMenuButton minesButton = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DARK_OAK_LOG)
                        .name("&eШахты")
                        .lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7>> Открыть").build()).build().item())
                .setSlot(10);
        minesButton.setClickEvent(event -> openMines(gamer));
        menu.addButton(minesButton);

        IMenuButton jobsButton = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLDEN_HOE)
                        .name("&eРаботы")
                        .lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7>> Открыть").build()).build().item())
                .setSlot(12);
        jobsButton.setClickEvent(event -> openJobs(gamer));
        menu.addButton(jobsButton);

        IMenuButton locationsButton = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.MUSIC_DISC_13)
                        .name("&eЛокации по пропуску")
                        .lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7>> Открыть").build()).build().item())
                .setSlot(14);
        locationsButton.setClickEvent(event -> openLocations(gamer));
        menu.addButton(locationsButton);

        IMenuButton bossesButton = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.SKELETON_SPAWN_EGG)
                        .name("&eБоссы")
                        .lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7>> Открыть").build()).build().item())
                .setSlot(16);
        bossesButton.setClickEvent(event -> openBosses(gamer));
        menu.addButton(bossesButton);

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(26);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    private void openMines(Gamer gamer) {
        StandardMenu menu = StandardMenu.create(5, "&eШахты");
        int i = 0;
        for (Mines mines : Mines.getMines()) {
            if (mines.needPerm()) continue;
            IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(mines.getPrisonIcon(gamer, false))
                    .setSlot(i++);
            bt.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if(mines.canTeleport(gamer, true)) {
                    g.teleport(mines.getSpawn());
                    p.closeInventory();
                }
            });
            if (mines.hasBoss() && mines.canTeleport(gamer))
                UpdateMenu.builder()
                        .mineBossUpdate(mines)
                        .gamerLive(gamer)
                        .start(0)
                        .build().update(menu, bt);
            menu.addButton(bt);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> new MinesMenu(gamer.getPlayer()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    private void openJobs(Gamer gamer) {
        StandardMenu menu = StandardMenu.create(5, "&eРаботы");
        int i = 0;
        for (EJobs job : EJobs.values()) {
            Job j = job.getJob();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(j.getButton(gamer).item()).setSlot(i++);
            btn.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if (g.getIntStatistics(EStat.LEVEL) >= j.getLevel() ||
                        g.hasPermission("*")) {
                    g.teleport(j.getLocation(j));
                    g.setCurrentJob(job);
                    p.closeInventory();
                } else {
                    gamer.sendMessage(Utils.colored(EMessage.JOBLEVEL.getMessage().replace("%level%", j.getLevel() + "")));
                }
            });
            menu.addButton(btn);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> new MinesMenu(gamer.getPlayer()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    private void openLocations(Gamer gamer) {
        StandardMenu menu = StandardMenu.create(5, "&eЛокации");
        int i = 0;
        for (Mines mines : Mines.getMines()) {
            if (!mines.needPerm()) continue;
            IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(mines.getPrisonIcon(gamer, false))
                    .setSlot(i++);
            bt.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if(mines.canTeleport(gamer, true)) {
                    g.teleport(mines.getSpawn());
                    p.closeInventory();
                }
            });
            if (mines.hasBoss() && mines.canTeleport(gamer))
                UpdateMenu.builder()
                        .mineBossUpdate(mines)
                        .gamerLive(gamer)
                        .start(0)
                        .build().update(menu, bt);
            menu.addButton(bt);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> new MinesMenu(gamer.getPlayer()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    private void openBosses(Gamer gamer) {
        StandardMenu menu = StandardMenu.create(5, "&eБоссы");
        int i = 0;
        for (Mines mines : Mines.getMines()) {
            if (!mines.hasBoss()) continue;
            IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(mines.getPrisonIcon(gamer, true)).setSlot(i++);
            bt.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if(mines.canTeleport(gamer, true)) {
                    g.teleport(mines.getSpawn());
                    p.closeInventory();
                }
            });
            if (mines.hasBoss() && mines.canTeleport(gamer))
                UpdateMenu.builder()
                        .mineBossUpdate(mines)
                        .gamerLive(gamer)
                        .start(0)
                        .build().update(menu, bt);
            menu.addButton(bt);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> new MinesMenu(gamer.getPlayer()));
        menu.addButton(back);

        menu.build();
        menu.open(gamer);
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Выберете следующее меню";
    }
}
