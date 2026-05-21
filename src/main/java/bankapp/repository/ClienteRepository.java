package bankapp.repository;

import bankapp.domain.Cliente;
import bankapp.services.outputport.ClientePersistencePort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Capa de acceso a datos: guarda y busca clientes en Array
public class ClienteRepository implements ClientePersistencePort {

    private List<Cliente> clientes = new ArrayList<>();

    // Guarda un cliente nuevo
    public Cliente saveCliente(Cliente cliente) {
        clientes.add(cliente);
        return cliente;
    }

    // Retorna todos los clientes
    public List<Cliente> findAllClientes() {
        return clientes;
    }

    // Busca un cliente por id
    public Optional<Cliente> findById(int id) {
        for (Cliente c : clientes) {
            if (c.getId() == id) return Optional.of(c);
        }
        return Optional.empty();
    }

    // Busca un cliente por nombre de usuario
    public Optional<Cliente> findByUsuario(String usuario) {
        for (Cliente c : clientes) {
            if (c.getUsuario().equalsIgnoreCase(usuario)) return Optional.of(c);
        }
        return Optional.empty();
    }

    // Verifica si ya existe un usuario con ese nombre
    public boolean existeUsuario(String usuario) {
        return findByUsuario(usuario).isPresent();
    }

    // Elimina un cliente por id
    public boolean deleteById(int id) {
        return clientes.removeIf(c -> c.getId() == id);
    }

    // Actualiza un cliente (devuelve el cliente si lo encontro)
    public Optional<Cliente> updateCliente(Cliente clienteActualizado) {
        for (Cliente c : clientes) {
            if (c.getId() == clienteActualizado.getId()) {
                return Optional.of(clienteActualizado);
            }
        }
        return Optional.empty();
    }
}

// Push Rama: CAJ-10
