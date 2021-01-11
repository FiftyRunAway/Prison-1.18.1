package org.runaway.enums;

import org.bukkit.configuration.file.FileConfiguration;
import org.runaway.configs.Config;
import org.runaway.configs.ConfigHeaders;

import java.io.File;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public enum EConfig {
    MINES("Mines", Config.mines, Config.minesFile, false, null),
    ACHIEVEMENTS("Achievements", Config.achievs, Config.achievsFile, true, null),
    BLOCKS("Blocks", Config.blocks, Config.blocksFile, true, null),
    BOOSTERS("Boosters", Config.boosters, Config.boostersFile, true, null),
    BOSS("Boss", Config.boss, Config.bossFile, false, null),
    CASES("Cases", Config.cases, Config.casesFile, false, null),
    CONFIG("Config", Config.standart, Config.standartFile, false, ConfigHeaders.configHeader()),
    DONATE("Donate", Config.donate, Config.donateFile, false, null),
    LOG("Log", Config.log, Config.logFile, true, null),
    MESSAGES("Messages", Config.messages, Config.messagesFile, false, null),
    MOBS("Mobs", Config.mobs, Config.mobsFile, false, null),
    MODULES("Modules", Config.modules, Config.modulesFile, false, null),
    SHOP("Shop", Config.shop, Config.shopFile, false, null),
    STATISTICS("Statistics", Config.statistics, Config.statisticsFile, true, null),
    TALANTS("Talants", Config.talants, Config.talantsFile, true, null),
    TRAINER("Trainer", Config.trainer, Config.trainerFile, false, null),
    BATTLEPASS("BattlePass", Config.bp, Config.bpFile, false, ConfigHeaders.bpHeader()),
    BATTLEPASS_DATA("BPData", Config.bpData, Config.bpDataFile, true, null),
    UPGRADE("Upgrade", Config.upgrade, Config.upgradeFile, false, null),
    REBIRTH_DATA("RData", Config.rebirthData, Config.rebirthDataFile, true, null),
    MINE_QUESTS("MineQuests", Config.quests, Config.questsFile, false, null),
    QUESTS_DATA("QuestsData", Config.questsData, Config.questsDataFile, true, null),
    JOBS_DATA("JobsData", Config.job, Config.jobFile, true, null);

    private String name;
    private FileConfiguration configuration;
    private File file;
    private boolean save;
    private String header;

    EConfig(String name, FileConfiguration configuration, File file, boolean saveOnUnload, String header) {
        this.configuration = configuration;
        this.file = file;
        this.name = name;
        this.save = saveOnUnload;
        this.header = header;
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

    public String getHeader() {
        return this.header;
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
