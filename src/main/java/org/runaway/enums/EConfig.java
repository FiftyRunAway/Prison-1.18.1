package org.runaway.enums;

import org.bukkit.configuration.file.FileConfiguration;
import org.runaway.configs.Config;

import java.io.File;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public enum EConfig {
    STATISTICS("Statistics", Config.statistics, Config.statisticsFile, true),
    UPGRADE("Upgrade", Config.upgrade, Config.upgradeFile, false),
    BLOCKS("Blocks", Config.blocks, Config.blocksFile, true),
    BOSS("Boss", Config.boss, Config.bossFile, false),
    DONATE("Donate", Config.donate, Config.donateFile, false),
    SHOP("Shop", Config.shop, Config.shopFile, false),
    TALANTS("Talants", Config.talants, Config.talantsFile, true),
    CONFIG("Config", Config.standart, Config.standartFile, false),
    BOOSTERS("Boosters", Config.boosters, Config.boostersFile, true),
    LOG("Log", Config.log, Config.logFile, true),
    MESSAGES("Messages", Config.messages, Config.messagesFile, false),
    MINES("Mines", Config.mines, Config.minesFile, false),
    MOBS("Mobs", Config.mobs, Config.mobsFile, false),
    MODULES("Modules", Config.modules, Config.modulesFile, false),
    CASES("Cases", Config.cases, Config.casesFile, false),
    ACHIEVEMENTS("Achievements", Config.achievs, Config.achievsFile, true),
    TRAINER("Trainer", Config.trainer, Config.trainerFile, false);

    private String name;
    private FileConfiguration configuration;
    private File file;
    private boolean save;

    EConfig(String name, FileConfiguration configuration, File file, boolean saveOnUnload) {
        this.configuration = configuration;
        this.file = file;
        this.name = name;
        this.save = saveOnUnload;
    }

    public FileConfiguration getConfig() {
        return this.configuration;
    }

    public String getConfigName() {
        return this.name;
    }

    public FileConfiguration getFileConfigurationConfig() {
        return this.configuration;
    }

    public boolean isSaving() {
        return this.save;
    }

    public void saveConfig() {
        try {
            this.configuration.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setDefault(EConfig config, FileConfiguration fileConfiguration, File file) {
        config.file = file;
        config.configuration = fileConfiguration;
    }
}
