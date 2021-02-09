package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.runaway.entity.Attributable;
import org.runaway.entity.MobController;
import org.runaway.entity.MobManager;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.MobType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnerCommand extends CommandManager {

    public SpawnerCommand() {
        super("spawners", "prison.admin", Collections.singletonList("спавнер"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            EConfig.MOBS.getConfig().getConfigurationSection("mobs").getKeys(false).forEach(s ->
                    p.sendMessage(Utils.colored("&b- " + s)));
            p.sendMessage((Utils.colored("&aСписок окончен")));
            return;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            MobType entity;
            try {
                entity = MobType.valueOf(args[1].toUpperCase());
            } catch (Exception ex) {
                p.sendMessage(Utils.colored("&cОшибка: Нет такого вида моба!"));
                return;
            }
            int respawnTime = Integer.parseInt(args[2]);
            ConfigurationSection section;
            String uid = Utils.generateUID();
            Configuration cfg = EConfig.MOBS.getConfig();
            if (!cfg.contains("mobs")) cfg.createSection("mobs");
            section = cfg.createSection("mobs." + uid);
            section.set("respawnTime", respawnTime);
            section.set("location", Utils.serializeLocation(p.getLocation()));
            section.set("type", args[1].toLowerCase());
            section.set("lastDeathTime", -1);
            p.sendMessage(Utils.colored("&aВы успешно установили спавнер &2" + args[1].toUpperCase()));
            EConfig.MOBS.saveConfig();
            Attributable attributable = MobManager.getAttributable(args[1]);
            MobController.builder()
                    .attributable(attributable) //тут паттерн моба
                    .spawnLocation(p.getLocation())
                    .respawnTime(respawnTime) //sec
                    .lastDeathTime(-1)
                    .UID(Utils.generateUID())
                    .mobSkillList(attributable.getMobSkills())
                    .build()
                    .init();

        } else {
            List<String> mobs = new ArrayList<>();
            MobManager.attributableMap.keySet().forEach(m -> mobs.add(m));
            p.sendMessage(Utils.colored("&cИспользование: /" + cmdName + " <debug, set> " + mobs.toString() + " [time]"));
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
