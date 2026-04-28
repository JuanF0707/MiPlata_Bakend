package bankapp;

import bankapp.config.Config;
import bankapp.userinterface.MenuApp;

// Clase principal: arranca la aplicacion
public class Main {

    public static void main(String[] args) {
        MenuApp menu = Config.createMenuApp();
        menu.iniciar();
    }
}
