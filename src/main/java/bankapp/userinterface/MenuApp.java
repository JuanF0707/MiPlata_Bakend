package bankapp.userinterface;

import bankapp.domain.Cliente;
import bankapp.domain.Cuenta;
import bankapp.domain.TarjetaCredito;
import bankapp.services.ClienteService;
import bankapp.utils.AppScanner;
import bankapp.utils.BankFormValidation;
import bankapp.view.AdminView;
import bankapp.view.ClienteView;
import bankapp.view.CuentaView;

import java.util.Scanner;

// Clase que maneja toda la navegacion de menus de la aplicacion
public class MenuApp {

    private final ClienteService clienteService;
    private final ClienteView clienteView;
    private final CuentaView cuentaView;
    private final AdminView adminView;
    private final Scanner sc = AppScanner.get();

    // Credenciales fijas del administrador
    private static final String ADMIN_USUARIO = "admin";
    private static final String ADMIN_CONTRASENA = "admin123";

    public MenuApp(ClienteService clienteService, ClienteView clienteView,
                   CuentaView cuentaView, AdminView adminView) {
        this.clienteService = clienteService;
        this.clienteView = clienteView;
        this.cuentaView = cuentaView;
        this.adminView = adminView;
    }

    // Punto de entrada: menu principal
    public void iniciar() {
        System.out.println("Bienvenido al Banco Mi Plata");

        boolean activo = true;
        while (activo) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    flujoIniciarSesion();
                    break;
                case 2:
                    clienteView.registrarCliente();
                    break;
                case 3:
                    flujoLoginAdmin();
                    break;
                case 4:
                    activo = false;
                    System.out.println("Gracias por usar Mi Plata. Hasta luego!");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
    }

