package net.baobab.mdbook.znai.integration.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;

public class DataBase {

    public static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./.znai-plugin/snippet;INIT=RUNSCRIPT FROM 'classpath:create.sql'";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void deleteList(final List<String> delKey) {
        String range = IntStream.range(0, delKey.size())
                .mapToObj(i -> "?")
                .collect(joining(", ", " ( ", " ) "));
        String sql = "DELETE FROM snippet WHERE code IN " + range;
        try ( var delete = getPrepareStatement(sql)) {
            for (int i = 0; i < delKey.size(); i++) {
                delete.setString(i + 1, delKey.get(i));
            }
            delete.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insert(final String code, final String genCode) {
        String sql = "INSERT INTO snippet VALUES (?, ?)";
        try ( var insert = getPrepareStatement(sql)) {
            insert.setString(1, code);
            insert.setString(2, genCode);
            insert.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SortedMap<String, String> getData() {
        SortedMap<String, String> collection = new TreeMap<>();
        try ( var stmt = getConnection().createStatement()) {
            String sql = "SELECT code, gen_code FROM snippet";
            try ( var resultSet = stmt.executeQuery(sql)) {
                while (resultSet.next()) {
                    String code = resultSet.getString("code");
                    String genCode = resultSet.getString("gen_code");
                    collection.put(code, genCode);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return collection;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static PreparedStatement getPrepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

}
