package bankapp;

import bankapp.config.Config;
import bankapp.persistence.database.DataBaseConnectionMySQL;
import bankapp.userinterface.MenuApp;



// Clase principal: arranca la aplicacion
public class Main {

    public static void main(String[] args) {
        DataBaseConnectionMySQL.getInstance().getConnection();
        MenuApp menu = Config.createMenuApp();
        menu.iniciar();
    }
}