    // Muestra el menu principal
    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("=== BANCO MI PLATA ===");
        System.out.println("1. Iniciar sesion");
        System.out.println("2. Registrarse");
        System.out.println("3. Acceso administrador");
        System.out.println("4. Salir");
        System.out.print("Elija una opcion: ");
    }

    // Flujo de inicio de sesion del cliente con reintentos hasta bloqueo
    private void flujoIniciarSesion() {
        System.out.println();
        System.out.println("--- Inicio de sesion ---");
        System.out.println("Ingrese su usuario:");
        String usuario = sc.nextLine().trim();

        // Verificar si el usuario existe antes de pedir contrasena
        java.util.Optional<Cliente> optCliente = clienteService.getClienteByUsuario(usuario);
        if (optCliente.isEmpty()) {
            System.out.println("El usuario '" + usuario + "' no existe.");
            return;
        }

        Cliente cliente = optCliente.get();

        if (cliente.isBloqueado()) {
            System.out.println("Cuenta bloqueada por 24 horas. Comunicate con tu banco.");
            return;
        }

        // Reintentar contrasena hasta que acierte o se bloquee
        while (!cliente.isBloqueado()) {
            int intentoActual = cliente.getIntentosFallidos() + 1;
            System.out.println("Intento " + intentoActual + " de 3");
            System.out.println("Ingrese su contrasena:");
            String contrasena = sc.nextLine().trim();

            Cliente autenticado = clienteService.iniciarSesion(usuario, contrasena);
            if (autenticado != null) {
                System.out.println("Bienvenido, " + autenticado.getNombre() + "!");
                menuCliente(autenticado);
                return;
            }

            if (cliente.isBloqueado()) {
                System.out.println("Cuenta bloqueada por 24 horas. Comunicate con tu banco.");
            }
        }
    }

    // Menu principal del cliente autenticado
    private void menuCliente(Cliente cliente) {
        boolean enSesion = true;
        while (enSesion) {
            mostrarMenuCliente(cliente);
            int opcion = leerOpcion();

            switch (opcion) {
                case 1: cuentaView.consultarSaldo(cliente); break;
                case 2: cuentaView.consignar(cliente); break;
                case 3: cuentaView.retirar(cliente); break;
                case 4: cuentaView.transferir(cliente); break;
                case 5: cuentaView.transferirEntrePropio(cliente); break;
                case 6: cuentaView.verMovimientos(cliente); break;
                case 7: menuTarjetaCredito(cliente); break;
                case 8:
                    clienteView.updateCliente(cliente);
                    break;
                case 9:
                    enSesion = false;
                    System.out.println("Sesion cerrada. Hasta pronto, " + cliente.getNombre() + "!");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // Muestra el menu del cliente
    private void mostrarMenuCliente(Cliente cliente) {
        System.out.println();
        System.out.println("=== MENU CLIENTE ===");
        System.out.println("Usuario: " + cliente.getUsuario());
        System.out.println("----------------------------");
        System.out.println("1. Consultar saldo");
        System.out.println("2. Consignar");
        System.out.println("3. Retirar");
        System.out.println("4. Transferir a otro cliente");
        System.out.println("5. Transferir entre mis cuentas");
        System.out.println("6. Ver movimientos");
        System.out.println("7. Menu Tarjeta de Credito");
        System.out.println("8. Editar mi perfil");
        System.out.println("9. Cerrar sesion");
        System.out.print("Elija una opcion: ");
    }

    // Menu especifico de Tarjeta de Credito
    private void menuTarjetaCredito(Cliente cliente) {
        boolean activo = true;
        while (activo) {
            System.out.println();
            System.out.println("=== TARJETA DE CREDITO ===");
            System.out.println("1. Comprar");
            System.out.println("2. Pagar TC");
            System.out.println("3. Avance en efectivo");
            System.out.println("4. Ver movimientos TC");
            System.out.println("5. Volver");
            System.out.print("Elija una opcion: ");

            int opcion = leerOpcion();
            switch (opcion) {
                case 1: cuentaView.comprarTC(cliente); break;
                case 2: cuentaView.pagarTC(cliente); break;
                case 3: cuentaView.avanceTC(cliente); break;
                case 4: cuentaView.verMovimientosTC(cliente); break;
                case 5: activo = false; break;
                default: System.out.println("Opcion no valida.");
            }
        }
    }

    // Flujo de login del administrador
    private void flujoLoginAdmin() {
        System.out.println();
        System.out.println("--- Acceso Administrador ---");
        System.out.println("Usuario:");
        String usuario = sc.nextLine().trim();
        System.out.println("Contrasena:");
        String contrasena = sc.nextLine().trim();

        if (ADMIN_USUARIO.equals(usuario) && ADMIN_CONTRASENA.equals(contrasena)) {
            System.out.println("Bienvenido, Administrador!");
            menuAdmin();
        } else {
            System.out.println("Credenciales de administrador incorrectas.");
        }
    }

    // Menu del administrador
    private void menuAdmin() {
        boolean activo = true;
        while (activo) {
            System.out.println();
            System.out.println("=== MENU ADMINISTRADOR ===");
            System.out.println("1. Ver todos los clientes");
            System.out.println("2. Buscar cliente por ID");
            System.out.println("3. Registrar nuevo cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Desbloquear cliente");
            System.out.println("6. Salir del panel admin");
            System.out.print("Elija una opcion: ");

            int opcion = leerOpcion();
            switch (opcion) {
                case 1: adminView.verTodosLosClientes(); break;
                case 2: adminView.buscarClientePorId(); break;
                case 3: clienteView.registrarCliente(); break;
                case 4: adminView.eliminarCliente(); break;
                case 5: adminView.desbloquearCliente(); break;
                case 6:
                    activo = false;
                    System.out.println("Saliendo del panel de administrador...");
                    break;
                default: System.out.println("Opcion no valida.");
            }
        }
    }

    // Lee una opcion del menu con manejo de errores
    private int leerOpcion() {
        try {
            int opcion = Integer.parseInt(sc.nextLine().trim());
            return opcion;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
