package org.runaway.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.runaway.utils.Vars;
import org.runaway.enums.EConfig;

import java.io.File;

/*
 * Created by _RunAway_ on 15.1.2019
 */

public class CreaterConfig implements IConfig {

    private String name;

    CreaterConfig(String name) {
        this.name = name;
    }

    @Override
    public void CreateConfig(EConfig config) {
        File file = new File("plugins" + File.separator + Vars.namePlugin(),this.name + ".yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        EConfig.setDefault(config, fileConfiguration, file);
    }
}
