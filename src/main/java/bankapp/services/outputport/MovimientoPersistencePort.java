package bankapp.services.outputport;

import bankapp.domain.Movimiento;

import java.util.List;

public interface MovimientoPersistencePort {

    void save (Movimiento movimiento, int cuentaId);
    List<Movimiento> findByCuentaId(int cuentaId);
    void actualizarSaldo (int cuentaId, double nuevoSaldo);



}
