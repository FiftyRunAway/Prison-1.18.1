package org.runaway;

import com.boydti.fawe.util.EditSessionBuilder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.patterns.Pattern;
import lombok.Getter;
import lombok.Setter;
import me.bigteddy98.bannerboard.api.BannerBoardAPI;
import me.bigteddy98.bannerboard.api.BannerBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.achievements.Achievement;
import org.runaway.auctions.TrashAuction;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.missions.*;
import org.runaway.board.Board;
import org.runaway.boosters.GBlocks;
import org.runaway.boosters.GMoney;
import org.runaway.boosters.LBlocks;
import org.runaway.boosters.LMoney;
import org.runaway.cases.Case;
import org.runaway.commands.*;
import org.runaway.configs.Config;
import org.runaway.donate.Donate;
import org.runaway.donate.DonateIcon;
import org.runaway.donate.Privs;
import org.runaway.entity.Spawner;
import org.runaway.enums.*;
import org.runaway.events.*;
import org.runaway.inventories.*;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.menu.MenuListener;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mine;
import org.runaway.mines.Mines;
import org.runaway.needs.Needs;
import org.runaway.quests.MinesQuest;
import org.runaway.sqlite.Database;
import org.runaway.sqlite.PreparedRequests;
import org.runaway.sqlite.SQLite;
import org.runaway.tasks.AsyncRepeatTask;
import org.runaway.tasks.AsyncTask;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.trainer.Trainer;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class Prison extends JavaPlugin {

    public static HashMap<UUID, Gamer> gamers = new HashMap<>();

    private static Prison instance;
    public static Prison getInstance() {
        return instance;
    }

    //SQLite
    private Map<String, Database> databases = new HashMap<>();
    public final String stat_table = "Statistics";
    private SaveType type_saving;
    private PreparedRequests preparedRequests;

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
    public static boolean useBannerBoard;
    public static boolean useTelegramBots;

    public static TrashAuction trashAuction;

    //Telegram
    public String bot_username;
    public String bot_token;

    //Событие
    public static String event = null;

    //Значения
    public static int value_mines;
    public static int value_shopitems;
    public static int value_donate;

    //Локации
    public static Location SPAWN;

    //Топы
    public static HashMap<String, TopPlayers> tops = new HashMap<>();

    // кэш /tip`a
    public static ArrayList<String> THXersBlocks = new ArrayList<>();
    public static ArrayList<String> THXersMoney = new ArrayList<>();

    // Список норм боссов
    public static ArrayList<UUID> bosses = new ArrayList<>();

    //Менеджер предметов
    @Getter
    private ItemManager itemManager;
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
        saveBoosters();
        removeEntities();

        Vars.sendSystemMessage(TypeMessage.SUCCESS, "Plugin was successful disabled!");
    }

    private void loader() {
        new Config().loadConfigs();
        FileConfiguration loader = EConfig.MODULES.getConfig();
        loadSQLite();
        loadTasks();
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
        if (loader.getBoolean("loader.telegram")) loadTelegramBotsAPI();
        if (loader.getBoolean("loader.viaversion")) loadViaVersion();

        if (loader.getBoolean("loader.battlepass")) BattlePass.load();

        if (loader.getBoolean("register.mobs")) registerMobs();

        if (loader.getBoolean("loader.server_status")) loadServerStatus();
        SPAWN = Utils.getLocation("spawn");
        Bukkit.getServer().getWorlds().forEach(world -> world.setGameRuleValue("announceAdvancements", "false"));
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    PacketContainer packet = event.getPacket();
                    EnumWrappers.EntityUseAction entityUseAction = packet.getEntityUseActions().read(0);

                }
            }
        });
        new AsyncRepeatTask(() -> {
            gamers.values().forEach(Gamer::savePlayer);
        }, 20 * 60 * 20, 20 * 60 * 20);
        loadBoosters();
        this.itemManager = new ItemManager();
        ParameterManager parameterManager = getItemManager().getParameterManager();
        if(true) {
            //EXAMPLE
            PrisonItem prisonItem = PrisonItem.builder()
                    .vanillaName("pick") //тех. название предмета
                    .itemLevel(1)//уровень предмета
                    .minLevel(3)//мин лвл для использования предмета
                    .vanillaItem(new Item.Builder(Material.DIAMOND_PICKAXE) //билд предмета
                            .lore(new Lore.BuilderLore().addString("&atest").build()).build().item()) //билд предмета
                    .parameters(Arrays.asList( //параметры
                            parameterManager.getNodropParameter(), //предмет не выпадает
                            parameterManager.getOwnerParameter(), //предмет с владельцем
                            parameterManager.getRareParameter(PrisonItem.Rare.DEFAULT), //редкость предмета
                            parameterManager.getCategoryParameter(PrisonItem.Category.TOOLS), //категория предмета
                            parameterManager.getRunesParameter(1), //кол-во рун (дефолтные руны как 2 параметр, если есть.
                            parameterManager.getUpgradableParameter())).build(); //предмет можно улучшить
            getItemManager().addPrisonItem(prisonItem); //инициализация предмета
        }
    }

    private void loadSQLite() {
        type_saving = SaveType.SQLITE;

        //Statistics table
        initializeDatabase(stat_table, "player", EStat.values());
        Gamer.preparedRequests = getPreparedRequests();
    }

    public static Database getMainDatabase() {
        return getInstance().getDatabase(instance.stat_table);
    }


    public PreparedRequests getPreparedRequests() {
        return preparedRequests;
    }

    /**
     *
     * @param databaseName
     *            name
     */
    public void initializeDatabase(String databaseName, String primaryKey, Saveable[] saveables) {
        Database db = new SQLite(databaseName, Config.standartFile);
        db.load(primaryKey, saveables);
        databases.put(databaseName, db);
        this.preparedRequests = new PreparedRequests(db);
    }

    /**
     *
     * @param databaseName
     *            name
     * @param createStatement
     *            statement once the database is created. Usually used to create
     *            tables.
     *
     *            Sets the string sent to player when an item cannot be purchased.
     * @param plugin to create database file inside.
     */
    /**
     * Get the global list of currently loaded databased.
     * <p>
     *
     * @return the {@link Prison}'s global database list.
     */
    public Map<String, Database> getDatabases() {
        return databases;
    }

    /**
     *
     * @param databaseName
     *            name
     *
     *            Gets a specific {@link Database}'s class.
     */
    public Database getDatabase(String databaseName) {
        return getDatabases().get(databaseName);
    }

    public SaveType getSaveType() {
        return type_saving;
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
            new Utils().RegisterEvent(new BossSpawn());
            new Utils().RegisterEvent(new PlayerFishing());

            //new Utils().RegisterEvent(new TWOFA());
            new Utils().RegisterEvent(new Needs());
            new Utils().RegisterEvent(new FishCatch());

            // Missions events
            new Utils().RegisterEvent(new KeyFarm());
            new Utils().RegisterEvent(new WoodFarm());
            new Utils().RegisterEvent(new BlocksFarm());
            new Utils().RegisterEvent(new FishFarm());
            new Utils().RegisterEvent(new KillsFarm());
            new Utils().RegisterEvent(new TreasureFarm());
            new Utils().RegisterEvent(new DamageFarm());
            new Utils().RegisterEvent(new RatsFarm());
            new Utils().RegisterEvent(new TrainerFarm());
            new Utils().RegisterEvent(new UpgradesFarm());

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering events!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Рег. тасков
    private void loadTasks() {
        AsyncTask.setJavaPlugin(this);
        AsyncRepeatTask.setJavaPlugin(this);
        SyncTask.setJavaPlugin(this);
        SyncRepeatTask.setJavaPlugin(this);
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
                    new BaseCommand(), new TrashCommand(), new QuestCommand(), new FisherCommand(),
                    new JobCommand(), new MsgCommand(), new ReplyCommand(), new InvseeCommand(), new ItemCommand()).forEach(CommandManager::register);

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering commands!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Регистрация мобов
    private static void registerMobs() {
        try {
            Mobs.registerMobs();
            Spawner.SpawnerUtils.init();
            new Spawner.SpawnerUpdater().runTaskTimer(getInstance(), 20L, 600L);

            //removeEntities();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering mobs!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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
                    Hologram hologram = HologramsAPI.createHologram(Prison.getInstance(), c.getLocation().getBlock().getLocation().add(0.5, 2, 0.5));
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
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка инвентарей
    private void loadInventories() {
        try {
            new BlockShopMenu(null).load();
            new ShopMenu(null).load();
            Confirmation.load();
            RebirthMenu.load();
            MinesQuest.load();
            Privs.loadIcons();
            PassivePerksMenu.load();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in creating inventories!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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

    //Привязка TelegramBotsAPI
    private void loadTelegramBotsAPI() {
        useTelegramBots = Bukkit.getPluginManager().isPluginEnabled("TelegramBotsAPIPlugin");
        if (useTelegramBots) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "TelegramBotsAPI was successfully connected");
            this.bot_username = EConfig.CONFIG.getConfig().getString("telegram.username");
            this.bot_token = EConfig.CONFIG.getConfig().getString("telegram.token");
            /*ApiContextInitializer.init();
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi();
                botsApi.registerBot(new TelegramBot());
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "TelegramBotsAPI Displays has not been installed yet");
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
                Prison.getInstance().setStatus(ServerStatus.valueOf(EConfig.CONFIG.getConfig().getString("status").toUpperCase()));
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
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка меню магазина
    private static void loadShopItems() {
        try {
            Prison.value_shopitems = EConfig.CONFIG.getConfig().getInt("values.shop_items");
            new ShopMenu(null);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading shop of items!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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
                    int get = gamer.getIntStatistics(EStat.PLAYEDTIME) + 1;
                    gamer.setStatistics(EStat.PLAYEDTIME, get);
                    if (get == 30) Achievement.TIME_30.get(gamer.getPlayer());
                    if (get == 90) Achievement.TIME_90.get(gamer.getPlayer());
                    if (get == 240) Achievement.TIME_4H.get(gamer.getPlayer());
                    if (get == 600) Achievement.TIME_10H.get(gamer.getPlayer());
                });
            }, 100, 1200);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in start timer!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка топов игроков
    private void loadTops() {
        try {
            useBannerBoard = Bukkit.getPluginManager().isPluginEnabled("BannerBoard");
            if (useBannerBoard) {
                Vars.sendSystemMessage(TypeMessage.SUCCESS, "BannerBoard was successfully connected");
            } else {
                Vars.sendSystemMessage(TypeMessage.INFO, "BannerBoard has not been installed yet");
                return;
            }

            Map<String, Long> money = new HashMap<>();
            Map<String, Long> blocks = new HashMap<>();
            Map<String, Long> level = new HashMap<>();
            Map<String, Long> rats = new HashMap<>();
            Map<String, Long> rebirth = new HashMap<>();
            Map<String, Long> keys = new HashMap<>();
            Map<String, Long> dm = new HashMap<>();
            money = getPreparedRequests().getTop(EStat.MONEY.getColumnName(), 10);
            blocks = getPreparedRequests().getTop(EStat.BLOCKS.getColumnName(), 10);
            level = getPreparedRequests().getTop(EStat.LEVEL.getColumnName(), 10);
            rats = getPreparedRequests().getTop(EStat.BOSSES.getColumnName(), 10);
            rebirth = getPreparedRequests().getTop(EStat.REBIRTH.getColumnName(), 10);
            keys = getPreparedRequests().getTop(EStat.KEYS.getColumnName(), 10);
            dm = getPreparedRequests().getTop(EStat.STREAMS.getColumnName(), 10);
            tops.put("money", new TopPlayers(Utils.getLocation("moneytop"), money, "&7Топ игроков по деньгам", 10,  MoneyType.RUBLES.getShortName()));
            tops.put("blocks", new TopPlayers(Utils.getLocation("blockstop"), blocks, "&7Топ игроков по блокам", 10, "блоков"));
            tops.put("levels", new TopPlayers(Utils.getLocation("levelstop"), level, "&7Топ игроков по уровням", 10, "уровень"));
            tops.put("rats", new TopPlayers(Utils.getLocation("ratstop"), rats, "&7Топ игроков по крысам", 10, "крыс"));
            tops.put("rebirths", new TopPlayers(Utils.getLocation("rebirthtop"), rebirth, "&7Топ игроков по перерождениям", 10, "перерождений"));
            tops.put("keys", new TopPlayers(Utils.getLocation("keystop"), keys, "&7Топ игроков по ключам", 10, "ключей"));
            tops.put("donate", new TopPlayers(Utils.getLocation("keystop"), dm, "&7Топ игроков по донату", 10, "рублей"));
            BannerBoardAPI api = BannerBoardManager.getAPI();
            api.registerCustomRenderer("prison_leaders", this, false, TopsBanner.class);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading players top!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public void forceUpdateTop() {

        Map<String, Long> money1 = getPreparedRequests().getTop(EStat.MONEY.getColumnName(), 10);
        Map<String, Long> blocks1 = getPreparedRequests().getTop(EStat.BLOCKS.getColumnName(), 10);
        Map<String, Long> level1 = getPreparedRequests().getTop(EStat.LEVEL.getColumnName(), 10);
        Map<String, Long> rats1 = getPreparedRequests().getTop(EStat.BOSSES.getColumnName(), 10);
        Map<String, Long> rebirth1 = getPreparedRequests().getTop(EStat.REBIRTH.getColumnName(), 10);
        Map<String, Long> keys1 = getPreparedRequests().getTop(EStat.KEYS.getColumnName(), 10);
        Map<String, Long> dm1 = getPreparedRequests().getTop(EStat.STREAMS.getColumnName(), 10);
        if (!Prison.tops.keySet().iterator().hasNext()) return;
        tops.keySet().forEach(s -> {
            if ("money".equals(s)) {
                Prison.tops.get(s).setTopValues(money1);
            } else if ("blocks".equals(s)) {
                Prison.tops.get(s).setTopValues(blocks1);
            } else if ("levels".equals(s)) {
                Prison.tops.get(s).setTopValues(level1);
            } else if ("rats".equals(s)) {
                Prison.tops.get(s).setTopValues(rats1);
            } else if ("rebirths".equals(s)) {
                //Prison.tops.get(s).setTopValues(rebirth1);
            } else if ("keys".equals(s)) {
                Prison.tops.get(s).setTopValues(keys1);
            } else if ("donate".equals(s)) {
                Prison.tops.get(s).setTopValues(dm1);
            }
        });
    }

    //Подгрузка шахт
    private void loadMines() {
        try {
            Prison.value_mines = EConfig.CONFIG.getConfig().getInt("values.mines");
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
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Отгрузка бустеров
    private void saveBoosters() {
        //Глобальные бустеры
        if (Prison.gBlocks.isActive()) {
            Utils.globalBoostersSetter("blocks", Prison.gBlocks);
        }
        if (Prison.gMoney.isActive()) {
            Utils.globalBoostersSetter("money", Prison.gMoney);
        }
        //Локальные бустеры

        //Блоков
        Utils.getlBlocksMultiplier().forEach((player_name, mult) -> {
            String name = player_name;
            AtomicLong time = new AtomicLong();
            Utils.getlBlocksRealTime().forEach((p, times) -> {
                if (p.equals(name)) {
                    time.set(times);
                }
            });
            AtomicLong start = new AtomicLong();
            Utils.getlBlocksActivatingTime().forEach((p, times) -> {
                if (p.equals(name)) {
                    start.set(times);
                }
            });
            Utils.localBoostersSetter(name, "blocks", time.get(), mult, start.get());
        });

        //Денег
        Utils.getlMoneyMultiplier().forEach((player_name, mult) -> {
            String name = player_name;
            AtomicLong time = new AtomicLong();
            Utils.getlMoneyRealTime().forEach((p, times) -> {
                if (p.equals(name)) {
                    time.set(times);
                }
            });
            AtomicLong start = new AtomicLong();
            Utils.getlMoneyActivatingTime().forEach((p, times) -> {
                if (p.equals(name)) {
                    start.set(times);
                }
            });
            Utils.localBoostersSetter(name, "money", time.get(), mult, start.get());
        });

        //Текщее время
        EConfig.LOG.getConfig().set("time", System.currentTimeMillis());
        EConfig.LOG.saveConfig();
    }

    //Загрузка бустеров
    private void loadBoosters() {
        FileConfiguration cfg = EConfig.LOG.getConfig();
        //Глобальные
        if (cfg.getBoolean("global.blocks.activity")) {
            ConfigurationSection section = cfg.getConfigurationSection("global.blocks.information");
            Prison.gBlocks.start(section.getString("owner"), section.getLong("time"), section.getDouble("multiplier"));
            cfg.set("global.blocks.activity", false);
            Bukkit.getConsoleSender().sendMessage(Vars.getPrefix() + "Started Global BLOCKS booster from " + section.getString("owner"));
        }
        if (cfg.getBoolean("global.money.activity")) {
            ConfigurationSection section = cfg.getConfigurationSection("global.money.information");
            Prison.gMoney.start(section.getString("owner"), section.getLong("time"), section.getDouble("multiplier"));
            cfg.set("global.money.activity", false);
            Bukkit.getConsoleSender().sendMessage(Vars.getPrefix() + "Started Global MONEY booster from " + section.getString("owner"));
        }

        //Локальные
        if (cfg.getConfigurationSection("local") != null) {
            cfg.getConfigurationSection("local").getKeys(true).forEach(s -> {
                ConfigurationSection section = cfg.getConfigurationSection("local." + s);
                if (cfg.contains("local." + s + ".blocks")) {
                    LBlocks blocks = new LBlocks();
                    long working_time = Math.round((double) (cfg.getLong("time") - section.getLong("blocks.activated")) / 1000);
                    blocks.start(s, section.getLong("blocks.time") - working_time, section.getDouble("blocks.multiplier"));
                    Bukkit.getConsoleSender().sendMessage(Vars.getPrefix() + "Started Local Blocks booster for " + s);
                }
                if (cfg.contains("local." + s + ".money")) {
                    LMoney money = new LMoney();
                    long gone_before_stopping = Math.round((double) (cfg.getLong("time") - section.getLong("money.activated")) / 1000);
                    money.start(s, section.getLong("money.time") - gone_before_stopping, section.getDouble("money.multiplier"));
                    Bukkit.getConsoleSender().sendMessage(Vars.getPrefix() + "Started Local Money booster for " + s);
                }
            });
        }
        cfg.set("local", null);
        EConfig.LOG.saveConfig();
    }

    //Подгрузка шахт
    private void loadDonates() {
        try {
            Prison.value_donate = EConfig.CONFIG.getConfig().getInt("values.donate");
            for (int i = 0; i < value_donate; i++) {
                ConfigurationSection file = EConfig.DONATE.getFileConfigurationConfig().getConfigurationSection("donate." + (i + 1));
                Donate n = new Donate(file.getString("name"), Material.valueOf(file.getString("icon").toUpperCase()), file.getInt("amount"), file.getInt("price"), file.getBoolean("temporary"), new Lore.BuilderLore().addList(file.getStringList("lore")).build(), file.getInt("slot"), file.getInt("sale"));
                Utils.donate.add(n);
                Donate.icons.put(n, (DonateIcon) new DonateIcon.Builder(n).build());
            }
            Vars.sendSystemMessage(TypeMessage.SUCCESS, value_donate + " donate items was loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading donate items!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Удаление мобов
    private static void removeEntities() {
        try {
            for (World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (!(e instanceof ArmorStand) &&
                            !(e instanceof org.bukkit.entity.Item) &&
                            !(e instanceof Player) &&
                            !(e instanceof ItemFrame)) {
                        e.setCustomName("toDelete");
                        e.remove();
                    }
                }
            }
            Vars.sendSystemMessage(TypeMessage.INFO, "Entities removed");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in delete entities! Please, don`t use /reload. Use /stop or /restart");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            //Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public void setStatus(ServerStatus serverStatus) {
        status = serverStatus;
    }

    public ServerStatus getStatus() {
        return status;
    }
}
