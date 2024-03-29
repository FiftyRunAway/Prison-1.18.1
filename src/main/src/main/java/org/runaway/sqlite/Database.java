package org.runaway.sqlite;

import org.runaway.Prison;
import org.runaway.enums.Saveable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class Database {

    protected Connection connection;
    protected String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public abstract Connection getSQLConnection();

    public abstract void load(String primaryKey, Saveable[] saveables);

    public void initialize() {
        connection = getSQLConnection();
    }

    /**
     * Execute any statement using this method. This will return a success or
     * failure boolean.
     * <p>
     *
     * @param statement to execute.
     *
     * @return the {@link Database}'s success or failure (true/false).
     */
    public Boolean executeStatement(String statement) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
            return !ps.execute();
        } catch (SQLException ex) {
            Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
            return false;
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
                return false;
            }
        }
    }
    /**
     * Get a single value from the database. Your If your statement returns multiple
     * values, only the first value will return. Use queryRow for multiple values in
     * 1 row.
     * <p>
     *
     * @param statement
     *            statement to execute.
     *
     * @param row
     *            row you would like to store data from.
     *
     * @return the {@link Database}'s Query in Object format. Casting required to
     *         change variables into their original form.
     */
    public Object queryValue(String statement, String row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getObject(row);
            }
        } catch (SQLException ex) {
            Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    /**
     * Get a list of data based on your statement. This should only be used when
     * querying for multiple values, in 1 row. I.E If you had a row called test,
     * with numbers 1 to 10. This method could be used to build a list containing
     * the data 1 to 10 from this row.
     * <p>
     *
     * @param statement
     *            statement to execute.
     *
     * @param row
     *            row you would like to store data from.
     *
     * @return the {@link Database}'s Query in List<Object> format. Casting required
     *         to change variables into their original form.
     */
    public List<Object> queryRow(String statement, String row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Object> objects = new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                objects.add(rs.getObject(row));
            }
            return objects;
        } catch (SQLException ex) {
            Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    /**
     * Get a map which contains lists for each row specified. For each row, a list
     * is stored containing all the queried values.
     *
     * You can access the list for each row using <Row, List> format. The list is in
     * List<Object> format.
     * <p>
     *
     * @param statement
     *            statement to execute.
     *
     * @param row
     *            row(s) you would like to store data from. Minimum 1 row required.
     *
     * @return the {@link Database}'s Query in Map<Row,List<Object>> format. Casting
     *         required to change variables into their original form.
     */
    public Map<String, List<Object>> queryMultipleRows(String statement, String... row) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Object> objects = new ArrayList<>();
        Map<String, List<Object>> map = new HashMap<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);

            rs = ps.executeQuery();
            while (rs.next()) {
                for (String singleRow : row) {
                    objects.add(rs.getObject(singleRow));
                }

                for (String singleRow : row) {
                    map.put(singleRow, objects);
                }

            }
            return map;
        } catch (SQLException ex) {
            Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Prison.getInstance().getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }


    /**
     * Close the current connection of the statement to the database.
     * <p>
     *
     * @param ps
     *            statement previously used.
     *
     * @param rs
     *            result set that was returned from the statement.
     *
     */
    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(Prison.getInstance(), ex);
        }
    }


    /**
     * Close the current connection to the database. The database will need to be
     * re-initialized if this is used. When intializing using the main class, it
     * will delete this current object and create a new object connected to the db.
     * If you'd like to reload this db without trashing the database object, invoke
     * the load() method through the global map of databases. E.g
     * getDatabase("name").load();.
     *
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            Error.close(Prison.getInstance(), e);
        }
    }
}
