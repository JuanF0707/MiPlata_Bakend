package bankapp.domain;

import bankapp.domain.enums.EstadoCuenta;
import bankapp.domain.enums.TipoMovimiento;

// Cuenta corriente: permite sobregiro del 20% sobre el saldo actual
public class CuentaCorriente extends Cuenta {

    // Constructor
    public CuentaCorriente(String numeroCuenta, double saldo) {
        super(numeroCuenta, saldo);
    }

    // Retiro: permite retirar hasta el 120% del saldo (20% de sobregiro)
    @Override
    public boolean retirar(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta no esta activa. No se puede retirar.");
            return false;
        }
        if (valor <= 0) {
            System.out.println("El valor debe ser mayor a cero.");
            return false;
        }
        double saldoDisponible = saldo * 1.20;
        if (valor > saldoDisponible) {
            System.out.printf("Saldo insuficiente. Saldo disponible con sobregiro (20%%): $%.2f%n", saldoDisponible);
            return false;
        }
        saldo -= valor;
        movimientos.add(new Movimiento(TipoMovimiento.RETIRO, valor, saldo, "Retiro realizado"));
        return true;
    }

    // Calcula los intereses de sobregiro si el saldo es negativo
    public double calcularInteresSobregiro() {
        if (saldo < 0) {
            return Math.abs(saldo) * 0.025;
        }
        return 0;
    }

    // Transfiere dinero a otra cuenta respetando el limite de sobregiro
    public boolean transferir(double valor, Cuenta cuentaDestino) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta no esta activa. No se puede transferir.");
            return false;
        }
        if (!validarDestino(cuentaDestino)) return false;
        double saldoDisponible = saldo * 1.20;
        if (valor > saldoDisponible) {
            System.out.printf("Saldo insuficiente para transferir. Disponible con sobregiro (20%%): $%.2f%n", saldoDisponible);
            return false;
        }
        saldo -= valor;
        cuentaDestino.recibirTransferencia(valor);
        movimientos.add(new Movimiento(TipoMovimiento.TRANSFERENCIA_OUT, valor, saldo, "Transferencia realizada"));
        return true;
    }
}
