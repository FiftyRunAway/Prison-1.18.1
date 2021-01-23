package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.IMenuButton;
import org.runaway.achievements.Achievement;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.type.StandardMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AchievementsMenu implements IMenus {

    private static StandardMenu menu;

    public AchievementsMenu(Player player) {
        menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);
        AtomicInteger i = new AtomicInteger();
        List<Achievement> achievs = new ArrayList<>(Arrays.asList(Achievement.values()));
        List<Achievement> toRemove = new ArrayList<>();

        achievs.forEach(achievement -> {
            if (gamer.getAchievements().contains(achievement)) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(achievement.getIcon(true)).setSlot(i.getAndIncrement()));
                toRemove.add(achievement);
            }
        });
        achievs.removeAll(toRemove);
        toRemove.clear();
        achievs.forEach(achievement -> {
            if (!gamer.getAchievements().contains(achievement) && !achievement.isSecret()) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(achievement.getIcon(false)).setSlot(i.getAndIncrement()));
                toRemove.add(achievement);
            }
        });
        achievs.removeAll(toRemove);
        toRemove.clear();
        achievs.forEach(achievement ->
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(achievement.getIcon(false)).setSlot(i.getAndIncrement())));
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
