package bankapp.services;

import bankapp.domain.Cliente;

import java.util.List;
import java.util.Optional;

// Contrato que define las operaciones disponibles sobre un Cliente
public interface ClienteService {

    Cliente registrarCliente();
    Optional<Cliente> getClienteById(int id);
    Optional<Cliente> getClienteByUsuario(String usuario);
    List<Cliente> getAllClientes();
    Cliente updateCliente(Cliente cliente);
    boolean deleteCliente(int id);
    Cliente iniciarSesion(String usuario, String contrasena);
}
