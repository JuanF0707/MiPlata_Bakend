package bankapp.domain;

import bankapp.domain.enums.EstadoCuenta;
import bankapp.domain.enums.TipoMovimiento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Cuenta {

    private Date fechaApertura;
    private String numeroCuenta;
    protected double saldo;
    protected EstadoCuenta estado;
    protected List<Movimiento> movimientos;

    // Constructor
    public Cuenta(String numeroCuenta, double saldo) {
        this.fechaApertura = new Date();
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.estado = EstadoCuenta.ACTIVA;
        this.movimientos = new ArrayList<>();
    }

    // Getters
    public Date getFechaApertura() { return fechaApertura; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public double getSaldo() { return saldo; }
    public EstadoCuenta getEstado() { return estado; }
    public List<Movimiento> getMovimientos() { return movimientos; }

    // Setters
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }

    // Consulta el saldo actual
    public double consultarSaldo() {
        return saldo;
    }

    // Consigna dinero a la cuenta
    public boolean consignar(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta no esta activa. No se puede consignar.");
            return false;
        }
        if (valor <= 0) {
            System.out.println("El valor debe ser mayor a cero.");
            return false;
        }
        saldo += valor;
        movimientos.add(new Movimiento(TipoMovimiento.CONSIGNACION, valor, saldo, "Consignacion realizada"));
        return true;
    }

    // Retira dinero de la cuenta
    public boolean retirar(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta no esta activa. No se puede retirar.");
            return false;
        }
        if (valor <= 0) {
            System.out.println("El valor debe ser mayor a cero.");
            return false;
        }
        if (valor > saldo) {
            System.out.println("Saldo insuficiente.");
            return false;
        }
        saldo -= valor;
        movimientos.add(new Movimiento(TipoMovimiento.RETIRO, valor, saldo, "Retiro realizado"));
        return true;
    }

    // Valida que la cuenta destino sea valida para transferir
    public boolean validarDestino(Cuenta cuentaDestino) {
        if (this.numeroCuenta.equals(cuentaDestino.getNumeroCuenta())) {
            System.out.println("No se puede transferir al mismo producto.");
            return false;
        }
        if (cuentaDestino.getEstado() != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta destino no esta activa.");
            return false;
        }
        return true;
    }

    public boolean recibirTransferencia(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La cuenta destino no esta activa.");
            return false;
        }
        saldo += valor;
        movimientos.add(new Movimiento(TipoMovimiento.TRANSFERENCIA_IN, valor, saldo, "Transferencia recibida"));
        return true;
    }

    // Retorna la lista de movimientos
    public List<Movimiento> obtenerMovimientos() {
        return movimientos;
    }
}

//push Rama CAJ-4