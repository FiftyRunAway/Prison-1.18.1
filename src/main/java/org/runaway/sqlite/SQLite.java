package org.runaway.sqlite;

import org.runaway.Prison;
import org.runaway.enums.Saveable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SQLite extends Database {


    private String customCreateString;

    private File dataFolder;

    public SQLite(String databaseName, File folder) {
        setDbName(databaseName);
        dataFolder = folder;
    }

    public Connection getSQLConnection() {
        return this.connection;
    }

    public void load(String primaryKey, Saveable[] saveables) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("CREATE TABLE IF NOT EXISTS %s (`%s` VARCHAR (32) NOT NULL,", getDbName(), primaryKey));
            Arrays.stream(saveables).forEach(eStat ->
                    sb.append('`').append(eStat.getColumnName()).append('`').append(' ')
                            .append(eStat.getColumnType()).append(" NOT NULL,"));
            sb.append("PRIMARY KEY (player));");
            System.out.println(sb.toString());
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File(Prison.getInstance().getDataFolder().getAbsolutePath()) + File.separator + getDbName());
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA synchronous = OFF;");
            statement.execute("PRAGMA temp_store = MEMORY;");
            statement.execute(sb.toString());
            for (Saveable saveable : saveables) {
                try {
                    statement.execute(String.format("ALTER TABLE %s ADD COLUMN %s %s NOT NULL", getDbName(), saveable.getColumnName(), saveable.getColumnType()));
                    statement.execute(String.format("UPDATE %s players SET %s = '%s'", getDbName(), saveable.getColumnType(), saveable.getDefaultValue()));
                } catch (SQLException e) {
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
