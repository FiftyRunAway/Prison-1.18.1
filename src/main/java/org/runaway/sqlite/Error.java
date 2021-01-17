package org.runaway.sqlite;

import org.runaway.Prison;

import java.util.logging.Level;

public class Error {

    public static void execute(Prison plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Prison plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
