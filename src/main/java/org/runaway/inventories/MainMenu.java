package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.events.PlayerInteract;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.events.ClickType;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Lore;
import org.runaway.utils.Vars;

public class MainMenu implements IMenus {

    private static StandardMenu menu;

    public MainMenu(Player player) {
        player.openInventory(menu.build());
    }

    public static void load() {
        try {
            menu = StandardMenu.create(6, ChatColor.YELLOW + "Профиль");

            IMenuButton ups = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_upg").getItemStack())
                    .setSlot(22);
            ups.setClickEvent(event -> new UpItemsMenu(event.getWhoClicked()));
            menu.addButton(ups);

            IMenuButton perks = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_passive").getItemStack())
                    .setSlot(14);
            perks.setClickEvent(event -> event.getWhoClicked().openInventory(PassivePerksMenu.getMenu(event.getWhoClicked()).build()));
            menu.addButton(perks);

            IMenuButton achievs = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_achieves").getItemStack())
                    .setSlot(23);
            achievs.setClickEvent(event -> new AchievementsMenu(event.getWhoClicked()));
            menu.addButton(achievs);

            IMenuButton exp = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_lvl").getItemStack())
                    .setSlot(21);
            exp.setClickEvent(event -> {
                Gamer gamer = Prison.gamers.get(event.getWhoClicked().getUniqueId());
                if (!gamer.needRebirth()) {
                    new LevelMenu(event.getWhoClicked());
                } else {
                    event.getWhoClicked().openInventory(RebirthMenu.getMenu(event.getWhoClicked()));
                }
            });
            menu.addButton(exp);

            IMenuButton ah = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_ah").getItemStack())
                    .setSlot(12);
            ah.setClickEvent(event ->
                    event.getWhoClicked().performCommand("ah"));
            menu.addButton(ah);

            IMenuButton rebirth = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_rebirth").getItemStack())
                    .setSlot(49);
            rebirth.setClickEvent(event -> event.getWhoClicked().openInventory(RebirthMenu.getMenu(event.getWhoClicked())));
            menu.addButton(rebirth);

            IMenuButton donate = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_donate").getItemStack())
                    .setSlot(13);
            donate.setClickEvent(event -> new DonateMenu(event.getWhoClicked()));
            menu.addButton(donate);

            IMenuButton mines = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_mines").getItemStack())
                    .setSlot(31);
            mines.setClickEvent(event -> new MinesMenu(event.getWhoClicked()));
            menu.addButton(mines);

            IMenuButton boosters = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_boosters").getItemStack())
                    .setSlot(39);
            boosters.setClickEvent(event -> {
                if (event.getClickType().equals(ClickType.LEFT)) {
                    new BoostersMenu(event.getWhoClicked());
                } else {
                    new BoosterMenu(event.getWhoClicked());
                }
            });
            menu.addButton(boosters);

            IMenuButton blocks = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_blocks").getItemStack())
                    .setSlot(41);
            blocks.setClickEvent(event -> new BrockenBlocksMenu(event.getWhoClicked()));
            menu.addButton(blocks);

            IMenuButton trash = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_trash").getItemStack())
                    .setSlot(0);
            trash.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                Gamer gamer = Prison.gamers.get(event.getWhoClicked().getUniqueId());
                gamer.teleportTrashAuction();
            });
            menu.addButton(trash);

            IMenuButton base = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_base").getItemStack())
                    .setSlot(8);
            base.setClickEvent(event -> GamerManager.getGamer(event.getWhoClicked()).teleportBase());
            menu.addButton(base);

            IMenuButton spawn = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_spawn").getItemStack())
                    .setSlot(45);
            spawn.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                Prison.gamers.get(event.getWhoClicked().getUniqueId()).teleport(Prison.SPAWN);
            });
            menu.addButton(spawn);

            IMenuButton sell = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_sell").getItemStack())
                    .setSlot(40);
            sell.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                PlayerInteract.sellAll(event.getWhoClicked());
            });
            menu.addButton(sell);

            IMenuButton quests = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_quests").getItemStack())
                    .setSlot(32);
            quests.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().performCommand("quest");
            });
            menu.addButton(quests);

            IMenuButton bp = DefaultButtons.FILLER.getButtonOfItemStack(
                    ItemManager.getPrisonItem("main_bp").getItemStack())
                    .setSlot(30);
            bp.setClickEvent(event -> new BattlePassMenu(event.getWhoClicked()));
            menu.addButton(bp);

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load profile menu!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Профиль";
    }
}
