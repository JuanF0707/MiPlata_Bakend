package bankapp.services;

import bankapp.domain.Movimiento;
import bankapp.domain.TarjetaCredito;
import bankapp.services.outputport.MovimientoPersistencePort;
import bankapp.utils.BankFormValidation;

// Implementacion de TarjetaCreditoService: logica de compras, pagos y avances
public class TarjetaCreditoServiceImpl implements TarjetaCreditoService {


    private final MovimientoPersistencePort movimientoRepository;

    public TarjetaCreditoServiceImpl(MovimientoPersistencePort movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

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

        //Si la compra es exitosa, se corre esta linea que guarda el codigo en MySQL
        boolean exito = tarjeta.comprar(valor, cuotas);
        if (exito) {
            Movimiento m = tarjeta.getMovimientos().get(tarjeta.getMovimientos().size() - 1);
            movimientoRepository.save(m, tarjeta.getId());
            movimientoRepository.actualizarSaldo(tarjeta.getId(), tarjeta.getDeuda());
        }
        return exito;

    }
    @Override
    public boolean pagar(TarjetaCredito tarjeta) {
        System.out.printf("Deuda actual: $%.2f%n", tarjeta.getDeuda());
        double valor = BankFormValidation.validarDouble("Ingrese el valor del pago: $");
        boolean exito = tarjeta.pagar(valor);
        // Agrego linea para guardar en MySQL
        if (exito) {
            System.out.printf("Pago exitoso. Deuda restante: $%.2f%n", tarjeta.getDeuda());
            Movimiento m = tarjeta.getMovimientos().get(tarjeta.getMovimientos().size()-1);
            movimientoRepository.save(m, tarjeta.getId());
            movimientoRepository.actualizarSaldo(tarjeta.getId(), tarjeta.getDeuda());
        }
        return exito;
    }

    @Override
    public boolean avance(TarjetaCredito tarjeta) {
        System.out.printf("Cupo disponible: $%.2f%n", tarjeta.getSaldoDisponible());
        double valor = BankFormValidation.validarDouble("Ingrese el valor del avance: $");
        int cuotas   = BankFormValidation.validarInt("Ingrese el numero de cuotas:");

        //Agrego linea para guardar en MySQL
        boolean exito = tarjeta.avance(valor, cuotas);
        if(exito){
            Movimiento m = tarjeta.getMovimientos().get(tarjeta.getMovimientos().size()-1);
            movimientoRepository.save(m, tarjeta.getId());
            movimientoRepository.actualizarSaldo(tarjeta.getId(), tarjeta.getDeuda());
        }
        return exito;
    }
}
