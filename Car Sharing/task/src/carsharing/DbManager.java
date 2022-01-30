package carsharing;

import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbManager {

    private static DbManager instance;
    private Connection connection;
    private String url = "jdbc:h2:./src/carsharing/db/";


    private DbManager(String dbName) throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            this.url += dbName;
            this.connection = DriverManager.getConnection(url);
            this.connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            System.out.println("DB Connection Failed: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DbManager getInstance(String dbName) throws SQLException {
        if (instance == null) {
            instance = new DbManager(dbName);
        } else if (instance.getConnection().isClosed()) {
            instance = new DbManager(dbName);
        }

        return instance;
    }

    public void initDb() throws SQLException, FileNotFoundException {
        try {
            RunScript.execute(connection, new FileReader("./src/carsharing/sql/initDB.sql"));
        } catch (FileNotFoundException e) {
            System.out.println("SQL Script not found: " + e.getMessage());
        }
    }
}
