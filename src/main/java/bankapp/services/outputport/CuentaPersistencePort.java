package bankapp.services.outputport;

import bankapp.domain.Cuenta;
import java.util.List;

public interface CuentaPersistencePort {
    Cuenta saveCuenta(Cuenta cuenta, int clienteId);
    List<Cuenta> findByClienteId(int clienteId);
    boolean updateSaldo(int cuentaId, double nuevoSaldo);
    boolean deleteByClienteId(int clienteId);
}
