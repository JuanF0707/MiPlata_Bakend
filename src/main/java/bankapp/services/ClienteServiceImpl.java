package bankapp.services;

import bankapp.domain.Cliente;
import bankapp.domain.CuentaAhorros;
import bankapp.domain.CuentaCorriente;
import bankapp.domain.TarjetaCredito;
import bankapp.repository.ClienteRepository;
import bankapp.services.outputport.ClientePersistencePort;
import bankapp.utils.AppScanner;
import bankapp.utils.BankFormValidation;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Implementacion de ClienteService: logica de negocio y captura de datos por consola
public class ClienteServiceImpl implements ClienteService {

    private final Scanner sc = AppScanner.get();
    private final ClientePersistencePort clienteRepository;
    private int nextId = 1;

    public ClienteServiceImpl(ClientePersistencePort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente registrarCliente() {

        System.out.println("Ingrese su numero de identificacion:");
        String identificacion = sc.nextLine().trim();

        System.out.println("Ingrese su nombre completo:");
        String nombre = sc.nextLine().trim();

        System.out.println("Ingrese su numero de celular:");
        String celular = sc.nextLine().trim();

        System.out.println("Ingrese su nombre de usuario:");
        String usuario = sc.nextLine().trim();

        if (clienteRepository.existeUsuario(usuario)) {
            System.out.println("Ese nombre de usuario ya existe. Elija otro.");
            return null;
        }

        String contrasena = BankFormValidation.validarContrasena("Ingrese su contrasena (min 8 caracteres, mayuscula, minuscula y numero):");

        System.out.println("Confirme su contrasena:");
        String confirmar = sc.nextLine().trim();

        if (!contrasena.equals(confirmar)) {
            System.out.println("Las contrasenas no coinciden.");
            return null;
        }

        // Crear cliente con id unico
        Cliente nuevoCliente = new Cliente(nextId++, identificacion, nombre, celular, usuario, contrasena);

        // Crear automaticamente los 3 productos bancarios
        String numAhorros   = BankFormValidation.generarNumeroCuenta();
        String numCorriente = BankFormValidation.generarNumeroCuenta();
        String numCredito   = BankFormValidation.generarNumeroCuenta();

        nuevoCliente.agregarCuenta(new CuentaAhorros(numAhorros, 0, 0.015));
        nuevoCliente.agregarCuenta(new CuentaCorriente(numCorriente, 0));
        nuevoCliente.agregarCuenta(new TarjetaCredito(numCredito, 5000000));

        return clienteRepository.saveCliente(nuevoCliente);
    }

    @Override
    public Optional<Cliente> getClienteById(int id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> getClienteByUsuario(String usuario) {
        return clienteRepository.findByUsuario(usuario);
    }

    @Override
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAllClientes();
    }

    @Override
    public Cliente updateCliente(Cliente cliente) {
        System.out.println("Ingrese el nuevo nombre:");
        String nombre = sc.nextLine().trim();

        System.out.println("Ingrese el nuevo numero de celular:");
        String celular = sc.nextLine().trim();

        cliente.editarPerfil(nombre, celular);
        return clienteRepository.updateCliente(cliente).orElse(null);
    }

    @Override
    public boolean deleteCliente(int id) {
        return clienteRepository.deleteById(id);
    }

    @Override
    public boolean cambiarContrasena(Cliente cliente) {
        System.out.println("Ingrese su contrasena actual:");
        String actual = sc.nextLine().trim();

        if (!cliente.getContrasena().equals(actual)) {
            System.out.println("La contrasena actual es incorrecta.");
            return false;
        }

        String nueva = BankFormValidation.validarContrasena("Ingrese la nueva contrasena (min 8 caracteres, mayuscula, minuscula y numero):");

        System.out.println("Confirme la nueva contrasena:");
        String confirmar = sc.nextLine().trim();

        if (!nueva.equals(confirmar)) {
            System.out.println("Las contrasenas no coinciden.");
            return false;
        }

        cliente.setContrasena(nueva);
        return true;
    }

    // Login: busca el cliente y lo autentica, maneja bloqueo
    @Override
    public Cliente iniciarSesion(String usuario, String contrasena) {
        Optional<Cliente> opt = clienteRepository.findByUsuario(usuario);
        if (opt.isEmpty()) {
            System.out.println("Usuario no encontrado.");
            return null;
        }
        Cliente cliente = opt.get();
        boolean autenticado = cliente.autenticar(usuario, contrasena);
        return autenticado ? cliente : null;
    }
}
