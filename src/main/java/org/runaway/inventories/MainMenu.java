package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.auctions.TrashAuction;
import org.runaway.enums.EMessage;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.events.PlayerInteract;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.events.ClickType;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.concurrent.atomic.AtomicInteger;

public class MainMenu implements IMenus {

    private static StandardMenu menu;

    public MainMenu(Player player) {
        player.openInventory(menu.build());
    }

    public static void load() {
        try {
            menu = StandardMenu.create(6, ChatColor.YELLOW + "Профиль");

            IMenuButton ups = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.WOOD_AXE)
                    .name("&aПрокачки")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(22);
            ups.setClickEvent(event -> new UpItemsMenu(event.getWhoClicked()));
            menu.addButton(ups);

            IMenuButton achievs = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STORAGE_MINECART)
                    .name("&aДостижения")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(23);
            achievs.setClickEvent(event -> new AchievementsMenu(event.getWhoClicked()));
            menu.addButton(achievs);

            IMenuButton exp = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.EXP_BOTTLE)
                    .name("&aПовысить уровень")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(21);
            exp.setClickEvent(event -> {
                Gamer gamer = Main.gamers.get(event.getWhoClicked().getUniqueId());
                if (!gamer.needRebirth()) {
                    new LevelMenu(event.getWhoClicked());
                } else {
                    event.getWhoClicked().openInventory(RebirthMenu.getMenu(event.getWhoClicked()));
                }
            });
            menu.addButton(exp);

            IMenuButton ah = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.ENDER_CHEST)
                    .name("&aАукционы")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(12);
            ah.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().performCommand("ah");
            });
            menu.addButton(ah);

            IMenuButton rebirth = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.EYE_OF_ENDER)
                    .name("&aМеню переождения")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(49);
            rebirth.setClickEvent(event -> event.getWhoClicked().openInventory(RebirthMenu.getMenu(event.getWhoClicked())));
            menu.addButton(rebirth);

            IMenuButton donate = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND)
                    .name("&aПожертвования")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(13);
            donate.setClickEvent(event -> new DonateMenu(event.getWhoClicked()));
            menu.addButton(donate);

            IMenuButton mines = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GRASS)
                    .name("&aСписок шахт")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(31);
            mines.setClickEvent(event -> new MinesMenu(event.getWhoClicked()));
            menu.addButton(mines);

            IMenuButton boosters = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_BLOCK)
                    .name("&aБустеры")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню активации")
                            .addString("&7<< Открыть меню активных").build()).build().item())
                    .setSlot(39);
            boosters.setClickEvent(event -> {
                if (event.getClickType().equals(ClickType.LEFT)) {
                    new BoostersMenu(event.getWhoClicked());
                } else {
                    new BoosterMenu(event.getWhoClicked());
                }
            });
            menu.addButton(boosters);

            IMenuButton blocks = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.SIGN)
                    .name("&aСписок сломанных блоков")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть меню").build()).build().item())
                    .setSlot(41);
            blocks.setClickEvent(event -> new BrockenBlocksMenu(event.getWhoClicked()));
            menu.addButton(blocks);

            IMenuButton trash = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.MINECART)
                    .name("&eПеремещение на свалку-аукцион")
                    .lore(new Lore.BuilderLore()
                            .addString("&cТолько 1.15.2!")
                            .addString("&7>> Перемещение")
                            .addString("&7<< Подробнее").build()).build().item())
                    .setSlot(0);
            trash.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                Gamer gamer = Main.gamers.get(event.getWhoClicked().getUniqueId());
                if (event.getClickType().equals(ClickType.LEFT)) {
                    StringBuilder times = new StringBuilder();
                    AtomicInteger i = new AtomicInteger(0);
                    TrashAuction.times.forEach(integer -> {
                        times.append(integer);
                        if (TrashAuction.times.size() != i.getAndIncrement() + 1) times.append(", ");
                    });
                    event.getWhoClicked().sendMessage(Utils.colored(EMessage.AUCTIONTIMES.getMessage().replaceAll("%time%", times.toString() + " часов по МСК")));
                } else {
                    gamer.teleportTrashAuction();
                }
            });
            menu.addButton(trash);

            IMenuButton base = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.NETHER_STAR)
                    .name("&eПеремещение на базу")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Перемещение").build()).build().item())
                    .setSlot(8);
            base.setClickEvent(event -> Main.gamers.get(event.getWhoClicked().getUniqueId()).teleportBase());
            menu.addButton(base);

            IMenuButton spawn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DOUBLE_PLANT)
                    .name("&eПеремещение на спавн")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Перемещение").build()).build().item())
                    .setSlot(45);
            spawn.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                Main.gamers.get(event.getWhoClicked().getUniqueId()).teleport(Main.SPAWN);
            });
            menu.addButton(spawn);

            IMenuButton sell = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.PAPER)
                    .name("&cПродать все блоки в инвентаре")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Продать").build()).build().item())
                    .setSlot(40);
            sell.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                PlayerInteract.sellAll(event.getWhoClicked());
            });
            menu.addButton(sell);

            IMenuButton quests = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.COMPASS)
                    .name("&eКвесты")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Посмотреть").build()).build().item())
                    .setSlot(32);
            quests.setClickEvent(event -> {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().performCommand("quest");
            });
            menu.addButton(quests);

            IMenuButton bp = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.TNT)
                    .name("&eБоевой пропуск &d(НОВИНКА!)")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Открыть").build()).build().item())
                    .setSlot(30);
            bp.setClickEvent(event -> new BattlePassMenu(event.getWhoClicked()));
            //menu.addButton(bp);

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load profile menu!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
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
