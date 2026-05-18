package bankapp.config;

import bankapp.persistence.database.DataBaseConnectionMySQL;
import bankapp.persistence.repository.ClienteRepositoryAdapterMySQL;
import bankapp.services.*;
import bankapp.services.outputport.ClientePersistencePort;
import bankapp.userinterface.MenuApp;
import bankapp.view.AdminView;
import bankapp.view.ClienteView;
import bankapp.view.CuentaView;

import java.sql.Connection;

// Simple Factory: crea y conecta todos los objetos de la aplicacion
// Igual al patron usado en LuciaStore — un solo lugar para ensamblar todo
public class Config {

    public static MenuApp createMenuApp() {

        // Capa repository
        Connection connection = DataBaseConnectionMySQL.getInstance().getConnection();
        ClientePersistencePort clienteRepository = new ClienteRepositoryAdapterMySQL(connection);

        // Capa services
        ClienteService clienteService = new ClienteServiceImpl(clienteRepository);
        CuentaService cuentaService = new CuentaServiceImpl(clienteRepository);
        TarjetaCreditoService tarjetaService = new TarjetaCreditoServiceImpl();

        // Capa view
        ClienteView clienteView = new ClienteView(clienteService);
        CuentaView cuentaView = new CuentaView(cuentaService, tarjetaService, clienteService);
        AdminView adminView = new AdminView(clienteService);

        // Ensamblado final
        return new MenuApp(clienteService, clienteView, cuentaView, adminView);
    }
}
