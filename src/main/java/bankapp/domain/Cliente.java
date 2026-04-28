package bankapp.domain;

import java.util.ArrayList;
import java.util.List;

// Clase que representa un cliente del banco
// Tiene sus datos personales, credenciales y lista de cuentas
public class Cliente {

    private int id;
    private String identificacion;
    private String nombre;
    private String celular;
    private String usuario;
    private String contrasena;
    private int intentosFallidos;
    private boolean bloqueado;
    private List<Cuenta> cuentas;

    // Constructor
    public Cliente(int id, String identificacion, String nombre, String celular, String usuario, String contrasena) {
        this.id = id;
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.celular = celular;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.intentosFallidos = 0;
        this.bloqueado = false;
        this.cuentas = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getCelular() { return celular; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public boolean isBloqueado() { return bloqueado; }
    public List<Cuenta> getCuentas() { return cuentas; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    // Agrega una cuenta al cliente
    public void agregarCuenta(Cuenta cuenta) {
        cuentas.add(cuenta);
    }

    // Busca una cuenta por numero
    public Cuenta obtenerCuenta(String numeroCuenta) {
        for (Cuenta c : cuentas) {
            if (c.getNumeroCuenta().equals(numeroCuenta)) {
                return c;
            }
        }
        return null;
    }

    // Elimina una cuenta por numero
    public boolean eliminarCuenta(String numeroCuenta) {
        return cuentas.removeIf(c -> c.getNumeroCuenta().equals(numeroCuenta));
    }

    // Autentica al cliente con usuario y contrasena
    public boolean autenticar(String usuario, String contrasena) {
        if (bloqueado) {
            System.out.println("Cuenta bloqueada por 24 horas. Comunicate con tu banco.");
            return false;
        }
        if (this.usuario.equals(usuario) && this.contrasena.equals(contrasena)) {
            intentosFallidos = 0;
            return true;
        } else {
            intentosFallidos++;
            if (intentosFallidos >= 3) {
                bloqueado = true;
                System.out.println("Demasiados intentos fallidos. Cuenta bloqueada.");
            } else {
                System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentosFallidos));
            }
            return false;
        }
    }

    // Bloquea la cuenta manualmente
    public void bloquearCuenta() {
        this.bloqueado = true;
    }

    // Desbloquea y resetea intentos (uso admin)
    public void resetearIntentos() {
        this.intentosFallidos = 0;
        this.bloqueado = false;
    }

    // Cambia la contrasena si la vieja es correcta
    public boolean cambiarContrasena(String nuevaContrasena, String viejaContrasena) {
        if (this.contrasena.equals(viejaContrasena)) {
            this.contrasena = nuevaContrasena;
            return true;
        }
        return false;
    }

    // Edita nombre y celular del perfil
    public void editarPerfil(String nombre, String celular) {
        this.nombre = nombre;
        this.celular = celular;
    }
}
