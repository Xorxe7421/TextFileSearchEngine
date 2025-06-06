package ge.gpavl;

import java.util.Scanner;

public class IOUtils {

    public static void print(Object object) {
        System.out.println(object);
    }

    public static String getUserInput(String prompt) {
        System.out.print(prompt);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
