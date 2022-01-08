package org.runaway.inventories;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.battlepass.WeeklyMission;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.managers.GamerManager;
import org.runaway.menu.SimpleItemStack;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BattlePassMenu implements IMenus {

    private StandardMenu missionsMenu;
    @Getter
    private StandardMenu main;
    private static int max_level;
    private static double pages;
    private Gamer gamer;

    private static HashMap<Integer, ArrayList<IMenuButton>> preloaded_icons = new HashMap<>();
    public static HashMap<String, Integer> data = new HashMap<>();

    BattlePassMenu(Player player) {
        gamer = GamerManager.getGamer(player);
        main = StandardMenu.create(6, ChatColor.YELLOW + "Боевой пропуск");
        missionsMenu = StandardMenu.create(6, ChatColor.YELLOW +  "Боевой пропуск &7• &eИспытания");
        main.addChild(ChatColor.YELLOW +  "Боевой пропуск &7• &eИспытания", missionsMenu);

        IMenuButton exp = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.EXP_BOTTLE)
                .name(ChatColor.AQUA + "" + gamer.getIntStatistics(EStat.BATTLEPASS_LEVEL) + " &eуровень боевого пропуска")
                .lore(new Lore.BuilderLore()
                        .addString("           &7Опыт:")
                        .addString(progresBarLevel(gamer))
                        .addString("&70                    " + BattlePass.level)
                        .build())
                .build().item()).setSlot(49);
        getMain().addButton(exp);

        IMenuButton wm = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.SIGN)
                .name("&eПосмотреть испытания")
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7>> Открыть").build())
                .build().item()).setSlot(51);
        wm.setClickEvent(e -> openMissionsMenu(e.getWhoClicked(), missionsMenu));
        main.addButton(wm);

        openStartPage();
    }

    private void openPage(int page, Gamer g) {
        StandardMenu menu = getMain();
        menu.setTitle(ChatColor.YELLOW + "Боевой пропуск (" + (page + 1) + "/" + Math.round(Math.ceil(pages)) + ")");
        for (int i = 0; i < 36; i++) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(i));
        }
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(45));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(53));

        Lore lore_bought = new Lore.BuilderLore()
                .addSpace()
                .addString("&6Вы приобрели боевой пропуск " + BattlePass.season + " сезона!")
                .build();
        Lore lor = new Lore.BuilderLore()
                .addSpace()
                .addString("&aМожно всегда приобрести в &e/donate")
                .addString("&aВсе платные награды вы сможете получить")
                .addString("&aпосле покупки!")
                .build();
        IMenuButton desc = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.KNOWLEDGE_BOOK)
                .name("&eБоевой пропуск")
                .lore(gamer.hasBattlePass() ? lore_bought : lor)
                .build().item()).setSlot(47);
        main.addButton(desc);

        if (!gamer.hasBattlePass())
            UpdateMenu.builder()
                    .updateType(new SimpleItemStack[]{SimpleItemStack.builder()
                            .material(Material.BOOK_AND_QUILL)
                            .durability(0).build()})
                    .gamerLive(gamer)
                    .build().update(main, desc);

        List<IMenuButton> btns = new ArrayList<>(preloaded_icons.get(page));
        if (page < Math.ceil(pages - 1)) {
            IMenuButton next = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aВперёд").build().item()).setSlot(53);
            next.setClickEvent(event -> {
                Gamer ga = GamerManager.getGamer(event.getWhoClicked().getUniqueId());
                openPage(data.get(ga.getGamer()) + 1, ga);
            });

            btns.add(next);
        }
        if (page != 0) {
            IMenuButton back = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aНазад").build().item()).setSlot(45);
            back.setClickEvent(event -> {
                Gamer ga = GamerManager.getGamer(event.getWhoClicked().getUniqueId());
                openPage(data.get(ga.getGamer()) - 1, ga);
            });

            btns.add(back);
        }

        btns.forEach(menu::addButton);
        data.put(g.getGamer(), page);

        boolean pass = true;
        int player_level = g.getIntStatistics(EStat.BATTLEPASS_LEVEL);
        boolean hasMaxLevel = false;
        int maxLevel = page * 9 + 9;
        if (max_level < maxLevel && max_level >= page * 9) hasMaxLevel = true;

        if (page > 0) {
            for (int i = 9; i < 18; i++) {
                int level = page * 9 + (i - 9);
                if (hasMaxLevel && level > max_level) break;
                if (pass && player_level < level) pass = false;
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(glass(pass, level, g)).setSlot(i));
            }
        } else {
            for (int i = 10; i < 18; i++) {
                if (hasMaxLevel && i - 9 > max_level) break;
                if (pass && player_level < i - 9) pass = false;
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(glass(pass, i - 9, g)).setSlot(i));
            }
        }

        //Pinned tasks
        int pinned = g.getPins().size();
        int pin_slots = 36 + pinned;
        List<IMission> missions = g.getPins();
        int k = 0;
        for (int i = 37; i < 44; i++) {
            if (i <= pin_slots) {
                IMission mission;
                try {
                    mission = missions.get(k);
                } catch (Exception exception) {
                    continue;
                }
                k++;
                Item.Builder ic = mission.getIcon().getIcon(gamer);
                ArrayList<String> lore = new ArrayList<>(ic.build().getLore().getList());
                lore.add(" ");
                lore.add(Utils.colored("&7• &c&nОткрепить"));
                IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(ic.lore(new Lore.BuilderLore().addList(lore).build()).build().item())
                        .setSlot(i);
                btn.setClickEvent(e -> {
                    BattlePass.unPin(mission, GamerManager.getGamer(e.getWhoClicked().getUniqueId()));
                    openPage(data.getOrDefault(gamer.getGamer(), 0), GamerManager.getGamer(e.getWhoClicked()));
                });
                menu.addButton(btn);
            } else {
                IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.IRON_FENCE)
                        .name("&8Свободная ячейка").lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7• &eМеню испытаний").build())
                        .build().item()).setSlot(i);
                btn.setClickEvent(event -> {
                    openMissionsMenu(event.getWhoClicked(), missionsMenu);
                });
                menu.addButton(btn);
            }
        }

        menu.open(gamer);
    }

    private void openStartPage() {
        int level = gamer.getIntStatistics(EStat.BATTLEPASS_LEVEL);
        int page = 0;
        if (gamer.hasBattlePass() && max_level > 8 && level >= 8) page = (level + 1) / 9;
        if (level > max_level) page = (int) Math.ceil(pages) - 1;

        openPage(page >= Math.ceil(pages) ? page - 1 : page, gamer);
    }

    private static ItemStack glass(boolean opened, int level, Gamer gamer) {
        return ExampleItems.glass(opened ? 5 : (gamer.getIntStatistics(EStat.BATTLEPASS_LEVEL) + 1 == level ? 4 : 14), ChatColor.GRAY + "" + level + " Уровень");
    }

    private static void preLoadingMenuButtons(int page) {
        ArrayList<IMenuButton> btns = new ArrayList<>();

        BattlePass.slots.forEach((reward, slot) -> {
            if (slot >= page * 54 && slot < (page + 1) * 54) {
                int sl;
                if (page > 0) {
                    sl = slot - (page * 54);
                } else sl = slot;

                IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(reward.getIcon().getIcon()).setSlot(sl);
                btns.add(btn);
            }
        });

        if (page == 0) {
            btns.add(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.COMPASS)
                    .name("&bБесплатные награды ->")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7Эти награды вы получите,")
                            .addString("&7даже &eне покупая &7боевой пропуск!")
                            .build())
                    .build().item()).setSlot(0));
            btns.add(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.COMPASS)
                    .name("&bПлатные награды ->")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7Эти награды вы получите,")
                            .addString("&eтолько имея &7боевой пропуск!")
                            .build())
                    .build().item()).setSlot(18));
            int[] slots = { 9, 27 };
            Arrays.stream(slots).forEach(slot ->
                    btns.add(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(11)).setSlot(slot)));
        }

        int[] slots = { 36, 44 };
        Arrays.stream(slots).forEach(slot ->
                btns.add(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(11)).setSlot(slot)));

        preloaded_icons.put(page, btns);
    }

    public static void load() {
        max_level = EConfig.BATTLEPASS.getConfig().getConfigurationSection("levels").getKeys(false).size();
        pages = (Double.parseDouble(String.valueOf(BattlePass.glass_slots.size())) + 1) / 9;

        // Pre-loading paged rewards
        for (int i = 0; i < Math.ceil(pages); i++) {
            preLoadingMenuButtons(i);
        }
    }

    private void openMissionsMenu(Player player, StandardMenu menu) {
        AtomicInteger i = new AtomicInteger(19);
        BattlePass.missions.forEach(wm -> {

            ArrayList<String> lore = new ArrayList<>();
            Date now = new Date();

            boolean opened = false;
            if (wm.getDate().getTime() < now.getTime()) opened = true;

            String name_wm = wm.getName();
            if (!opened) {
                long diff = new Date(wm.getDate().getTime() - now.getTime()).getTime();

                if (diff > 86400000) {
                    lore.add("&7>> Откроется через: &b" + diff / 86400000 + " дн.");
                } else {
                    lore.add("&7>> Откроется через: &b" + diff / 3600000 + " часов по МСК");
                }
                name_wm = ChatColor.MAGIC + wm.getName();
            } else {
                lore.add("&7Начались &b" + new SimpleDateFormat("dd.MM").format(wm.getDate()));
                lore.add("&7>> Посмотреть задания");
            }

            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.PAPER)
                    .name("&e" + name_wm)
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addList(lore)
                            .build())
                    .build().item()).setSlot(i.getAndIncrement());

            boolean finalOpened = opened;
            btn.setClickEvent(e -> {
                if (finalOpened) {
                    openTasksMenu(GamerManager.getGamer(e.getWhoClicked().getUniqueId()), wm);
                }
            });
            menu.addButton(btn);

            if (i.get() == 26) i.set(28);
        });

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(53);
        back.setClickEvent(event -> new BattlePassMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.open(GamerManager.getGamer(player));
    }

    private String progresBarLevel(Gamer gamer) {
        StringBuilder sb = new StringBuilder();
        double pr = Double.parseDouble(gamer.getStatistics(EStat.BATTLEPASS_SCORE).toString());
        double progress = Math.ceil((pr / BattlePass.level) * 100);
        double pos = Math.ceil(progress / 10);
        for (int i = 0; i < 10; i++) {
            if (i < pos) {
                sb.append("&a&m--");
            } else {
                sb.append("&8&m--");
            }
        }
        sb.append("&8[&c").append(Math.round(progress)).append("%&8]");
        return Utils.colored(sb.toString());
    }

    private void openTasksMenu(Gamer gamer, WeeklyMission missions) {
        StandardMenu menu = StandardMenu.create(4, ChatColor.YELLOW +  "Боевой пропуск &7• " + missions.getName());
        AtomicInteger i = new AtomicInteger(10);
        missions.getMissions().forEach(mission -> {
            Item.Builder ic = mission.getIcon().getIcon(gamer);
            ArrayList<String> lore = new ArrayList<>(ic.build().getLore().getList());
            if (!mission.isCompleted(gamer)) {
                lore.add(" ");
                if (mission.isPinned(gamer)) {
                    lore.add(Utils.colored("&7• &c&nОткрепить"));
                } else {
                    lore.add(Utils.colored("&7• &e&nЗакрепить"));
                }
            }
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(ic.lore(new Lore.BuilderLore().addList(lore).build()).build().item())
                    .setSlot(i.getAndIncrement());
            btn.setClickEvent(e -> {
                Gamer g = GamerManager.getGamer(e.getWhoClicked().getUniqueId());
                if (mission.isCompleted(g))
                    return;
                if (mission.isPinned(gamer)) {
                    BattlePass.unPin(mission, g);
                } else {
                    BattlePass.addPin(mission, g);
                }
                openTasksMenu(g, missions);
            });
            menu.addButton(btn);
            if (i.get() == 16) i.set(19);
        });

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(35);
        back.setClickEvent(event -> openMissionsMenu(event.getWhoClicked(), missionsMenu));
        menu.addButton(back);
        menu.open(gamer);
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW +  "Боевой пропуск";
    }
}
