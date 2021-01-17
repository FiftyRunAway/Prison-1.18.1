package org.runaway.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.io.File;
import java.util.Arrays;

/*
 * Created by _RunAway_ on 14.1.2019
 */

public class Config {

    public static FileConfiguration upgrade, boss, donate, shop,
            talants, log, standart, boosters, messages, mines, mobs, modules, cases, achievs, trainer,
            bp, bpData, rebirthData, quests, questsData, job;
    public static File upgradeFile, bossFile, donateFile, shopFile,
            talantsFile, logFile, standartFile, boostersFile, messagesFile, minesFile, mobsFile, modulesFile, casesFile,
            achievsFile, trainerFile, bpFile, bpDataFile, rebirthDataFile, questsFile, questsDataFile, jobFile;

    public void loadConfigs() {
        try {
            Arrays.stream(EConfig.values()).forEach(config -> {
                CreateConfig(config.getConfigName(), config);
                Vars.sendSystemMessage(TypeMessage.INFO, "'" + Utils.upCurLetter(config.getConfigName(), 1) + ".yml' config was loaded");
                if (config.getHeader() != null) {
                    config.getConfig().options().header(config.getHeader()).copyHeader();
                    config.saveConfig();
                }
            });

            Vars.sendSystemMessage(TypeMessage.SUCCESS, EConfig.values().length + " configs was loaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading configs!");
            Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public void unloadConfigs() {
        try {
            Arrays.stream(EConfig.values()).forEach(config -> {
                if (config.isSaving()) config.saveConfig();
            });
            Vars.sendSystemMessage(TypeMessage.SUCCESS, EConfig.values().length + " configs was unloaded!");
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with unloading configs!");
            ex.printStackTrace();
        }
    }

    private void CreateConfig(String FileName, EConfig config) {
        CreaterConfig createrConfig = new CreaterConfig(FileName);
        createrConfig.CreateConfig(config);
        config.saveConfig();
    }
}
