package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.enums.EMessage;
import org.runaway.jobs.Job;
import org.runaway.jobs.JobReq;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class JobUpgradeMenu implements IMenus {

    public static StandardMenu getMenu(Gamer gamer, Job job) {
        StandardMenu menu = StandardMenu.create(3, ChatColor.YELLOW + "Работа • " + job.getName());

        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(job.getLevels().get(Job.getLevel(gamer, job))).forEach(jobReq ->
                list.add(" &7• " + (Job.hasStatistics(gamer, jobReq) ? ChatColor.GREEN : ChatColor.RED) +
                jobReq.getRequriement().getName() + ": " + Job.getStatistics(gamer, jobReq.getRequriement()) + "/" + jobReq.getValue()));

        IMenuButton upgrade = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.EXPERIENCE_BOTTLE)
                .name("&eУлучшение работы")
                .lore(new Lore.BuilderLore()
                        .addString("&7Собирай ресурсы, чтобы")
                        .addString("&7&nповышать свою награду&r&7 за работу!")
                        .addSpace()
                        .addString("&7Требования:")
                        .addList(list)
                        .build())
                .build().item()).setSlot(11);
        upgrade.setClickEvent(event -> {
            Player player = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(player);
            for (JobReq jobReq : job.getLevels().get(Job.getLevel(g, job))) {
                if (!Job.hasStatistics(g, jobReq)) {
                    g.sendMessage(Utils.colored(EMessage.NOTENOUGHPROPERTY.getMessage().replace("%property%", jobReq.getRequriement().getName().toLowerCase())));
                    player.closeInventory();
                    return;
                } else {
                    if (jobReq.getRequriement().isTaken()) {
                        Job.take(g, jobReq);
                    }
                }
            }
            Job.addStatistics(g, job.getClass().getSimpleName().toLowerCase());
            player.sendMessage(Utils.colored(EMessage.JOBUPGRADE.getMessage().replace("%job%", job.getName())));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 10, 10);
            g.sendTitle("&b" + job.getName(), "&eповышен уровень работы!");
            player.closeInventory();
        });
        menu.addButton(upgrade);

        IMenuButton info = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BIRCH_SIGN)
                .name("&7Уровень данной работы: &e" + Job.getLevel(gamer, job) + "/" + job.getMaxLevel())
                .build().item()).setSlot(13);
        menu.addButton(info);

        IMenuButton desc = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(job.getMaterial())
                .name("&cОписание")
                .lore(new Lore.BuilderLore()
                        .addString(ChatColor.YELLOW + job.getDescrition() + ",")
                        .addString("&eчтобы прокачивать уровень своей")
                        .addString("&eработы и тем самым &nповышать награду!")
                        .build())
                .build().item()).setSlot(15);
        menu.addButton(desc);
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(26);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);
        return menu;
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Работа";
    }
}
