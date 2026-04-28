package bankapp.view;

import bankapp.domain.Cliente;
import bankapp.services.ClienteService;
import bankapp.utils.AppScanner;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Vista que conecta el menu con las operaciones del ClienteService
public class ClienteView {

    private final ClienteService clienteService;
    private final Scanner sc = AppScanner.get();

    public ClienteView(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Inicia el flujo de registro de un nuevo cliente
    public Cliente registrarCliente() {
        System.out.println();
        System.out.println("--- Registro de nuevo cliente ---");
        Cliente cliente = clienteService.registrarCliente();
        if (cliente != null) {
            System.out.println("Cliente " + cliente.getNombre() + " registrado correctamente!");
            System.out.println("Se crearon automaticamente sus 3 productos bancarios.");
        }
        return cliente;
    }

    // Muestra los datos de un cliente por id
    public void getClienteById(int id) {
        Optional<Cliente> opt = clienteService.getClienteById(id);
        if (opt.isPresent()) {
            Cliente c = opt.get();
            System.out.println("ID: " + c.getId());
            System.out.println("Nombre: " + c.getNombre());
            System.out.println("Celular: " + c.getCelular());
            System.out.println("Usuario: " + c.getUsuario());
            System.out.println("Estado: " + (c.isBloqueado() ? "BLOQUEADO" : "ACTIVO"));
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    // Inicia el flujo de actualizacion de perfil
    public void updateCliente(Cliente cliente) {
        System.out.println("--- Actualizar perfil ---");
        clienteService.updateCliente(cliente);
        System.out.println("Perfil actualizado correctamente.");
    }

    // Elimina un cliente por id
    public void deleteCliente(int id) {
        boolean exito = clienteService.deleteCliente(id);
        System.out.println(exito ? "Cliente eliminado correctamente." : "Cliente no encontrado.");
    }

    // Muestra todos los clientes registrados
    public void getAllClientes() {
        List<Cliente> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("--- Lista de clientes ---");
        for (Cliente c : clientes) {
            System.out.printf("[%d] %s | Usuario: %s | Celular: %s | Estado: %s%n",
                    c.getId(), c.getNombre(), c.getUsuario(), c.getCelular(),
                    c.isBloqueado() ? "BLOQUEADO" : "ACTIVO");
        }
    }
}
