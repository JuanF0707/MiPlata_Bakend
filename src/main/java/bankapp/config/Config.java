package bankapp.config;

import bankapp.persistence.database.DataBaseConnectionMySQL;
import bankapp.persistence.repository.ClienteRepositoryAdapterMySQL;
import bankapp.persistence.repository.CuentaRepositoryAdapterMySQL;
import bankapp.persistence.repository.MovimientoRepositoryAdapterMySQL;
import bankapp.services.*;
import bankapp.services.outputport.ClientePersistencePort;
import bankapp.services.outputport.CuentaPersistencePort;
import bankapp.services.outputport.MovimientoPersistencePort;
import bankapp.userinterface.MenuApp;
import bankapp.view.AdminView;
import bankapp.view.ClienteView;
import bankapp.view.CuentaView;

import java.sql.Connection;

// Simple Factory: crea y conecta todos los objetos de la aplicacion
// Igual al patron usado en LuciaStore — un solo lugar para ensamblar todo
public class Config {

    public static MenuApp createMenuApp() {

        // Conexión a la BDD
        Connection connection = DataBaseConnectionMySQL.getInstance().getConnection();

        // Capa repository
        ClientePersistencePort clienteRepository = new ClienteRepositoryAdapterMySQL(connection);
        CuentaPersistencePort cuentaRepository = new CuentaRepositoryAdapterMySQL(connection);
        MovimientoPersistencePort movimientoRepository = new MovimientoRepositoryAdapterMySQL(connection);


        // Capa services
        ClienteService clienteService = new ClienteServiceImpl(clienteRepository);
        CuentaService cuentaService = new CuentaServiceImpl(clienteRepository, movimientoRepository, cuentaRepository);
        TarjetaCreditoService tarjetaService = new TarjetaCreditoServiceImpl(movimientoRepository, cuentaRepository);

        // Capa view
        ClienteView clienteView = new ClienteView(clienteService);
        CuentaView cuentaView = new CuentaView(cuentaService, tarjetaService, clienteService);
        AdminView adminView = new AdminView(clienteService);

        // Ensamblado final
        return new MenuApp(clienteService, clienteView, cuentaView, adminView);
    }
}
