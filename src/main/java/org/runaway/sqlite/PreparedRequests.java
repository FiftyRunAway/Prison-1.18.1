package org.runaway.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedRequests {

    public static void voidRequest(DoVoid doing, Database db, String player, String table, Object value, String column) {
        if (doing.equals(DoVoid.INSERT)) {
            try {
                PreparedStatement st = db.connection.prepareStatement("INSERT INTO " + table + " (player, " + column + ") VALUES (?,?)");
                st.setString(1, player);
                st.setObject(2, value);
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (doing.equals(DoVoid.UPDATE)) {
            try {
                PreparedStatement st = db.connection.prepareStatement("UPDATE " + table + " SET " + column + " = ? WHERE player = ?");
                st.setObject(1, value);
                st.setString(2, player);
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Object returnRequest(DoReturn doing, Database db, String player, String table, String column) {
        if (doing.equals(DoReturn.SELECT)) {
            try {
                PreparedStatement st = db.connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
                st.setString(1, player);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    return rs.getObject(column);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
