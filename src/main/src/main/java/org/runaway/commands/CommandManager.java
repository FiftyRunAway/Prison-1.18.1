package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_18_R1.command.CraftRemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.runaway.Prison;
import org.runaway.commands.completers.TabCompletion;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/*
 * Created by _RunAway_ on 19.1.2019
 */

public abstract class CommandManager implements CommandExecutor, TabExecutor {

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
        //unregister(this.command);
        ReflectCommand cmd = new ReflectCommand(this.command);
        cmd.setAliases(this.alias);
        cmd.setPermissionMessage(this.permMessage);
        getCommandMap().register("", cmd);
        cmd.setExecutor(this);
    }

    private static void unregister(String command) {
        try {
            Object result = getPrivateField(Prison.getInstance().getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(command);
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

    private static Object getPrivateField(Object object, String field)throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public abstract void runCommand(Player p, String[] args, String cmdName);

    public abstract void runConsoleCommand(CommandSender cs, String[] args, String cmdName);

    public TabCompletion getTabCompletion() {
        return null;
    }

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

        @Override
        public List<String> tabComplete(CommandSender sender, String alais, String[] args) {
            if (exe != null) return exe.onTabComplete(sender, this, alais, args);
            return null;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (getTabCompletion() == null) return null;
        return getTabCompletion().getVariants(commandSender, command, strings);
    }
}
