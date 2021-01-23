package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_12_R1.command.CraftRemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/*
 * Created by _RunAway_ on 19.1.2019
 */

public abstract class CommandManager implements CommandExecutor {

    private static CommandMap cmap;
    protected final String command;
    private final List<String> alias;
    private final String permMessage;
    private final boolean console;

    public CommandManager(String command) {
        this(command, null, null, false);
    }

    public CommandManager(String command, String permissionMessage) {
        this(command, permissionMessage, null, false);
    }

    public CommandManager(String command, List<String> aliases) {
        this(command, null, aliases, false);
    }

    public CommandManager(String command, String permissionMessage, List<String> aliases, boolean console) {
        this.command = command.toLowerCase();
        this.permMessage = permissionMessage;
        this.alias = aliases;
        this.console = console;
    }

    public void register() {
        unregister(this.command);
        ReflectCommand cmd = new ReflectCommand(this.command);
        cmd.setAliases(this.alias);
        cmd.setPermissionMessage(this.permMessage);
        getCommandMap().register("", cmd);
        cmd.setExecutor(this);
    }

    private static void unregister(String command) {
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            final CommandMap map = (CommandMap) field.get(Bukkit.getServer());
            final Field field2 = map.getClass().getDeclaredField("knownCommands");
            field2.setAccessible(true);
            final HashMap<String, Command> knownCommands = (HashMap<String, Command>) field2.get(map);
            knownCommands.remove(command);
            field2.setAccessible(false);
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex9) {
            ex9.printStackTrace();
        }
    }

    private CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return cmap;
        }
        return getCommandMap();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        if (sender instanceof ConsoleCommandSender || sender instanceof CraftRemoteConsoleCommandSender) {
            if (console) {
                runConsoleCommand(sender, args, cmd.getName());
                return true;
            }
            sender.sendMessage("Из консоли команды выполнять нельзя...");
            return true;
        }
        Player p = (Player) sender;
        if(this.permMessage != null && !sender.hasPermission(this.permMessage)) {
            p.sendMessage(ChatColor.RED + "У вас недостаточно прав для использования данной команды!");
            return true;
        }
        runCommand(p, args, cmd.getName());
        return true;
    }

    public abstract void runCommand(Player p, String[] args, String cmdName);

    public abstract void runConsoleCommand(CommandSender cs, String[] args, String cmdName);

    private static final class ReflectCommand extends Command {

        private CommandManager exe;

        ReflectCommand(String command) {
            super(command);
        }

        void setExecutor(CommandManager exe) {
            this.exe = exe;
        }

        public boolean execute(CommandSender s, String commandLabel, String[] args) {
            if (exe != null) {
                return exe.onCommand(s, this, commandLabel, args);
            }
            return false;
        }
    }
}
