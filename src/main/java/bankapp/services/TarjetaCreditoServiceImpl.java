package bankapp.services;

import bankapp.domain.TarjetaCredito;
import bankapp.utils.BankFormValidation;

// Implementacion de TarjetaCreditoService: logica de compras, pagos y avances
public class TarjetaCreditoServiceImpl implements TarjetaCreditoService {

    @Override
    public boolean comprar(TarjetaCredito tarjeta) {
        System.out.printf("Cupo disponible: $%.2f | Deuda actual: $%.2f%n",
                tarjeta.getSaldoDisponible(), tarjeta.getDeuda());

        double valor = BankFormValidation.validarDouble("Ingrese el valor de la compra: $");
        int cuotas  = BankFormValidation.validarInt("Ingrese el numero de cuotas (1-36):");

        if (cuotas < 1 || cuotas > 36) {
            System.out.println("Numero de cuotas no valido. Debe ser entre 1 y 36.");
            return false;
        }

        double cuotaMensual = tarjeta.calcularCuotaMensual(valor, cuotas);
        System.out.printf("Cuota mensual estimada: $%.2f%n", cuotaMensual);

        return tarjeta.comprar(valor, cuotas);
    }

    @Override
    public boolean pagar(TarjetaCredito tarjeta) {
        System.out.printf("Deuda actual: $%.2f%n", tarjeta.getDeuda());
        double valor = BankFormValidation.validarDouble("Ingrese el valor del pago: $");
        boolean exito = tarjeta.pagar(valor);
        if (exito) {
            System.out.printf("Pago exitoso. Deuda restante: $%.2f%n", tarjeta.getDeuda());
        }
        return exito;
    }

    @Override
    public boolean avance(TarjetaCredito tarjeta) {
        System.out.printf("Cupo disponible: $%.2f%n", tarjeta.getSaldoDisponible());
        double valor = BankFormValidation.validarDouble("Ingrese el valor del avance: $");
        int cuotas   = BankFormValidation.validarInt("Ingrese el numero de cuotas:");
        return tarjeta.avance(valor, cuotas);
    }
}
