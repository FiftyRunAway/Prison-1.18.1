package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.runaway.Gamer;

/*
 * Created by _RunAway_ on 10.2.2019
 */

public class ModeMenu implements IMenus {

    private static Inventory inventory;

    public ModeMenu() {
        inventory = Bukkit.createInventory(null, getRows() * 9, getName());/*
        inventory.setItem(10, Items.item(Material.STONE_SWORD, "Посижу на лайте <3 &7(" + EMode.EASY.getPercent() + "% от стандарта)", Arrays.asList(
                "&fВыбирая этот режим игры,",
                "&fВам проще всего прокачиваться.",
                "&4НО: Вы не сможете сражаться с",
                "&4игроками другой сложности!")));
        inventory.setItem(13, Items.item(Material.IRON_SWORD, "&aСамое то! &7(" + EMode.NORMAL.getPercent() + "% от стандарта)", Arrays.asList(
                "&fВыбирая этот режим игры,",
                "&fВы прокачиваетесь по стандарту.",
                "&4НО: Вы не сможете сражаться с",
                "&4игроками другой сложности!")));
        inventory.setItem(16, Items.item(Material.DIAMOND_SWORD, "&4Хочу боооли/AHAHAHAH &7(" + EMode.HARD.getPercent() + "% от стандарта)", Arrays.asList(
                "&fВыбирая этот режим игры,",
                "&fПрокачка почти нереальна,",
                "&fтолько для &cХАРДКОРЩИКОВ 3>",
                "&4НО: Вы не сможете сражаться с",
                "&4игроками другой сложности!")));*/
    }

    public void open(Gamer gamer) {
        gamer.getPlayer().openInventory(inventory);
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW +  "Выбор сложности";
    }
}
