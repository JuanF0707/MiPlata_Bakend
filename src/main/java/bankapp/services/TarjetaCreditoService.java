package bankapp.services;

import bankapp.domain.TarjetaCredito;

// Contrato especifico para operaciones de Tarjeta de Credito
public interface TarjetaCreditoService {

    boolean comprar(TarjetaCredito tarjeta);
    boolean pagar(TarjetaCredito tarjeta);
    boolean avance(TarjetaCredito tarjeta);
}
