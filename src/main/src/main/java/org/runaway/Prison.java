package org.runaway;

import lombok.Getter;
import me.bigteddy98.bannerboard.api.BannerBoardAPI;
import me.bigteddy98.bannerboard.api.BannerBoardManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
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
import org.runaway.cases.CaseManager;
import org.runaway.commands.*;
import org.runaway.configs.Config;
import org.runaway.donate.Donate;
import org.runaway.donate.DonateIcon;
import org.runaway.donate.DonateStat;
import org.runaway.donate.Privs;
import org.runaway.entity.MobManager;
import org.runaway.enums.*;
import org.runaway.events.*;
import org.runaway.fishing.EFish;
import org.runaway.inventories.*;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.Parameter;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.MenuListener;
import org.runaway.mines.Mine;
import org.runaway.mines.Mines;
import org.runaway.needs.Needs;
import org.runaway.passiveperks.EPassivePerk;
import org.runaway.quests.MinesQuest;
import org.runaway.requirements.*;
import org.runaway.runes.utils.RuneManager;
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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class Prison extends JavaPlugin {

    public final boolean ranksImages = true;

    private static Prison instance;
    public static Prison getInstance() {
        return instance;
    }

    public static boolean isDisabling;
    public static List<String> stats;

    //SQLite
    private Map<String, Database> databases = new HashMap<>();
    public final String stat_table = "Statistics";
    public final String donateTable = "Donate";
    private SaveType type_saving;
    private PreparedRequests preparedRequests;
    private PreparedRequests donateRequests;

    private ServerStatus status = null;

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
    public static boolean useItemsAdder;

    public List<String> fish_food = new ArrayList<>();

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
    public static Map<String, TopPlayers> tops = new HashMap<>();

    // кэш /tip`a
    public static ArrayList<String> THXersBlocks = new ArrayList<>();
    public static ArrayList<String> THXersMoney = new ArrayList<>();

    // Список шахт
    public static ArrayList<Mine> mines = new ArrayList<>();

    @Getter
    public final Properties keys = new Properties();

    @Getter
    public final HashMap<String, String> localization = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        loader();
    }

    public void onDisable() {
        if (getStatus().equals(ServerStatus.ERROR)) return;
        isDisabling = true;
        Utils.DisableKick();
        removeEntities();
        new Config().unloadConfigs();
        TrashAuction.closeAll();
        saveBoosters();
        Vars.sendSystemMessage(TypeMessage.SUCCESS, "Plugin was successfully disabled!");
    }

    private void loader() {
        new Config().loadConfigs();
        String language = "ru_ru";
        this.downloadAndApplyLanguage(language);
        FileConfiguration loader = EConfig.MODULES.getConfig();
        loadTasks();
        loadSQLite();
        if (loader.getBoolean("register.events")) registerEvents();
        if (loader.getBoolean("register.commands")) registerCommands();
        ParameterManager.init();
        loadItems();
        if (loader.getBoolean("loader.messages")) loadMessage();
        if (loader.getBoolean("loader.hd")) loadHolographicDisplays();
        if (loader.getBoolean("loader.bossbar")) loadBar();
        if (loader.getBoolean("loader.tops")) loadTops();
        if (loader.getBoolean("loader.board")) Board.loadBoard();
        if (loader.getBoolean("loader.block_list")) BlockBreak.loadLogger();
        if (loader.getBoolean("loader.block_shop")) PlayerInteract.loadShop();
        if (loader.getBoolean("loader.auto_restart")) AutoRestart.loadAutoRestarter();
        if (loader.getBoolean("loader.mines")) loadMines();

        CaseManager.initAllKeys();
        if (loader.getBoolean("register.mobs")) registerMobs();
        if (loader.getBoolean("loader.mines_menu")) Mines.loadMinesMenu();
        CaseManager.initAllCases();

        if (loader.getBoolean("loader.shop_items")) loadShopItems();
        if (loader.getBoolean("loader.achievements")) Achievement.load();
        if (loader.getBoolean("loader.donate_menu")) loadDonates();
        if (loader.getBoolean("loader.pex")) loadPermissionsEx();
        if (loader.getBoolean("loader.trainer")) loadTrainer();
        if (loader.getBoolean("loader.timer")) startTimer();
        if (loader.getBoolean("loader.nametag")) loadNametagEdit();
        if (loader.getBoolean("loader.telegram")) loadTelegramBotsAPI();
        if (loader.getBoolean("loader.viaversion")) loadViaVersion();
        loadItemsAdder();

        if (loader.getBoolean("loader.server_status")) loadServerStatus();

        if (loader.getBoolean("loader.battlepass")) BattlePass.load();

        //CustomHUD.load();

        SPAWN = Utils.getLocation("spawn");
        Arrays.stream(EFish.values()).forEach(fish -> fish_food.add(ChatColor.stripColor(fish.getFish().getName())));
        Bukkit.getServer().getWorlds().forEach(world -> {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        });

        /*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.MONITOR, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    PacketContainer packet = event.getPacket();
                    EnumWrappers.EntityUseAction entityUseAction = packet.getEntityUseActions().read(0);

                }
            }
        });*/
        new AsyncRepeatTask(() -> {
            GamerManager.gamers.values().forEach(Gamer::savePlayer);
        }, 20 * 60 * 20, 20 * 60 * 20);
        loadBoosters();

        if(false) {
            //EXAMPLE
            PrisonItem prisonItem = PrisonItem.builder()
                    .vanillaName("pick") //тех. название предмета
                    .itemLevel(1)//уровень предмета
                    .vanillaItem(new org.runaway.items.Item.Builder(Material.DIAMOND_PICKAXE) //билд предмета
                            .name("&dАлмазная кирка")
                            //.lore(new Lore.BuilderLore().addString("&atest").build())
                            .build().item()) //билд предмета
                    .nextPrisonItem("pick_2") //тех. название след. предмета
                    .upgradeRequireList(new RequireList(MoneyRequire.builder().amount(10000).build(),
                            BlocksRequire.builder().localizedBlock(new LocalizedBlock(Material.DIRT)).amount(256).build())) //что нужно для апгрейда
                    .parameters(Arrays.asList( //параметры
                            ParameterManager.getNodropParameter(), //предмет не выпадает
                            ParameterManager.getOwnerParameter(), //предмет с владельцем
                            ParameterManager.getMinLevelParameter(3), //мин лвл для использования предмета
                            //ParameterManager.getStattrakBlocksParameter(), //статтрек блоков
                            ParameterManager.getRareParameter(PrisonItem.Rare.DEFAULT), //редкость предмета
                            ParameterManager.getCategoryParameter(PrisonItem.Category.TOOLS), //категория предмета
                            ParameterManager.getRunesParameter(1), //кол-во рун (дефолтные руны как 2 параметр, если есть.
                            ParameterManager.getUpgradableParameter())).build(); //предмет можно улучшить
            ItemManager.addPrisonItem(prisonItem); //инициализация предмета

            PrisonItem prisonItem2 = PrisonItem.builder()
                    .vanillaName("pick") //тех. название предмета
                    .itemLevel(2)//уровень предмета
                    .vanillaItem(new org.runaway.items.Item.Builder(Material.DIAMOND_PICKAXE) //билд предмета
                            .name("&dАлмазная кирка")
                            //.lore(new Lore.BuilderLore().addString("&atest").build())
                            .build().item()) //билд предмета
                    .parameters(Arrays.asList( //параметры
                            ParameterManager.getNodropParameter(), //предмет не выпадает
                            ParameterManager.getOwnerParameter(), //предмет с владельцем
                            ParameterManager.getMinLevelParameter(4), //мин лвл для использования предмета
                            //ParameterManager.getStattrakBlocksParameter(), //статтрек блоков
                            ParameterManager.getRareParameter(PrisonItem.Rare.DEFAULT), //редкость предмета
                            ParameterManager.getCategoryParameter(PrisonItem.Category.TOOLS), //категория предмета
                            ParameterManager.getRunesParameter(2), //кол-во рун (дефолтные руны как 2 параметр, если есть.
                            ParameterManager.getUpgradableParameter())).build(); //предмет можно улучшить
            ItemManager.addPrisonItem(prisonItem2); //инициализация предмета
        }
        ItemManager.getPrisonItem("menu").setConsumerOnClick(gamer ->
                new MainMenu(gamer.getPlayer()));

        LevelMenu.passiveLevels = new ArrayList<>();
        LevelMenu.passiveLevels.addAll(Stream.of(EPassivePerk.values())
                .map(p -> p.getPerk().getLevel()).sorted().distinct().toList());
    }

    public void setAutoRestart() {
        isAutoRestart = true;
    }

    private void downloadAndApplyLanguage(String lang) {
        File file = FileUtils.getFile(this.getDataFolder().toString(), "lang", lang + ".lang");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                new ResourceDownloader().downloadResource(lang, file);
                this.loadLanguage(file);
            } catch (IOException | IllegalArgumentException ex) {
                this.keys.clear();
                ex.printStackTrace();
                return;
            }
        }
        this.loadLanguage(file);
    }

    private void loadLanguage(File file) {
        Charset charset = StandardCharsets.UTF_8;
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader is = new InputStreamReader(fis, charset)) {
            this.keys.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
            this.keys.clear();
        }
    }

    private void loadItems() {
        RuneManager.init();

        EConfig.ITEMS.getConfig().getKeys(false).forEach(s -> {
            PrisonItem.Category category = s.equals("none") ? null : PrisonItem.Category.valueOf(s.toUpperCase());
            EConfig.ITEMS.getConfig().getConfigurationSection(s).getKeys(false).forEach(item -> {
                ConfigurationSection section = EConfig.ITEMS.getConfig().getConfigurationSection(s + "." + item);
                List<String> parametrss = null;
                if (section.contains("parameters")) {
                    parametrss = Arrays.asList(section.getString("parameters")
                            .replace("{", "")
                            .replace("}", "").split(";"));
                }
                String[] type = section.getString("type").split(":");
                int data = 0;
                Material material;
                try {
                    if (type.length > 1) {
                        material = Material.valueOf(type[0].toUpperCase());
                        data = Integer.parseInt(type[1]);
                    } else {
                        material = Material.valueOf(section.getString("type"));
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unknown material type - " + section.getString("type"));
                }
                List<Parameter> parameters = getParameters(parametrss);
                if (category != null) parameters.add(ParameterManager.getCategoryParameter(category));

                if (material.name().startsWith("WOODEN") ||
                        material.name().startsWith("LEATHER")) {
                    parameters.add(ParameterManager.getRunesParameter(1));
                } else if (material.name().startsWith("STONE") ||
                        material.name().startsWith("CHAINMAIL")) {
                    parameters.add(ParameterManager.getRunesParameter(2));
                } else if (material.name().startsWith("IRON")) {
                    parameters.add(ParameterManager.getRunesParameter(3));
                } else if (material.name().startsWith("DIAMOND")) {
                    parameters.add(ParameterManager.getRunesParameter(4));
                }
                PrisonItem.Rare rare = null;
                if (section.contains("rarity")) {
                    rare = PrisonItem.Rare.valueOf(section.getString("rarity").toUpperCase());
                    parameters.add(ParameterManager.getRareParameter(rare));
                }

                PrisonItem prisonItem;
                if (parameters.contains(ParameterManager.getUpgradableParameter())) {
                    prisonItem = PrisonItem.builder()
                            .vanillaName(item)
                            .itemLevel(section.getInt("item_level"))
                            .nextPrisonItem(section.contains("next") ? section.getString("next") + "_" + (section.getInt("item_level") + 1) : null)
                            .upgradeRequireList(getUpProperties(item))
                            .vanillaItem(new org.runaway.items.Item.Builder(material)
                                    .data((short) data)
                                    .enchantmentList(section.contains("enchants") ? UpgradeMisc.getEnchants(section.getString("enchants")) : null)
                                    .name((rare != null ? rare.getColor() : "") + section.getString("name"))
                                    .lore(section.contains("lore") ? new Lore.BuilderLore()
                                            .addList(section.getStringList("lore"))
                                            .build() : null)
                                    .build().item())
                            .parameters(parameters)
                            .build();
                } else {
                    prisonItem = PrisonItem.builder()
                            .vanillaName(item)
                            .itemLevel(section.contains("item_level") ? section.getInt("item_level") : 0)
                            .vanillaItem(new org.runaway.items.Item.Builder(material)
                                    .data((short) data)
                                    .enchantmentList(section.contains("enchants") ? UpgradeMisc.getEnchants(section.getString("enchants")) : null)                                    .name(section.getString("name"))
                                    .lore(section.contains("lore") ? new Lore.BuilderLore()
                                            .addList(section.getStringList("lore"))
                                            .build() : null)
                                    .build().item())
                            .parameters(parameters)
                            .build();
                }
                ItemManager.addPrisonItem(prisonItem);
            });
        });

        FileConfiguration loader = EConfig.MODULES.getConfig();
        if (loader.getBoolean("loader.inventories")) loadInventories();
        if (loader.getBoolean("loader.trash_auction")) TrashAuction.load();
    }

    private RequireList getUpProperties(String cfg) {
        RequireList requireList = new RequireList();
        UpgradeMisc.getProperties(cfg).forEach((r, s) -> {
            if (r == UpgradeProperty.COST) {
                requireList.addRequire(MoneyRequire.builder().amount(Integer.parseInt(s)).takeAfter(true).build());
            } else if (r == UpgradeProperty.RATS) {
                requireList.addRequire(MobsRequire.builder().mobName("rat").amount(Integer.parseInt(s)).build());
            } else if (r == UpgradeProperty.WOOD) {
                requireList.addRequire(BlocksRequire.builder().localizedBlock(new LocalizedBlock(Material.DARK_OAK_WOOD)).amount(Integer.parseInt(s)).build());
            } else if (r == UpgradeProperty.BOW_KILL) {
                requireList.addRequire(MobsRequire.builder().mobName("zombie").amount(Integer.parseInt(s)).build());
            } else if (r == UpgradeProperty.STARS) {
                requireList.addRequire(StarsRequire.builder().amount(Integer.parseInt(s)).build());
            } else {
                requireList.addRequire(BlocksRequire.builder().localizedBlock(new LocalizedBlock(Material.valueOf(r.name().toUpperCase()))).amount(Integer.parseInt(s)).build());
            }
        });
        return requireList;
    }

    private List<Parameter> getParameters(List<String> string) {
        if (string == null) return new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();
        string.forEach(s -> {
            String[] spl = s.split(":");
            if (spl.length > 1) {
                Parameter parameter = getParameter(spl[0], spl[1]);
                if(parameter != null)
                parameters.add(getParameter(spl[0], spl[1]));
            } else {
                Parameter parameter =getParameter(s, null);
                if(parameter != null)
                parameters.add(getParameter(s, null));
            }
        });
        return parameters;
    }

    private Parameter getParameter(String s, Object value) {
        switch (s.toLowerCase()) {
            case "upg": {
                return ParameterManager.getUpgradableParameter();
            }
            case "nodrop": {
                return ParameterManager.getNodropParameter();
            }
            case "owner": {
                return ParameterManager.getOwnerParameter();
            }
            case "stblocks": {
                //return ParameterManager.getStattrakBlocksParameter();
                return null;
            }
            case "stmobs": {
                //return ParameterManager.getStattrakMobsParameter();
                return null;
            }
            case "stplayers": {
                return null;
                //return ParameterManager.getStattrakPlayerParameter();
            }
            case "minlevel": {
                return ParameterManager.getMinLevelParameter(Integer.parseInt(value.toString()));
            }
            case "runes": {
                return ParameterManager.getRunesParameter(Integer.parseInt(value.toString()));
            }
        }
        return null;
    }

    private void loadSQLite() {
        type_saving = SaveType.SQLITE;

        //Statistics table
        initializeDatabase(stat_table, "player", EStat.values());
        initializeDonateDatabase(donateTable, "player", DonateStat.values());
        Gamer.preparedRequests = getPreparedRequests();
        Gamer.donateRequests = getDonateRequests();
    }

    public static Database getMainDatabase() {
        return getInstance().getDatabase(instance.stat_table);
    }


    public PreparedRequests getPreparedRequests() {
        return preparedRequests;
    }

    public PreparedRequests getDonateRequests() {
        return donateRequests;
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

    public void initializeDonateDatabase(String databaseName, String primaryKey, Saveable[] saveables) {
        Database db = new SQLite(databaseName, Config.donateFile);
        db.load(primaryKey, saveables);
        databases.put(databaseName, db);
        this.donateRequests = new PreparedRequests(db);
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
            new Utils().RegisterEvent(new MobDamage());
            new Utils().RegisterEvent(new PlayerInventoryClick());
            new Utils().RegisterEvent(new PlayerInventoryClose());
            new Utils().RegisterEvent(new PlayerDeath());
            new Utils().RegisterEvent(new PlayerMove());
            new Utils().RegisterEvent(new MenuListener());
            new Utils().RegisterEvent(new ListPing());
            new Utils().RegisterEvent(new PlayerAttack());
            new Utils().RegisterEvent(new BossSpawn());
            new Utils().RegisterEvent(new PlayerFishing());
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
            new Utils().RegisterEvent(new BossDamageFarm());
            new Utils().RegisterEvent(new MobKillFarm());

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
            stats = new ArrayList<>();
            Arrays.stream(EStat.values()).forEach(eStat ->
                    stats.add(eStat.name().toLowerCase(Locale.ROOT)));

            Arrays.asList(new SetStatCommand(), new AutosellCommand(), new BoosterCommand(),
                    new LevelCommand(), new BoostersCommand(), new FractionCommand(), new RestartCommand(),
                    new SpawnCommand(), new UpgradeCommand(), new MinesCommand(), new TipCommand(),
                    new TrainerCommand(), new RebirthCommand(), new GiftCommand(),
                    new ShopCommand(), new AchievementsCommand(), new DonateCommand(),
                    new SpawnerCommand(), new ScrollsCommand(), new ProfileCommand(), new PayCommand(),
                    new BaseCommand(), new TrashCommand(), new QuestCommand(), new FisherCommand(),
                    new JobCommand(), new MsgCommand(), new ReplyCommand(), new InvseeCommand(), new ItemCommand(),
                    new HideCommand(), new RuneCommand(), new KitCommand()).forEach(CommandManager::register);

        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering commands!");
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Регистрация мобов
    private static void registerMobs() {
        try {
            new MobManager();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with registering mobs!");
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
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Подгрузка инвентарей
    private void loadInventories() {
        try {
            FileConfiguration loader = EConfig.MODULES.getConfig();
            if (loader.getBoolean("loader.main_menu")) MainMenu.load();
            if (loader.getBoolean("loader.upmenu")) UpItemsMenu.load();

            BlockShopMenu.load();
            ShopMenu.load();
            Confirmation.load();
            RebirthMenu.load();
            MinesQuest.load();
            Privs.loadIcons();
            PassivePerksMenu.load();
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in creating inventories!");
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
        Vars.sendSystemMessage(TypeMessage.INFO, "PermissionsEx has not been installed yet");
    }

    //Привязка TelegramBotsAPI
    private void loadTelegramBotsAPI() {
        useTelegramBots = Bukkit.getPluginManager().isPluginEnabled("TelegramBotsAPIPlugin");
        if (useTelegramBots) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "TelegramBotsAPI was successfully connected");
            this.bot_username = EConfig.CONFIG.getConfig().getString("telegram.username");
            this.bot_token = EConfig.CONFIG.getConfig().getString("telegram.token");
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "TelegramBotsAPI has not been installed yet");
    }

    //Привязка ItemsAdder
    private void loadItemsAdder() {
        boolean usePlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        useItemsAdder = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder") && usePlaceholderAPI;
        if (useItemsAdder) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "ItemsAdder and PlaceholderAPI were successfully connected");
            return;
        }
        Vars.sendSystemMessage(TypeMessage.INFO, "ItemsAdder and PlaceholderAPI have not been installed yet");
    }

    //Привязка NametagEdit
    private void loadNametagEdit() {
        useNametagEdit = Bukkit.getPluginManager().isPluginEnabled("NametagEdit");
        if (useNametagEdit) {
            Vars.sendSystemMessage(TypeMessage.SUCCESS, "NametagEdit was successfully connected");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Utils.getPlayers().forEach(s -> GamerManager.getGamer(Bukkit.getPlayer(s)).setNametag());
                }
            }.runTaskTimer(instance, 20L, 400L);
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

    //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
    //Подгрузка сообщений
    private void loadMessage() {
        try {
            if (EConfig.MESSAGES.getConfig().contains("prefix")) {
                //Vars.setPrefix(EConfig.MESSAGES.getConfig().getString("prefix"));
                Vars.setPrefix("&7[&d&lPrison&7] &r");
            } else {
                EConfig.MESSAGES.getConfig().set("prefix", "&6&lPrison > &f");
                Vars.setPrefix("&7[&d&lPrison&7] &r");
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
                    Gamer gamer = GamerManager.getGamer(Bukkit.getPlayer(s));
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

            Map<String, Long> money;
            Map<String, Long> blocks;
            Map<String, Long> level;
            Map<String, Long> rats;
            Map<String, Long> rebirth;
            Map<String, Long> keys;
            Map<String, Long> dm;
            money = getPreparedRequests().getTop(EStat.MONEY.getColumnName(), 10);
            blocks = getPreparedRequests().getTop(EStat.BLOCKS.getColumnName(), 10);
            level = getPreparedRequests().getTop(EStat.LEVEL.getColumnName(), 10);
            rats = getPreparedRequests().getTop(EStat.KILLS.getColumnName(), 10);
            rebirth = getPreparedRequests().getTop(EStat.REBIRTH.getColumnName(), 10);
            keys = getPreparedRequests().getTop(EStat.KEYS.getColumnName(), 10);
            dm = getDonateRequests().getTop(DonateStat.TOTAL_DONATED.getColumnName(), 10);
            tops.put("money", new TopPlayers(money, MoneyType.RUBLES.getShortName()));
            tops.put("blocks", new TopPlayers(blocks,"блоков"));
            tops.put("levels", new TopPlayers(level, "уровень"));
            tops.put("rats", new TopPlayers(rats,"крыс"));
            tops.put("rebirths", new TopPlayers(rebirth,"перерождений"));
            tops.put("keys", new TopPlayers(keys, "ключей"));
            tops.put("donate", new TopPlayers(dm, "рублей"));
            BannerBoardAPI api = BannerBoardManager.getAPI();
            api.registerCustomRenderer("prison_leaders", this, false, TopsBanner.class);
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading players top!");
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public void forceUpdateTop() {

        Map<String, Long> money1 = getPreparedRequests().getTop(EStat.MONEY.getColumnName(), 10);
        Map<String, Long> blocks1 = getPreparedRequests().getTop(EStat.BLOCKS.getColumnName(), 10);
        Map<String, Long> level1 = getPreparedRequests().getTop(EStat.LEVEL.getColumnName(), 10);
        Map<String, Long> rats1 = getPreparedRequests().getTop(EStat.KILLS.getColumnName(), 10);
        Map<String, Long> rebirth1 = getPreparedRequests().getTop(EStat.REBIRTH.getColumnName(), 10);
        Map<String, Long> keys1 = getPreparedRequests().getTop(EStat.KEYS.getColumnName(), 10);
        Map<String, Long> dm1 = getDonateRequests().getTop(DonateStat.TOTAL_DONATED.getColumnName(), 10);
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
                Prison.tops.get(s).setTopValues(rebirth1);
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
                Mine mine = new Mine(file.getString("techName"),
                        file.getString("name"),
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
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    //Удаление мобов
    private static void removeEntities() {
        try {
            Bukkit.getWorlds().forEach((world) -> {
                world.getEntities().stream().filter((entity) ->
                                !entity.getType().equals(EntityType.PLAYER) &&
                                !entity.getType().equals(EntityType.ITEM_FRAME) &&
                                !entity.getType().equals(EntityType.ARMOR_STAND))
                        .forEachOrdered(Entity::remove);
            });
            Vars.sendSystemMessage(TypeMessage.INFO, "Entities removed");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in delete entities! Please, don`t use /reload. Use /stop or /restart");
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
