package org.runaway.sqlite;

import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;

public class SQLite extends Database {


    private String customCreateString;

    private File dataFolder;

    public SQLite(String databaseName, String createStatement, File folder) {
        setDbName(databaseName);
        customCreateString = createStatement;
        dataFolder = folder;
    }

    public Connection getSQLConnection() {
        return this.connection;
    }

    public void load() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File(Main.getInstance().getDataFolder().getAbsolutePath()) + File.separator + getDbName());
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA synchronous = OFF;");
            statement.execute("PRAGMA temp_store = MEMORY;");
            statement.execute(customCreateString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
