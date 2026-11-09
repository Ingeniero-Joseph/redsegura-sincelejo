package com.redsegura;

import java.util.Scanner;

/**
 * Aplicación de consola RedSegura. Muestra un menú y llama a RedSeguraCore.
 * Si algo sale mal (ID inexistente, peso inválido, número mal escrito) se muestra
 * el error y el programa sigue funcionando.
 *
 * Con el argumento --demo ejecuta Q1 a Q7 de una vez y termina.
 */
public class RedSeguraApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static final RedSeguraCore core = new RedSeguraCore();

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            runDemo();
            return;
        }

        boolean salir = false;
        while (!salir) {
            printMenu();
            if (!scanner.hasNextLine()) {
                break; // se cerró la entrada
            }
            String opcion = scanner.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> System.out.println(core.searchInTrees(ask("Lugar a buscar", "Hospital Universitario")));
                    case "2" -> System.out.println(core.rotationsDemo());
                    case "3" -> System.out.println(core.representations());
                    case "4" -> System.out.println(core.traversals(ask("Vértice inicial", "S02")));
                    case "5" -> System.out.println(core.shortestPath(ask("Origen", "S02"), ask("Destino", "S08")));
                    case "6" -> System.out.println(core.blockAndRecalculate(ask("Conexión a bloquear", "R16"),
                            ask("Origen", "S02"), ask("Destino", "S08")));
                    case "7" -> System.out.println(core.vertexInfo(ask("ID del vértice", "S99")));
                    case "8" -> {
                        String id = ask("Conexión", "R16");
                        int peso = Integer.parseInt(ask("Nuevo peso en minutos", "20"));
                        core.changeWeight(id, peso);
                        System.out.println("Peso de " + id + " cambiado a " + peso + " min.");
                    }
                    case "9" -> {
                        String id = ask("Conexión a desbloquear", "R16");
                        core.unblockEdge(id);
                        System.out.println("Conexión " + id + " desbloqueada.");
                    }
                    case "10" -> System.out.println(core.componentsAndCycles());
                    case "11" -> System.out.println(core.benchmark(2000));
                    case "12" -> {
                        core.reset();
                        System.out.println("Datos originales restaurados.");
                    }
                    case "13" -> runDemo();
                    case "0" -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException e) {
                // también atrapa NumberFormatException (es subclase)
                System.out.println("[ERROR] " + e.getMessage() + " El programa continúa.");
            }
        }
        System.out.println("Fin de RedSegura.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========== RedSegura Sincelejo ==========");
        System.out.println(" 1. Q1 Buscar un lugar en el ABB y en el AVL");
        System.out.println(" 2. Q2 Rotaciones AVL (LL, RR, LR, RL)");
        System.out.println(" 3. Q3 Lista, matriz de adyacencia e incidencia");
        System.out.println(" 4. Q4 BFS y DFS");
        System.out.println(" 5. Q5 Ruta mínima (Dijkstra)");
        System.out.println(" 6. Q6 Bloquear conexión y recalcular");
        System.out.println(" 7. Q7 Consultar un vértice");
        System.out.println(" 8. Cambiar el peso de una conexión");
        System.out.println(" 9. Desbloquear una conexión");
        System.out.println("10. Componentes débiles y ciclos");
        System.out.println("11. Medición de tiempos ABB vs AVL");
        System.out.println("12. Restaurar datos originales");
        System.out.println("13. Demo completa Q1 a Q7");
        System.out.println(" 0. Salir");
        System.out.print("Opción: ");
    }

    /** Pide un dato. Si el usuario solo presiona Enter se usa el valor por defecto. */
    private static String ask(String mensaje, String porDefecto) {
        System.out.print(mensaje + " [" + porDefecto + "]: ");
        if (!scanner.hasNextLine()) {
            return porDefecto;
        }
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? porDefecto : texto;
    }

    private static void runDemo() {
        title("Q1 - Buscar 'Hospital Universitario' en ABB y AVL");
        System.out.println(core.searchInTrees("Hospital Universitario"));

        title("Q2 - Las cuatro rotaciones AVL");
        System.out.println(core.rotationsDemo());

        title("Q3 - Representaciones del grafo");
        System.out.println(core.representations());

        title("Q4 - BFS y DFS desde S02");
        System.out.println(core.traversals("S02"));

        title("Q5 - Dijkstra de S02 a S08");
        System.out.println(core.shortestPath("S02", "S08"));

        title("Q6 - Bloquear R16 y recalcular");
        System.out.println(core.blockAndRecalculate("R16", "S02", "S08"));
        core.reset();

        title("Q7 - Consultar el vértice inexistente S99");
        try {
            System.out.println(core.vertexInfo("S99"));
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage() + " El programa continúa.");
        }
    }

    private static void title(String texto) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(texto);
        System.out.println("==================================================");
    }
}
