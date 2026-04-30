package bankapp.view;

import bankapp.domain.*;
import bankapp.services.ClienteService;
import bankapp.services.CuentaService;
import bankapp.services.TarjetaCreditoService;
import bankapp.utils.AppScanner;
import bankapp.utils.BankFormValidation;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Vista que conecta el menu de transacciones con CuentaService y TarjetaCreditoService
public class CuentaView {

    private final CuentaService cuentaService;
    private final TarjetaCreditoService tarjetaService;
    private final ClienteService clienteService;
    private final Scanner sc = AppScanner.get();

    public CuentaView(CuentaService cuentaService, TarjetaCreditoService tarjetaService, ClienteService clienteService) {
        this.cuentaService = cuentaService;
        this.tarjetaService = tarjetaService;
        this.clienteService = clienteService;
    }

    // Muestra el selector de cuenta y retorna la elegida
    public Cuenta seleccionarCuenta(Cliente cliente) {
        List<Cuenta> cuentas = cliente.getCuentas();
        System.out.println("--- Tus productos bancarios ---");
        for (int i = 0; i < cuentas.size(); i++) {
            Cuenta c = cuentas.get(i);
            String tipo = c.getClass().getSimpleName();
            String info = "";
            if (c instanceof TarjetaCredito) {
                TarjetaCredito tc = (TarjetaCredito) c;
                info = String.format("Cupo: $%.2f | Deuda: $%.2f | Disponible: $%.2f",
                        tc.getCupo(), tc.getDeuda(), tc.getSaldoDisponible());
            } else {
                info = String.format("Saldo: $%.2f", c.getSaldo());
            }
            System.out.printf("%d. %s | N° %s | %s%n", (i + 1), tipo, c.getNumeroCuenta(), info);
        }

        int opcion = BankFormValidation.validarInt("Seleccione un producto:") - 1;
        if (opcion < 0 || opcion >= cuentas.size()) {
            System.out.println("Opcion no valida.");
            return null;
        }
        return cuentas.get(opcion);
    }

    // Consignar en la cuenta seleccionada
    public void consignar(Cliente cliente) {
        Cuenta cuenta = seleccionarCuenta(cliente);
        if (cuenta == null) return;
        if (cuenta instanceof TarjetaCredito) {
            System.out.println("No se puede consignar directamente en una Tarjeta de Credito. Use 'Pagar TC'.");
            return;
        }
        cuentaService.consignar(cuenta);
    }

    // Retirar de la cuenta seleccionada
    public void retirar(Cliente cliente) {
        Cuenta cuenta = seleccionarCuenta(cliente);
        if (cuenta == null) return;
        if (cuenta instanceof TarjetaCredito) {
            System.out.println("Para avances en efectivo use la opcion 'Avance TC'.");
            return;
        }
        cuentaService.retirar(cuenta);
    }

    // Transferir desde la cuenta seleccionada
    public void transferir(Cliente clienteOrigen) {
        System.out.println("--- Transferencia ---");
        Cuenta origen = seleccionarCuenta(clienteOrigen);
        if (origen == null) return;

        System.out.println("Ingrese el usuario del destinatario:");
        String usuarioDestino = sc.nextLine().trim();

        Optional<Cliente> optDestino = clienteService.getClienteByUsuario(usuarioDestino);
        if (optDestino.isEmpty()) {
            System.out.println("Usuario destinatario no encontrado.");
            return;
        }

        Cliente clienteDestino = optDestino.get();
        if (clienteDestino.getUsuario().equalsIgnoreCase(clienteOrigen.getUsuario())) {
            System.out.println("No puedes transferirte a ti mismo desde este menu. Use 'Transferir entre mis cuentas'.");
            return;
        }

        cuentaService.transferir(origen, clienteDestino);
    }

    // Transferir entre cuentas propias
    public void transferirEntrePropio(Cliente cliente) {
        System.out.println("--- Transferencia entre mis cuentas ---");
        System.out.println("Cuenta ORIGEN:");
        Cuenta origen = seleccionarCuenta(cliente);
        if (origen == null) return;

        System.out.println("Cuenta DESTINO:");
        cuentaService.transferir(origen, cliente);
    }

    // Ver movimientos de una cuenta
    public void verMovimientos(Cliente cliente) {
        Cuenta cuenta = seleccionarCuenta(cliente);
        if (cuenta == null) return;
        cuentaService.verMovimientos(cuenta);
    }

    // Consultar saldo de una cuenta
    public void consultarSaldo(Cliente cliente) {
        Cuenta cuenta = seleccionarCuenta(cliente);
        if (cuenta == null) return;
        cuentaService.consultarSaldo(cuenta);
    }

    // Comprar con tarjeta de credito
    public void comprarTC(Cliente cliente) {
        TarjetaCredito tc = obtenerTarjetaCredito(cliente);
        if (tc == null) return;
        tarjetaService.comprar(tc);
    }

    // Pagar tarjeta de credito
    public void pagarTC(Cliente cliente) {
        TarjetaCredito tc = obtenerTarjetaCredito(cliente);
        if (tc == null) return;
        tarjetaService.pagar(tc);
    }

    // Avance en efectivo con tarjeta de credito
    public void avanceTC(Cliente cliente) {
        TarjetaCredito tc = obtenerTarjetaCredito(cliente);
        if (tc == null) return;
        tarjetaService.avance(tc);
    }

    // Ver movimientos directamente de la tarjeta de credito
    public void verMovimientosTC(Cliente cliente) {
        TarjetaCredito tc = obtenerTarjetaCredito(cliente);
        if (tc == null) return;
        cuentaService.verMovimientos(tc);
    }

    // Busca la tarjeta de credito del cliente
    private TarjetaCredito obtenerTarjetaCredito(Cliente cliente) {
        for (Cuenta c : cliente.getCuentas()) {
            if (c instanceof TarjetaCredito) return (TarjetaCredito) c;
        }
        System.out.println("No tienes una Tarjeta de Credito registrada.");
        return null;
    }
}

// push rama CAJ-16