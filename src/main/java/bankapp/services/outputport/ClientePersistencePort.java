package bankapp.services.outputport;

import bankapp.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClientePersistencePort {

    Cliente saveCliente(Cliente cliente);
    List<Cliente> findAllClientes();
    Optional<Cliente> findById(int id);
    Optional<Cliente> findByUsuario(String usuario);
    boolean existeUsuario(String usuario);
    boolean deleteById(int id);
    Optional<Cliente> updateCliente(Cliente clienteActualizado);


}
