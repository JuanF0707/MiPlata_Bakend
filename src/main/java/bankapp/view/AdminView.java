package bankapp.view;

import bankapp.domain.Cliente;
import bankapp.services.ClienteService;
import bankapp.utils.AppScanner;
import bankapp.utils.BankFormValidation;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Vista del panel de administrador: gestiona todos los clientes
public class AdminView {

    private final ClienteService clienteService;
    private final Scanner sc = AppScanner.get();

    public AdminView(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Lista todos los clientes registrados
    public void verTodosLosClientes() {
        List<Cliente> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("--- Lista de clientes ---");
        for (Cliente c : clientes) {
            System.out.printf("[%d] %s | Usuario: %s | Cel: %s | Estado: %s%n",
                    c.getId(), c.getNombre(), c.getUsuario(), c.getCelular(),
                    c.isBloqueado() ? "BLOQUEADO" : "ACTIVO");
        }
    }

    // Busca y muestra un cliente por id
    public void buscarClientePorId() {
        int id = BankFormValidation.validarInt("Ingrese el id del cliente:");
        Optional<Cliente> opt = clienteService.getClienteById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            System.out.println("--- Datos del cliente ---");
            System.out.println("ID:           " + c.getId());
            System.out.println("Identificacion: " + c.getIdentificacion());
            System.out.println("Nombre:       " + c.getNombre());
            System.out.println("Celular:      " + c.getCelular());
            System.out.println("Usuario:      " + c.getUsuario());
            System.out.println("Estado:       " + (c.isBloqueado() ? "BLOQUEADO" : "ACTIVO"));
            System.out.println("Cuentas:      " + c.getCuentas().size());
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    // Elimina un cliente por id
    public void eliminarCliente() {
        int id = BankFormValidation.validarInt("Ingrese el id del cliente a eliminar:");
        boolean exito = clienteService.deleteCliente(id);
        System.out.println(exito ? "Cliente eliminado correctamente." : "Cliente no encontrado.");
    }

    // Desbloquea la cuenta de un cliente
    public void desbloquearCliente() {
        int id = BankFormValidation.validarInt("Ingrese el id del cliente a desbloquear:");
        Optional<Cliente> opt = clienteService.getClienteById(id);
        if (opt.isPresent()) {
            opt.get().resetearIntentos();
            System.out.println("Cuenta desbloqueada correctamente.");
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }
}
