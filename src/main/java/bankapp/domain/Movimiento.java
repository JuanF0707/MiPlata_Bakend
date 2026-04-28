package bankapp.domain;

import bankapp.domain.enums.TipoMovimiento;
import java.util.Date;

// Clase que representa un movimiento bancario registrado en una cuenta
public class Movimiento {

    private static int contadorId = 0;

    private int id;
    private TipoMovimiento tipo;
    private double valor;
    private Date fecha;
    private double saldoPosterior;
    private String descripcion;

    // Constructor
    public Movimiento(TipoMovimiento tipo, double valor, double saldoPosterior, String descripcion) {
        this.id = ++contadorId;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = new Date();
        this.saldoPosterior = saldoPosterior;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() { return id; }
    public TipoMovimiento getTipo() { return tipo; }
    public double getValor() { return valor; }
    public Date getFecha() { return fecha; }
    public double getSaldoPosterior() { return saldoPosterior; }
    public String getDescripcion() { return descripcion; }

    // Retorna el movimiento como texto para mostrar en pantalla
    public String mostrar() {
        return String.format("[%d] %s | $%.2f | Saldo: $%.2f | %s | %s",
                id, tipo, valor, saldoPosterior, descripcion, fecha);
    }
}
