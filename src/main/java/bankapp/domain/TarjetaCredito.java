package bankapp.domain;

import bankapp.domain.enums.EstadoCuenta;
import bankapp.domain.enums.TipoMovimiento;

// Tarjeta de credito: maneja cupo, deuda, compras a cuotas y pagos
public class TarjetaCredito extends Cuenta {

    private double cupo;
    private double deuda;

    // Constructor
    public TarjetaCredito(String numeroCuenta, double cupo) {
        super(numeroCuenta, 0);
        this.cupo = cupo;
        this.deuda = 0;
    }

    // Getters
    public double getCupo() { return cupo; }
    public double getDeuda() { return deuda; }
    public double getSaldoDisponible() { return cupo - deuda; }

    // En TC, consignar directo no aplica — usar recibirTransferencia o pagar
    @Override
    public boolean consignar(double valor) {
        System.out.println("No se puede consignar directamente en una Tarjeta de Credito. Use 'Pagar TC'.");
        return false;
    }

    // Recibir transferencia hacia TC actua como pago parcial o total de la deuda
    @Override
    public boolean recibirTransferencia(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La tarjeta no esta activa.");
            return false;
        }
        double pagoReal = Math.min(valor, deuda);
        deuda -= pagoReal;
        movimientos.add(new Movimiento(TipoMovimiento.PAGO_TC, pagoReal, cupo - deuda, "Pago recibido por transferencia"));
        if (pagoReal < valor) {
            System.out.printf("La deuda era $%.2f. Solo se acredito ese monto; el excedente ($%.2f) no fue devuelto.%n",
                    pagoReal, valor - pagoReal);
        }
        return true;
    }

    // Avance en efectivo: usa cuotas para calcular la deuda total
    @Override
    public boolean retirar(double valor) {
        return avance(valor, 12);
    }

    // Avance en efectivo con numero de cuotas elegido
    public boolean avance(double valor, int cuotas) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La tarjeta no esta activa.");
            return false;
        }
        double disponible = cupo - deuda;
        if (valor > disponible) {
            System.out.println("Cupo insuficiente para el avance.");
            return false;
        }
        double tasa = 0.023;
        double cuotaMensual = (valor * tasa) / (1 - Math.pow(1 + tasa, -cuotas));
        double totalDeuda = cuotaMensual * cuotas;
        deuda += totalDeuda;
        movimientos.add(new Movimiento(TipoMovimiento.RETIRO, totalDeuda, cupo - deuda,
                String.format("Avance en efectivo a %d cuotas. Cuota: $%.2f", cuotas, cuotaMensual)));
        return true;
    }

    // Compra a cuotas
    public boolean comprar(double valor, int cuotas) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La tarjeta no esta activa.");
            return false;
        }
        double disponible = cupo - deuda;
        if (valor > disponible) {
            System.out.println("Cupo insuficiente.");
            return false;
        }
        double cuotaMensual = calcularCuotaMensual(valor, cuotas);
        deuda += cuotaMensual * cuotas;
        movimientos.add(new Movimiento(TipoMovimiento.COMPRA_TC, valor, cupo - deuda,
                String.format("Compra a %d cuotas. Cuota mensual: $%.2f", cuotas, cuotaMensual)));
        System.out.printf("Compra exitosa. Cuota mensual: $%.2f%n", cuotaMensual);
        return true;
    }

    // Pago directo a la deuda
    public boolean pagar(double valor) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La tarjeta no esta activa.");
            return false;
        }
        if (valor > deuda) {
            System.out.println("El valor del pago excede la deuda actual.");
            return false;
        }
        deuda -= valor;
        movimientos.add(new Movimiento(TipoMovimiento.PAGO_TC, valor, cupo - deuda, "Pago realizado"));
        return true;
    }

    // Calcula la tasa segun el numero de cuotas
    public double calcularTasaInteres(int cuotas) {
        if (cuotas <= 2) return 0;
        if (cuotas <= 6) return 0.019;
        return 0.023;
    }

    // Calcula la cuota mensual con la formula de amortizacion
    public double calcularCuotaMensual(double valor, int cuotas) {
        double tasa = calcularTasaInteres(cuotas);
        if (tasa == 0) return valor / cuotas;
        return (valor * tasa) / (1 - Math.pow(1 + tasa, -cuotas));
    }

    // Transferencia desde TC (aumenta deuda)
    public boolean transferir(double valor, Cuenta cuentaDestino) {
        if (estado != EstadoCuenta.ACTIVA) {
            System.out.println("La tarjeta no esta activa.");
            return false;
        }
        if (!validarDestino(cuentaDestino)) return false;
        double disponible = cupo - deuda;
        if (valor > disponible) {
            System.out.println("Cupo insuficiente para la transferencia.");
            return false;
        }
        deuda += valor;
        cuentaDestino.recibirTransferencia(valor);
        movimientos.add(new Movimiento(TipoMovimiento.TRANSFERENCIA_OUT, valor, cupo - deuda, "Transferencia realizada"));
        return true;
    }
}
