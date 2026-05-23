package bankapp.services;

import bankapp.domain.*;
import bankapp.persistence.repository.MovimientoRepositoryAdapterMySQL;
import bankapp.services.outputport.ClientePersistencePort;
import bankapp.utils.AppScanner;
import bankapp.utils.BankFormValidation;

import java.util.List;
import java.util.Scanner;

public class CuentaServiceImpl implements CuentaService {

    private final Scanner sc = AppScanner.get();
    private final ClientePersistencePort clienteRepository;
    private final MovimientoRepositoryAdapterMySQL movimientoRepository;

    public CuentaServiceImpl(ClientePersistencePort clienteRepository, MovimientoRepositoryAdapterMySQL movimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public boolean consignar(Cuenta cuenta) {
        double valor = BankFormValidation.validarDouble("Cuanto desea consignar? $");
        boolean exito = cuenta.consignar(valor);
            if (exito) {
                System.out.printf("Consignacion exitosa por $%.2f. Nuevo saldo: $%.2f%n", valor, cuenta.getSaldo());
                Movimiento m = cuenta.getMovimientos().get(cuenta.getMovimientos().size()-1);
                movimientoRepository.save(m, cuenta.getId());
            }

        return exito;
    }

    @Override
    public boolean retirar(Cuenta cuenta) {
        double valor = BankFormValidation.validarDouble("Cuanto desea retirar? $");
        boolean exito = cuenta.retirar(valor);
        if (exito) {
            System.out.printf("Retiro exitoso por $%.2f. Nuevo saldo: $%.2f%n", valor, cuenta.getSaldo());
            Movimiento m = cuenta.getMovimientos().get(cuenta.getMovimientos().size()-1);
            movimientoRepository.save(m, cuenta.getId());
        }
        return exito;
    }

    @Override
    public boolean transferir(Cuenta origen, Cliente clienteDestino) {
        System.out.println("--- Cuentas disponibles del destinatario ---");
        List<Cuenta> cuentasDestino = clienteDestino.getCuentas();
        for (int i = 0; i < cuentasDestino.size(); i++) {
            Cuenta c = cuentasDestino.get(i);
            System.out.println((i + 1) + ". " + c.getClass().getSimpleName() + " | N° " + c.getNumeroCuenta());
        }

        int opcion = BankFormValidation.validarInt("Seleccione la cuenta destino:") - 1;
        if (opcion < 0 || opcion >= cuentasDestino.size()) {
            System.out.println("Opcion no valida.");
            return false;
        }
        Cuenta destino = cuentasDestino.get(opcion);
        double valor = BankFormValidation.validarDouble("Cuanto desea transferir? $");

        boolean exito = false;
        if (origen instanceof CuentaAhorros) {
            exito = ((CuentaAhorros) origen).transferir(valor, destino);
        } else if (origen instanceof CuentaCorriente) {
            exito = ((CuentaCorriente) origen).transferir(valor, destino);
        } else if (origen instanceof TarjetaCredito) {
            exito = ((TarjetaCredito) origen).transferir(valor, destino);
        }

        if (exito) {
            System.out.printf("Transferencia exitosa de $%.2f a la cuenta %s%n", valor, destino.getNumeroCuenta());
        }
        return exito;
    }

    @Override
    public void verMovimientos(Cuenta cuenta) {
        List<Movimiento> movimientos = cuenta.obtenerMovimientos();
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
            return;
        }
        System.out.println("--- Historial de movimientos ---");
        for (int i = 0; i < movimientos.size(); i++) {
            System.out.println((i + 1) + ". " + movimientos.get(i).mostrar());
        }
        System.out.println("Total: " + movimientos.size() + " movimientos.");
    }

    @Override
    public double consultarSaldo(Cuenta cuenta) {
        double saldo = cuenta.consultarSaldo();
        System.out.printf("Saldo actual: $%.2f%n", saldo);
        return saldo;
    }
}
