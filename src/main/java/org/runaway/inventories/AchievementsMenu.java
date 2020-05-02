package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.runaway.Item;
import org.runaway.menu.button.IMenuButton;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EConfig;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.type.StandardMenu;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AchievementsMenu implements IMenus {

    private static StandardMenu menu;

    public AchievementsMenu(Player player) {
        menu = StandardMenu.create(getRows(), getName());
        FileConfiguration file = EConfig.ACHIEVEMENTS.getConfig();
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(Achievement.values()).forEach(achievement -> {
            List l = file.getStringList(achievement.toString());
            if (l.contains(player.getName())) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(achievement.getIcon(true)).setSlot(i.getAndIncrement()));
            }
        });
        Arrays.stream(Achievement.values()).forEach(achievement -> {
            List l = file.getStringList(achievement.toString());
            if (!l.contains(player.getName())) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(achievement.getIcon(false)).setSlot(i.getAndIncrement()));
            }
        });
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(53);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Ваши достижения";
    }
}
