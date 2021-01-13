package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Lore;

import java.util.concurrent.atomic.AtomicInteger;

public class BrockenBlocksMenu implements IMenus {

    BrockenBlocksMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);

        if (gamer.getBlocksValues().isEmpty()) {
            gamer.sendMessage(EMessage.BROCKENBLOCKS);
            player.closeInventory();
            return;
        }

        AtomicInteger i = new AtomicInteger(0);
        gamer.getBlocksValues().forEach((s, amount) ->
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.valueOf(s.split("-")[0]))
                .data(Short.parseShort(s.split("-")[1]))
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7>> &e" + Math.round(amount) + " &fблоков сломано").build()).build().item()).setSlot(i.getAndIncrement())));
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
        return ChatColor.YELLOW + "Вскопанные блоки";
    }
}
