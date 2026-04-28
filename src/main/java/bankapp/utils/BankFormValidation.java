package bankapp.utils;

import java.util.Random;
import java.util.Scanner;

// Utilidades para validar entradas del usuario y generar datos del sistema
public class BankFormValidation {

    private static final Scanner sc = AppScanner.get();

    // Valida que la entrada sea un entero positivo
    public static int validarInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor > 0) return valor;
                System.out.println("El valor debe ser mayor a cero.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero entero.");
            }
        }
    }

    // Valida que la entrada sea un double positivo
    public static double validarDouble(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                double valor = Double.parseDouble(sc.nextLine().trim());
                if (valor > 0) return valor;
                System.out.println("El valor debe ser mayor a cero.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
            }
        }
    }

    // Valida que el texto no sea vacio
    public static String validarTexto(String prompt) {
        while (true) {
            System.out.println(prompt);
            String valor = sc.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("Este campo no puede estar vacio.");
        }
    }

    // Genera un numero de cuenta aleatorio de 10 digitos
    public static String generarNumeroCuenta() {
        Random random = new Random();
        long numero = (long)(random.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(numero);
    }
}
