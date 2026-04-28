package bankapp.utils;

import java.util.Scanner;

// Scanner singleton compartido por toda la aplicacion
// Evita conflictos entre multiples instancias de Scanner sobre System.in
public class AppScanner {

    private static final Scanner sc = new Scanner(System.in);

    public static Scanner get() {
        return sc;
    }
}
