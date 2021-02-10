package org.runaway.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.runaway.items.Item;
import org.runaway.enums.EStat;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.rebirth.ESkill;
import org.runaway.rebirth.RSkill;
import org.runaway.utils.ExampleItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class RebirthMenu implements IMenus {

    private static ArrayList<RSkill> skills;
    private static StandardMenu menu;
    private static int[] pass;

    public static StandardMenu getMenu(Player player) {
        StandardMenu inventory = menu;
        AtomicInteger i = new AtomicInteger(0);

        skills.forEach(skill -> inventory.addButton(skill.getIcon(player).setSlot(pass[i.getAndIncrement()])));

        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.KNOWLEDGE_BOOK)
                .name("&fОчки перерождения: &d" + GamerManager.getGamer(player).getStatistics(EStat.REBIRTH_SCORE) + " ОП")
                .build().item()).setSlot(40));

        return inventory;
    }

    public static void load() {
        menu = StandardMenu.create(5, "&eПерерождение");
        pass = new int[] { 10, 12, 14, 16, 28, 30, 32, 34 };
        for (int i = 0; i < 45; i++) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(8)).setSlot(i));
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(44);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        skills = new ArrayList<>();
        Arrays.stream(ESkill.values()).forEach(skill -> skills.add(skill.getSkill()));
    }


    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public String getName() {
        return "&eПерерождение";
    }
}
