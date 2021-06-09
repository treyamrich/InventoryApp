import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class Sql {

    private static Connection connect() {
        //Create connection object
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:db\\Kaimuki_Server_Room.db";
            // Change conn to a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    //This method is only for the selecting everything from the database
    public static ArrayList<String[]> selectEverything() {
       String query = "SELECT * FROM inventory ORDER BY id DESC;";
       ArrayList<String[]> results = new ArrayList<String[]>();
       try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
            //Loop through results, creating a new array every time, then add array to array list
            while(rs.next()) {
                
                String[] columns = new String[5];
                columns[0] = rs.getString("hardware_type");
                columns[1] = rs.getString("serial_num");
                columns[2] = rs.getString("time_in");
                columns[3] = rs.getString("time_out");
                columns[4] = rs.getString("description");
                results.add(columns);
            }
            conn.close();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }
    //This method is for filtering the database by hardware type
    public static ArrayList<String[]> filterQuery(String field, String condition) {
        String query = "SELECT * FROM inventory WHERE " + field + " =? ORDER BY id DESC;";
        ArrayList<String[]> results = new ArrayList<String[]>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             //Set condition
             pstmt.setString(1, condition);
             //Loop through results
             ResultSet rs = pstmt.executeQuery();
             while(rs.next()) {
                String[] columns = new String[5];
                columns[0] = rs.getString("hardware_type");
                columns[1] = rs.getString("serial_num");
                columns[2] = rs.getString("time_in");
                columns[3] = rs.getString("time_out");
                columns[4] = rs.getString("description");
                results.add(columns);
             }
             conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }
    //This method is for filtering dates by amt of days
    public static ArrayList<String[]> filterByYearQuery(String year) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        String query = "SELECT * FROM inventory WHERE time_in LIKE '" + year + "%'" + 
                        " OR time_out LIKE '" + year + "%'" +
                        " ORDER BY id DESC;";
        try(Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while(rs.next()) {
                String[] columns = new String[5];
                columns[0] = rs.getString("hardware_type");
                columns[1] = rs.getString("serial_num");
                columns[2] = rs.getString("time_in");
                columns[3] = rs.getString("time_out");
                columns[4] = rs.getString("description");
                results.add(columns);
             }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }
    //This method inserts a single record into the database
    public static void insertQuery(String hardware_type, String serial_num, String time_in, String time_out, String description) {
        String query = "INSERT INTO inventory (hardware_type, serial_num, time_in, time_out, description) VALUES (?, ?, ?, ?, ?);";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             //Replace the question mark with item info
             pstmt.setString(1, hardware_type);
             pstmt.setString(2, serial_num);
             pstmt.setString(3, time_in);
             pstmt.setString(4, time_out);
             pstmt.setString(5, description);
             //Execute query and close
             pstmt.executeUpdate();
             conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //This method deletes a record based off Serial Number
    public static void deleteQuery(String serialNum) {
        String query = "DELETE FROM inventory WHERE serial_num = ?;";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             //Replace the question mark with the serial number
             pstmt.setString(1, serialNum);
             //Execute query
             pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //This method can be used for altering tables
    public static void alterTableQuery() {
        String sql = "CREATE TABLE IF NOT EXISTS inventory (id INTEGER PRIMARY KEY, serial_num TEXT, time_in TEXT, time_out TEXT, hardware_type TEXT, description TEXT);";
        //String sql = "DELETE FROM inventory;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
              stmt.execute(sql);
              conn.close();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}