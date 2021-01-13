package org.runaway.sqlite;

import com.google.common.base.Joiner;
import org.runaway.enums.Saveable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PreparedRequests {

    private final Database database;
    private final String dbName;
    public PreparedRequests(Database database) {
        this.database = database;
        this.dbName = database.getDbName();
    }

    public Database getDatabase() {
        return database;
    }

    public String getDbName() {
        return this.dbName;
    }

    public Map<Saveable, Object> getAllValues(String player, Saveable[] saveables) {
        return getAllValues("player", player, saveables);
    }

    public void saveAllValues(String primaryKey, String primaryValue, Map<Saveable, Object> saveableValues) {
        primaryValue = primaryValue.toLowerCase();
        List<String> columnNames = saveableValues.keySet().stream().map(Saveable::getColumnName).collect(Collectors.toList());
        String statementString = String.format("UPDATE %s SET ('%s') = ('%s') WHERE %s = '%s'",
                getDbName(), Joiner.on("','").join(columnNames), Joiner.on("','").join(saveableValues.values()), primaryKey, primaryValue);
        getDatabase().executeStatement(statementString);
    }

    public boolean isExist(String key, String value) {
        try {
            PreparedStatement preparedStatement = getDatabase().getSQLConnection().prepareStatement(String.format("SELECT EXISTS (SELECT 1 FROM %s WHERE %s = '%s')"
                    , getDbName(), key, value.toLowerCase()));
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exist = resultSet.getBoolean(1);
            preparedStatement.close();
            return exist;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Map<Saveable, Object> getAllValues(String key, String value, Saveable[] saveables) {
        try {
            Map<Saveable, Object> allValues = new HashMap<>();
            if(!isExist(key, value)) {
                Arrays.stream(saveables).forEach(eStat ->
                        allValues.put(eStat, eStat.getDefaultValue()));
                create(key, value, Arrays.asList(saveables.clone()));
                return allValues;
            }
            String statementString = String.format("SELECT * FROM %s WHERE %s = '%s'", getDbName(), key, value.toLowerCase());
            PreparedStatement preparedStatement = getDatabase().getSQLConnection().prepareStatement(statementString);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Arrays.stream(saveables).forEach(eStat -> {
                    try {
                        allValues.put(eStat, resultSet.getObject(eStat.getColumnName()));
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                });
            }
            preparedStatement.close();
            return allValues;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void create(String key, String primary, List<Saveable> saveableList) {
        Map<String, Object> defaultValues = new HashMap<>();
        saveableList.forEach(saveable ->
                defaultValues.put(saveable.getColumnName(), saveable.getDefaultValue()));
        List<String> columnNames = saveableList.stream().map(Saveable::getColumnName).collect(Collectors.toList());
        String allColumns = Joiner.on("','").join(columnNames);
        String allValues = Joiner.on("','").join(defaultValues.values());
        String statementString = String.format("INSERT INTO %s (%s, '%s') VALUES ('%s', '%s')", getDbName(), key, allColumns, primary.toLowerCase(), allValues);
        getDatabase().executeStatement(statementString);
    }

    public void voidRequest(DoVoid doing, String player, String column, Object value) {
        if (doing.equals(DoVoid.INSERT)) {
            getDatabase().executeStatement(String.format("INSERT INTO %s (player, %s) VALUES ('%s', '%s')", getDbName(), column, player, value));
        } else if (doing.equals(DoVoid.UPDATE)) {
            getDatabase().executeStatement(String.format("UPDATE %s SET %s = '%s' WHERE player = '%s'", getDbName(), column, value, player));
        }
    }

    public Object returnRequest(DoReturn doing, String player, String column) {
        if (doing.equals(DoReturn.SELECT)) {
            try {
                PreparedStatement st = getDatabase().connection.prepareStatement(String.format("SELECT * FROM %s WHERE player = %s", getDbName(), player));
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    return rs.getObject(column);
                }
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, Long> getTop(String orderBy, int limit) {
        try {
            Map<String, Long> top = new TreeMap<>();
            PreparedStatement preparedStatement = database.getSQLConnection().prepareStatement(String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT " + limit, getDbName(), orderBy));
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                String name = set.getString("full_name");
                top.put(name, set.getLong(orderBy));
            }
            Map<String, Long> result = top.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            preparedStatement.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
