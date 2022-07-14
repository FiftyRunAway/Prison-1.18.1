package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.items.ItemManager;
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

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class MinesMenu implements IMenus {

    public MinesMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);

        AtomicInteger in = new AtomicInteger(36);
        Mines.mines.forEach(mines -> {
            IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(mines.getPrisonIcon(gamer))
                    .setSlot(mines.needPerm() ? in.getAndIncrement() : mines.getMinLevel() - 1);
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
        });

        for (EJobs job : EJobs.values()) {
            Job j = job.getJob();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(j.getButton(gamer).item()).setSlot(j.getLevel() - 1);
            btn.setClickEvent(event -> {
                Player p = event.getWhoClicked();
                Gamer g = GamerManager.getGamer(p);
                if (!g.hasPermission("prison.job.test") && !p.isOp()) {
                    g.sendMessage("Пока только для отдельных игроков");
                    return;
                }
                if (g.getIntStatistics(EStat.LEVEL) >= j.getLevel()) {
                    g.teleport(j.getLocation(j));
                    g.setCurrentJob(job);
                    p.closeInventory();
                } else {
                    gamer.sendMessage(Utils.colored(EMessage.JOBLEVEL.getMessage().replaceAll("%level%", j.getLevel() + "")));
                }
            });
            menu.addButton(btn);
        }

        /*IMenuButton base = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.NETHER_STAR).name("&6База")
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Требования к доступу:")
                        .addString("&7• " + (gamer.getIntStatistics(EStat.LEVEL) < 5 ? ChatColor.RED : ChatColor.GREEN)  + "Минимальный уровень • 5")
                        .addString("&7• " + (gamer.getFaction().equals(FactionType.DEFAULT) ? ChatColor.RED : ChatColor.GREEN)  + "Вступление во фракцию")
                        .build())
                .build().item()).setSlot(4);
        base.setClickEvent(event -> GamerManager.getGamer(event.getWhoClicked()).teleportBase());
        menu.addButton(base);*/


        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
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
