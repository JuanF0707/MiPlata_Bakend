package bankapp.persistence.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionMySQL {

    // Atributos
    private static final String url = "jdbc:mysql://localhost:3306/mi_plata";
    private static final String user = "root";
    private static final String password = "";
    private Connection connection;

    private static DataBaseConnectionMySQL instance;

    // Constructor
    private DataBaseConnectionMySQL(){
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("CONECTADO A LA BASE DE DATOS");
        } catch (SQLException e) {
            System.out.println("Fallo al conectar la Base De Datos");
            System.exit(1); // Cierro el programa cuando la BDD no conecta.
        }
    }

    public static DataBaseConnectionMySQL getInstance(){
        if (instance == null){
            instance = new DataBaseConnectionMySQL();
        }
        return instance;
    }


    public Connection getConnection() {
        return connection;
    }
}
