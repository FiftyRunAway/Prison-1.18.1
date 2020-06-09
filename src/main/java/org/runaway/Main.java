package org.runaway;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.achievements.Achievement;
import org.runaway.auction.TrashAuction;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.missions.*;
import org.runaway.board.Board;
import org.runaway.boosters.GBlocks;
import org.runaway.boosters.GMoney;
import org.runaway.cases.Case;
import org.runaway.commands.*;
import org.runaway.configs.Config;
import org.runaway.donate.Donate;
import org.runaway.donate.DonateIcon;
import org.runaway.entity.Spawner;
import org.runaway.enums.*;
import org.runaway.events.*;
import org.runaway.google.TWOFA;
import org.runaway.inventories.*;
import org.runaway.menu.MenuListener;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mine;
import org.runaway.mines.Mines;
import org.runaway.trainer.Trainer;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class Main extends JavaPlugin {

    public static HashMap<UUID, Gamer> gamers = new HashMap<>();

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private ServerStatus status = null;

    //Кейсы
    public static ArrayList<Case> cases = new ArrayList<>();

    public static BossBar MoneyBar, BlocksBar;
    public static GBlocks gBlocks = new GBlocks();
    public static GMoney gMoney = new GMoney();
    public static boolean isAutoRestart = false;

    // API
    public static boolean useHolographicDisplays;
    public static boolean usePermissionsEx;
    public static boolean useNametagEdit;
    public static boolean useViaVersion;

    //Событие
    public static String event = null;

    //Значения
    public static int value_mines;
    public static int value_shopitems;
    public static int value_donate;

    //Локации
    public static Location SPAWN;

    //Топы
    private static HashMap<String, TopPlayers> tops = new HashMap<>();

    // кэш /tip`a
    public static ArrayList<String> THXersBlocks = new ArrayList<>();
    public static ArrayList<String> THXersMoney = new ArrayList<>();

    // Список норм боссов
    public static ArrayList<UUID> bosses = new ArrayList<>();

    // Список шахт
    public static ArrayList<Mine> mines = new ArrayList<>();

    public void onEnable() {
        instance = this;
        loader();
    }

    public void onDisable() {
        if (getStatus().equals(ServerStatus.ERROR)) return;
        Utils.DisableKick();
        new Config().unloadConfigs();
        TrashAuction.closeAll();

        Vars.sendSystemMessage(TypeMessage.SUCCESS, "Plugin was successful disabled!");
    }

    private void loader() {
        new Config().loadConfigs();
        FileConfiguration loader = EConfig.MODULES.getConfig();

        if (loader.getBoolean("register.events")) registerEvents();
        if (loader.getBoolean("register.commands")) registerCommands();
        if (loader.getBoolean("loader.messages")) loadMessage();
        if (loader.getBoolean("loader.inventories")) loadInventories();
        if (loader.getBoolean("loader.hd")) loadHolographicDisplays();
        if (loader.getBoolean("loader.bossbar")) loadBar();
        if (loader.getBoolean("loader.mines_menu")) Mines.loadMinesMenu();
        if (loader.getBoolean("loader.tops")) loadTops();
        if (loader.getBoolean("loader.board")) new Board().loadBoard();
        if (loader.getBoolean("loader.block_list")) new BlockBreak().loadLogger();
        if (loader.getBoolean("loader.block_shop")) new PlayerInteract().loadShop();
        if (loader.getBoolean("loader.auto_restart")) new AutoRestart().loadAutoRestarter();
        if (loader.getBoolean("loader.mines")) loadMines();
        if (loader.getBoolean("loader.shop_items")) loadShopItems();
        if (loader.getBoolean("loader.cases")) loadCases();
        if (loader.getBoolean("loader.achievements")) Achievement.JOIN.load();
        if (loader.getBoolean("loader.donate_menu")) loadDonates();
        if (loader.getBoolean("loader.pex")) loadPermissionsEx();
        if (loader.getBoolean("loader.trainer")) loadTrainer();
        if (loader.getBoolean("loader.timer")) startTimer();
        if (loader.getBoolean("loader.upmenu")) UpItemsMenu.load();
        if (loader.getBoolean("loader.main_menu")) MainMenu.load();
        if (loader.getBoolean("loader.trash_auction")) TrashAuction.load();
        if (loader.getBoolean("loader.nametag")) loadNametagEdit();
        if (loader.getBoolean("loader.2fa")) loadTwoFA();
        if (loader.getBoolean("loader.auctionhouse")) loadTwoFA();
        if (loader.getBoolean("loader.viaversion")) loadViaVersion();

        if (loader.getBoolean("loader.battlepass")) BattlePass.load();

        if (loader.getBoolean("register.mobs")) registerMobs();

        if (loader.getBoolean("loader.server_status")) loadServerStatus();
        SPAWN = Utils.getLocation("spawn");
        Bukkit.getServer().getWorlds().forEach(world -> world.setGameRuleValue("announceAdvancements", "false"));
    }

    //Регистрация эвентов
    private static void registerEvents() {
        try {
            new Utils().RegisterEvent(new Cancelers());
            new Utils().RegisterEvent(new PlayerJoin());
            new Utils().RegisterEvent(new PlayerLogin());
            new Utils().RegisterEvent(new PlayerQuit());
            new Utils().RegisterEvent(new BlockBreak());
            new Utils().RegisterEvent(new SignChange());
            new Utils().RegisterEvent(new AsyncChat());
            new Utils().RegisterEvent(new PlayerInteract());
            new Utils().RegisterEvent(new PlayerInventoryClick());
            new Utils().RegisterEvent(new PlayerInventoryClose());
            new Utils().RegisterEvent(new PlayerDeath());
            new Utils().RegisterEvent(new PlayerMove());
            new Utils().RegisterEvent(new MenuListener());
            new Utils().RegisterEvent(new ListPing());
            new Utils().RegisterEvent(new PlayerAttack());

            new Utils().RegisterEvent(new TWOFA());

            // Missions events
            new Utils().RegisterEvent(new KeyFarm());
            new Utils().RegisterEvent(new WoodFarm());
            new Utils().RegisterEvent(new BlocksFarm());
            new Utils().RegisterEvent(new FishFarm());
            new Utils().RegisterEvent(new KillsFarm());
            new Utils().RegisterEvent(new TreasureFarm());

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering events!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Регистрация команд
    private void registerCommands() {
        try {
            Arrays.asList(new SetStatCommand(), new AutosellCommand(), new BoosterCommand(),
                    new LevelCommand(), new BoostersCommand(), new FractionCommand(), new RestartCommand(),
                    new SpawnCommand(), new UpgradeCommand(), new MinesCommand(), new TipCommand(),
                    new TrainerCommand(), new RebirthCommand(), new GiftCommand(),
                    new ShopCommand(), new AchievementsCommand(), new DonateCommand(),
                    new SpawnerCommand(), new ScrollsCommand(), new ProfileCommand(), new PayCommand(),
                    new BaseCommand(), new TrashCommand()).forEach(CommandManager::register);

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering commands!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Регистрация мобов
    private static void registerMobs() {
        try {
            Mobs.registerMobs();
            Spawner.SpawnerUtils.init();
            new Spawner.SpawnerUpdater().runTaskTimer(getInstance(), 20L, 600L);
            removeEntities();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering mobs!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    // Загрузка Google Authenticator
    private static void loadTwoFA() {
        try {
            new TWOFA();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with setting up 2-FA!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка кейсов
    private static void loadCases() {
        try {
            EConfig.CASES.getConfig().getKeys(false).forEach(st -> {
                HashMap<ItemStack, Float> drops = new HashMap<>();
                StandardMenu menu = StandardMenu.create(5, Utils.colored(EConfig.CASES.getConfig().getString(st + ".name") + " &7• &eПросмотр"));
                AtomicInteger i = new AtomicInteger();
                EConfig.CASES.getConfig().getStringList(st + ".drop").forEach(s -> {
                    String[] strings = s.split(":");
                    switch (strings[0].toLowerCase()) {
                        case "config": {
                            ItemStack is = UpgradeMisc.buildItem(strings[1], false, null, false);
                            drops.put(is, Float.valueOf(strings[2]));
                            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(is).setSlot(i.getAndIncrement()));
                            break;
                        } case "item": {
                            ItemStack is = ExampleItems.unserializerString(strings[1]);
                            drops.put(is, Float.valueOf(strings[2]));
                            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(is).setSlot(i.getAndIncrement()));
                            break;
                        } case "location": {
                            ItemStack is = ExampleItems.unserializerLocationItem(strings[1]);
                            drops.put(is, Float.valueOf(strings[2]));
                            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(is).setSlot(i.getAndIncrement()));
                            break;
                        }
                    }
                });
                ItemStack key = new Item.Builder(Material.valueOf(EConfig.CASES.getConfig().getString(st + ".key.material")))
                        .name(EConfig.CASES.getConfig().getString(st + ".key.name")).build().item();
                Case c = new Case(EConfig.CASES.getConfig().getBoolean(st + ".animation"), drops, Utils.unserializeLocation(EConfig.CASES.getConfig().getString(st + ".location")), Utils.colored(EConfig.CASES.getConfig().getString(st + ".name")), key, Material.valueOf(EConfig.CASES.getConfig().getString(st + ".material")), EConfig.CASES.getConfig().getInt(st + ".money"), menu.build());
                cases.add(c);
                if (useHolographicDisplays) {
                    Hologram hologram = HologramsAPI.createHologram(Main.getInstance(), c.getLocation().getBlock().getLocation().add(0.5, 2, 0.5));
                    if (hologram == null) {
                        System.out.println("hologram is null. but Why?...");
                        return;
                    }
                    hologram.insertTextLine(0, Utils.colored(c.getName()));
                }
                Vars.sendSystemMessage(TypeMessage.INFO, "Case '" + st + "' was loaded");
            });
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with load cases informations!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка босс-бара
    private void loadBar() {
        try {
            BlocksBar = Bukkit.createBossBar("Бустер блоков", BarColor.YELLOW, BarStyle.SOLID);
            BlocksBar.setVisible(false);
            MoneyBar = Bukkit.createBossBar("Бустер денег", BarColor.GREEN, BarStyle.SOLID);
            MoneyBar.setVisible(false);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with creating BossBar`s!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка инвентарей
    private void loadInventories() {
        try {
            new BlockShopMenu(null).load();
            new ShopMenu(null).load();
            Confirmation.load();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in creating inventories!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Привязка HolographicDisplays
    private void loadHolographicDisplays() {
        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        if (useHolographicDisplays) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "Holographic Displays was successfully connected");
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "Holographic Displays has not been installed yet");
    }

    //Привязка PermissionsEx
    private void loadPermissionsEx() {
        usePermissionsEx = Bukkit.getPluginManager().isPluginEnabled("PermissionsEx");
        if (usePermissionsEx) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "PermissionsEx was successfully connected");
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "PermissionsEx Displays has not been installed yet");
    }

    //Привязка NametagEdit
    private void loadNametagEdit() {
        useNametagEdit = Bukkit.getPluginManager().isPluginEnabled("NametagEdit");
        if (useNametagEdit) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "NametagEdit was successfully connected");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Utils.getPlayers().forEach(s -> gamers.get(Bukkit.getPlayer(s).getUniqueId()).setNametag());
                }
            }.runTaskTimer(instance, 20L, 1200L);
        } else {
            Vars.sendSystemMessage(TypeMessage.INFO, "NametagEdit has not been installed yet");
        }
    }

    //Привязка ViaVersion
    private void loadViaVersion() {
        useViaVersion = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        if (useViaVersion) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "ViaVersion was successfully connected");
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "ViaVersion has not been installed yet");
    }

    //Подгрузка статуса сервера
    private void loadServerStatus() {
        try {
            if (status == null) {
                Main.getInstance().setStatus(ServerStatus.valueOf(EConfig.CONFIG.getConfig().getString("status").toUpperCase()));
                Vars.sendSystemMessage(TypeMessage.SUCCESS,"Successful loaded status: " + getStatus().toString());
            }
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in setting status of server. Stopping server...");
            getServer().shutdown();
        }
    }

    private int errors = 0;

    //Подгрузка сообщений
    private void loadMessage() {
        try {
            if (EConfig.MESSAGES.getConfig().contains("prefix")) {
                Vars.setPrefix(EConfig.MESSAGES.getConfig().getString("prefix"));
            } else {
                EConfig.MESSAGES.getConfig().set("prefix", "&6&lPrison > &f");
                Vars.setPrefix("&6&lPrison > &f");
            }
            if (EConfig.MESSAGES.getConfig().contains("site")) {
                Vars.setSite(EConfig.MESSAGES.getConfig().getString("site"));
            } else {
                EConfig.MESSAGES.getConfig().set("site", "www.prison.com");
                Vars.setSite("www.prison.com");
            }
            errors = 0;
            if (!EConfig.MESSAGES.getConfig().contains("messages")) {
                EConfig.MESSAGES.getConfig().set("messages.UnavaliableMessage", EMessage.UNAVALIABLEMESSAGE.getDefaultMessage());
                EConfig.MESSAGES.saveConfig();
                errors++;
            }
            Arrays.stream(EMessage.values()).forEach(message -> {
                if (!message.equals(EMessage.UNAVALIABLEMESSAGE)) {
                    if (EConfig.MESSAGES.getConfig().contains("messages." + message.getConfigName())) {
                        EMessage.messages.put(message.getConfigName(), message.getMessageConfig());
                    } else {
                        EMessage.messages.put(message.getConfigName(), message.getDefaultMessage());
                        EConfig.MESSAGES.getConfig().set("messages." + message.getConfigName(), message.getDefaultMessage());
                        errors++;
                    }
                }
            });
            EConfig.MESSAGES.saveConfig();
            Vars.sendSystemMessage(TypeMessage.SUCCESS, EMessage.values().length + " messages was loaded!");
            if (errors != 0) Vars.sendSystemMessage(TypeMessage.INFO, errors + " NEW messages was loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading messages!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка меню магазина
    private static void loadShopItems() {
        try {
            Main.value_shopitems = EConfig.CONFIG.getConfig().getInt("values.shop_items");
            new ShopMenu(null);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading shop of items!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Запуск счётчика времени проведённого на сервере
    private void startTimer() {
        try {
            Bukkit.getServer().getScheduler().runTaskTimer(getInstance(), () -> {
                if (Utils.getPlayers().isEmpty()) return;
                Utils.getPlayers().forEach(s -> {
                    Gamer gamer = gamers.get(Bukkit.getPlayer(s).getUniqueId());
                    int get = (int) gamer.getStatistics(EStat.PLAYEDTIME) + 1;
                    gamer.setStatistics(EStat.PLAYEDTIME, get);
                    if (get == 30) Achievement.TIME_30.get(gamer.getPlayer(), false);
                    if (get == 90) Achievement.TIME_90.get(gamer.getPlayer(), false);
                    if (get == 240) Achievement.TIME_4H.get(gamer.getPlayer(), false);
                    if (get == 600) Achievement.TIME_10H.get(gamer.getPlayer(), false);
                });
            }, 100, 1200);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in start timer!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка топов игроков
    private void loadTops() {
        try {
            long reload_time = EConfig.CONFIG.getConfig().getInt("reload_top") * 1200;
            HashMap<String, Long> money = new HashMap<>();
            HashMap<String, Long> blocks = new HashMap<>();
            HashMap<String, Long> level = new HashMap<>();
            HashMap<String, Long> rats = new HashMap<>();
            HashMap<String, Long> rebirth = new HashMap<>();
            HashMap<String, Long> keys = new HashMap<>();
            for (String name : EConfig.STATISTICS.getConfig().getKeys(false)) {
                if (EStat.MONEY.getFromConfig(name) instanceof Integer) {
                    money.put(name, (long) (int) EStat.MONEY.getFromConfig(name));
                } else {
                    money.put(name, Math.round((double)EStat.MONEY.getFromConfig(name)));
                }
                if (EStat.BLOCKS.getFromConfig(name) instanceof Integer) {
                    blocks.put(name, (long) (int)EStat.BLOCKS.getFromConfig(name));
                } else {
                    blocks.put(name, Math.round((double)EStat.BLOCKS.getFromConfig(name)));
                }
                level.put(name, (long) (int)EStat.LEVEL.getFromConfig(name));
                rats.put(name, (long) (int)EStat.RATS.getFromConfig(name));
                rebirth.put(name, (long) (int)EStat.REBIRTH.getFromConfig(name));
                keys.put(name, (long) (int)EStat.KEYS.getFromConfig(name));
            }
            tops.put("деньги", new TopPlayers(Utils.getLocation("moneytop"), money, "&7Топ игроков по деньгам", 10));
            tops.put("блоки", new TopPlayers(Utils.getLocation("blockstop"), blocks, "&7Топ игроков по блокам", 10));
            tops.put("уровни", new TopPlayers(Utils.getLocation("levelstop"), level, "&7Топ игроков по уровням", 10));
            tops.put("крысы", new TopPlayers(Utils.getLocation("ratstop"), rats, "&7Топ игроков по крысам", 10));
            tops.put("перерождения", new TopPlayers(Utils.getLocation("rebirthtop"), rebirth, "&7Топ игроков по перерождениям", 10));
            tops.put("ключи", new TopPlayers(Utils.getLocation("keystop"), keys, "&7Топ игроков по ключам", 10));

            Bukkit.getScheduler().runTaskTimer(this, () -> {
                EConfig.STATISTICS.saveConfig();
                HashMap<String, Long> money1 = new HashMap<>();
                HashMap<String, Long> blocks1 = new HashMap<>();
                HashMap<String, Long> level1 = new HashMap<>();
                HashMap<String, Long> rats1 = new HashMap<>();
                HashMap<String, Long> rebirth1 = new HashMap<>();
                HashMap<String, Long> keys1 = new HashMap<>();

                for (String name : EConfig.STATISTICS.getConfig().getKeys(false)) {
                    if (EStat.MONEY.getFromConfig(name) instanceof Integer) {
                        money1.put(name, (long) (int)EStat.MONEY.getFromConfig(name));
                    } else {
                        money1.put(name, Math.round((double)EStat.MONEY.getFromConfig(name)));
                    }
                    if (EStat.BLOCKS.getFromConfig(name) instanceof Integer) {
                        blocks1.put(name, (long) (int)EStat.BLOCKS.getFromConfig(name));
                    } else {
                        blocks1.put(name, Math.round((double)EStat.BLOCKS.getFromConfig(name)));
                    }
                    level1.put(name, (long) (int)EStat.LEVEL.getFromConfig(name));
                    rats1.put(name, (long) (int)EStat.RATS.getFromConfig(name));
                    rebirth1.put(name, (long) (int)EStat.REBIRTH.getFromConfig(name));
                    keys1.put(name, (long) (int)EStat.KEYS.getFromConfig(name));
                }
                if (!Main.tops.keySet().iterator().hasNext()) return;
                tops.keySet().forEach(s -> {
                    if ("деньги".equals(s)) {
                        Main.tops.get(s).setTopValues(money1);
                    } else if ("блоки".equals(s)) {
                        Main.tops.get(s).setTopValues(blocks1);
                    } else if ("уровни".equals(s)) {
                        Main.tops.get(s).setTopValues(level1);
                    } else if ("крысы".equals(s)) {
                        Main.tops.get(s).setTopValues(rats1);
                    } else if ("перерождения".equals(s)) {
                        Main.tops.get(s).setTopValues(rebirth1);
                    } else if ("ключи".equals(s)) {
                        Main.tops.get(s).setTopValues(keys1);
                    }
                    Main.tops.get(s).recreate();
                });
            }, 100L, reload_time);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading players top!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка шахт
    private void loadMines() {
        try {
            Main.value_mines = EConfig.CONFIG.getConfig().getInt("values.mines");
            EConfig.CONFIG.getConfig().getConfigurationSection("mines").getKeys(false).forEach(s -> {
                ConfigurationSection file = EConfig.CONFIG.getFileConfigurationConfig().getConfigurationSection("mines." + s);
                Material surface = Material.AIR;
                if (!file.getString("surface").equals("none")) surface = Material.valueOf(file.getString("surface"));
                Mine mine = new Mine(file.getString("name"),
                        file.getInt("min_level"),
                        file.getBoolean("pvp"),
                        file.getInt("h"),
                        file.getInt("diametr"),
                        Bukkit.getWorld(file.getString("world")),
                        file.getInt("x"),
                        file.getInt("y"),
                        file.getInt("z"),
                        file.getInt("time"),
                        file.getString("types"),
                        Material.getMaterial(file.getString("material")),
                        file.getInt("tpx"),
                        file.getInt("tpy"),
                        file.getInt("tpz"),
                        Utils.unserializeLocation(file.getString("holo")),
                        file.getBoolean("tpSpawn"),
                        surface);
                mines.add(mine);

                Mine.updateMine(mine);
            });
            Vars.sendSystemMessage(TypeMessage.SUCCESS, value_mines + " mines was loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading mines!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка шахт
    private void loadDonates() {
        try {
            Main.value_donate = EConfig.CONFIG.getConfig().getInt("values.donate");
            for (int i = 0; i < value_donate; i++) {
                ConfigurationSection file = EConfig.DONATE.getFileConfigurationConfig().getConfigurationSection("donate." + (i + 1));
                Donate n = new Donate(file.getString("name"), Material.valueOf(file.getString("icon").toUpperCase()), file.getInt("amount"), file.getInt("price"), file.getBoolean("temporary"), new Lore.BuilderLore().addList(file.getStringList("lore")).build(), file.getInt("slot"));
                Utils.donate.add(n);
                Donate.icons.put(n, (DonateIcon) new DonateIcon.Builder(n).build());
            }
            Vars.sendSystemMessage(TypeMessage.SUCCESS, value_donate + " donate items was loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading donate items!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка тренера
    private void loadTrainer() {
        try {
            EConfig.TRAINER.getConfig().getKeys(false).forEach(s -> {
                ConfigurationSection section = EConfig.TRAINER.getConfig().getConfigurationSection(s);
                Utils.trainer.add(new Trainer(s.toUpperCase(), section.getString("name"), Material.valueOf(section.getString("icon")), new Lore.BuilderLore().addList(section.getStringList("description")).build(), section.getStringList("values")));
            });
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading trainer!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Удаление мобов
    private static void removeEntities() {
        try {
            for (Spawner cSpawner : Spawner.spawners.values()) {
                if (cSpawner.getCurrent() != null) {
                    cSpawner.getCurrent().getBukkitEntity().remove();
                    cSpawner.dead();
                }
            }
            Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));
            Vars.sendSystemMessage(TypeMessage.INFO, "Entities removed");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in delete entities! Please, don`t use /reload. Use /stop or /restart");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            //Main.getInstance().setStatus(ServerStatus.ERROR);
            //ex.printStackTrace();
        }
    }

    public void setStatus(ServerStatus serverStatus) {
        status = serverStatus;
    }

    public ServerStatus getStatus() {
        return status;
    }
}
