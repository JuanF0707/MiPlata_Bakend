package bankapp.domain;

import bankapp.domain.enums.EstadoCuenta;
import bankapp.domain.enums.TipoMovimiento;

// Cuenta de ahorros: retiro cobra 1.5% de interes
public class CuentaAhorros extends Cuenta {

    private double tasaInteres;

    // Constructor
    public CuentaAhorros(String numeroCuenta, double saldo, double tasaInteres) {
        super(numeroCuenta, saldo);
        this.tasaInteres = tasaInteres;
    }

    // Getters
    public double getTasaInteres() { return tasaInteres; }

    // Retiro: cobra un 1.5% de interes sobre el valor retirado
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
        double interes = valor * 0.015;
        double totalADescontar = valor + interes;

        if (totalADescontar > saldo) {
            System.out.printf("Saldo insuficiente. Recuerda que se cobra un 1.5%% de interes. Total a descontar: $%.2f%n", totalADescontar);
            return false;
        }
        saldo -= totalADescontar;
        movimientos.add(new Movimiento(TipoMovimiento.RETIRO, valor, saldo,
                String.format("Retiro: $%.2f + Interes: $%.2f", valor, interes)));
        return true;
    }

    // Aplica el interes mensual al saldo
    public void aplicarInteres() {
        if (estado == EstadoCuenta.ACTIVA) {
            double interes = saldo * tasaInteres;
            saldo += interes;
            movimientos.add(new Movimiento(TipoMovimiento.INTERES, interes, saldo, "Interes aplicado"));
        } else {
            System.out.println("La cuenta no esta activa. No se puede aplicar interes.");
        }
    }

    // Calcula el interes sin aplicarlo
    public double calcularInteres() {
        return saldo * tasaInteres;
    }

    // Transfiere dinero a otra cuenta (cobra 1.5% de interes sobre el valor, igual que un retiro)
    public boolean transferir(double valor, Cuenta cuentaDestino) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta no esta activa. No se puede transferir.");
            return false;
        }
        if (!validarDestino(cuentaDestino)) return false;
        double interes = valor * 0.015;
        double totalADescontar = valor + interes;
        if (totalADescontar > saldo) {
            System.out.printf("Saldo insuficiente. Recuerda que se cobra un 1.5%% de interes. Total a descontar: $%.2f%n", totalADescontar);
            return false;
        }
        saldo -= totalADescontar;
        cuentaDestino.recibirTransferencia(valor);
        movimientos.add(new Movimiento(TipoMovimiento.TRANSFERENCIA_OUT, valor, saldo,
                String.format("Transferencia: $%.2f + Interes: $%.2f", valor, interes)));
        return true;
    }
}
