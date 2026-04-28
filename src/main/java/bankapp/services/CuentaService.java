package bankapp.services;

import bankapp.domain.Cuenta;
import bankapp.domain.Cliente;

import java.util.List;

// Contrato que define las operaciones sobre cuentas bancarias
public interface CuentaService {

    boolean consignar(Cuenta cuenta);
    boolean retirar(Cuenta cuenta);
    boolean transferir(Cuenta origen, Cliente clienteDestino);
    void verMovimientos(Cuenta cuenta);
    double consultarSaldo(Cuenta cuenta);
}
