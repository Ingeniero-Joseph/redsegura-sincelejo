package com.redsegura;

import java.util.List;

/**
 * Núcleo del sistema. Une el grafo con los dos árboles y responde las consultas Q1 a Q7.
 * La aplicación de consola solo habla con esta clase (contrato de integración).
 *
 * Los árboles usan como clave la etiqueta del lugar (ej. "Hospital Universitario")
 * y se llenan en orden de ID: S01, S02, ..., S12.
 */
public class RedSeguraCore {

    private MixedGraph graph;
    private BinarySearchTree bst;
    private AvlTree avl;

    public RedSeguraCore() {
        reset();
    }

    /** Vuelve a cargar los datos originales (sin bloqueos ni cambios de peso). */
    public void reset() {
        graph = DataFactory.createGraph();
        bst = new BinarySearchTree();
        avl = new AvlTree();
        for (Vertex v : graph.getVertices()) {
            bst.insert(v.getLabel(), v);
            avl.insert(v.getLabel(), v);
        }
    }

    public MixedGraph getGraph() {
        return graph;
    }

    // ------------------------------------------------------------ Q1

    public String searchInTrees(String label) {
        Vertex enAbb = bst.search(label);
        if (enAbb == null) {
            throw new IllegalArgumentException("No hay ningún lugar con la etiqueta '" + label + "'.");
        }
        int pasosAbb = bst.searchSteps(label);
        int pasosAvl = avl.searchSteps(label);
        return "Lugar encontrado: " + enAbb + " - " + enAbb.getType() + "\n"
                + "ABB: " + pasosAbb + " paso(s)   (altura del ABB = " + bst.height() + ")\n"
                + "AVL: " + pasosAvl + " paso(s)   (altura del AVL = " + avl.height() + ")\n"
                + "El AVL se mantiene balanceado, por eso su altura es menor o igual y la búsqueda\n"
                + "necesita igual o menos comparaciones que en el ABB.\n";
    }

    // ------------------------------------------------------------ Q2

    public String rotationsDemo() {
        String[][] casos = {
                {"LL", "30", "20", "10"},
                {"RR", "10", "20", "30"},
                {"LR", "30", "10", "20"},
                {"RL", "10", "30", "20"}
        };
        StringBuilder sb = new StringBuilder();
        for (String[] caso : casos) {
            AvlTree arbol = new AvlTree();
            arbol.insert(caso[1], null);
            arbol.insert(caso[2], null);
            arbol.insert(caso[3], null);
            sb.append("Caso ").append(caso[0]).append(": se insertan ")
                    .append(caso[1]).append(", ").append(caso[2]).append(", ").append(caso[3]).append("\n");
            sb.append("  Rotación: ").append(arbol.getRotations()).append("\n");
            sb.append("  Raíz final: ").append(arbol.getRootKey())
                    .append("   preorden: ").append(arbol.preOrder()).append("\n\n");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------ Q3

    public String representations() {
        GraphRepresentations r = graph.representations();
        return "--- Lista de adyacencia ---\n" + r.adjacencyListText()
                + "\n--- Matriz de adyacencia ponderada ---\n" + r.adjacencyMatrixText()
                + "\n--- Matriz de incidencia ---\n" + r.incidenceMatrixText();
    }

    // ------------------------------------------------------------ Q4

    public String traversals(String start) {
        BreadthFirstSearch bfs = graph.bfs(start);
        DepthFirstSearch dfs = graph.dfs(start);
        return "BFS desde " + start + ": " + String.join(" -> ", bfs.getOrder()) + "\n"
                + "DFS desde " + start + ": " + String.join(" -> ", dfs.getOrder()) + "\n\n"
                + "Árbol BFS:\n" + bfs.treeText() + "\n"
                + "¿Por qué el orden es diferente?\n"
                + "BFS usa una cola (FIFO): visita todos los vecinos de un vértice antes de pasar\n"
                + "al siguiente nivel. DFS usa recursión (como una pila, LIFO): se va por el primer\n"
                + "vecino hasta el fondo y solo se devuelve cuando no hay más salidas.\n";
    }

    // ------------------------------------------------------------ Q5

    public String shortestPath(String origin, String destination) {
        DijkstraSolver d = graph.dijkstra(origin);
        if (!d.hasPathTo(destination)) {
            return "No hay ruta de " + origin + " a " + destination + ".\n";
        }
        return "Ruta: " + String.join(" -> ", d.pathTo(destination))
                + "\nCosto: " + d.distanceTo(destination) + " minutos"
                + "\nConexiones usadas: " + d.edgesTo(destination) + "\n";
    }

    /** Para las pruebas: minutos de la ruta mínima (-1 si no hay). */
    public int distance(String origin, String destination) {
        return graph.dijkstra(origin).distanceTo(destination);
    }

    public List<String> path(String origin, String destination) {
        return graph.dijkstra(origin).pathTo(destination);
    }

    // ------------------------------------------------------------ Q6

    public String blockAndRecalculate(String edgeId, String origin, String destination) {
        DijkstraSolver antes = graph.dijkstra(origin);
        int costoAntes = antes.distanceTo(destination);
        List<String> conexionesAntes = antes.edgesTo(destination);

        graph.blockEdge(edgeId);

        DijkstraSolver despues = graph.dijkstra(origin);
        int costoDespues = despues.distanceTo(destination);

        StringBuilder sb = new StringBuilder();
        sb.append("Conexión bloqueada: ").append(graph.getEdge(edgeId)).append("\n");
        sb.append("Antes:   ").append(String.join(" -> ", antes.pathTo(destination)))
                .append(" = ").append(costoAntes).append(" min\n");

        if (costoDespues == -1) {
            sb.append("Después: no hay ruta de ").append(origin).append(" a ").append(destination).append("\n");
            sb.append("Explicación: al bloquear ").append(edgeId)
                    .append(" ya no queda ningún camino que respete la orientación de los arcos.\n");
            return sb.toString();
        }

        sb.append("Después: ").append(String.join(" -> ", despues.pathTo(destination)))
                .append(" = ").append(costoDespues).append(" min\n");
        sb.append("Explicación: ");
        if (!conexionesAntes.contains(edgeId)) {
            sb.append("la ruta no cambia porque no usaba la conexión ").append(edgeId).append(".\n");
        } else if (costoDespues == costoAntes) {
            sb.append("la ruta anterior usaba ").append(edgeId).append(". Dijkstra encontró otra ruta con el\n")
                    .append("mismo costo; antes no la escogía porque la relajación usa '<' y se queda con\n")
                    .append("el primer camino que encontró con ese costo.\n");
        } else {
            sb.append("la ruta anterior usaba ").append(edgeId).append(", así que Dijkstra tomó la siguiente\n")
                    .append("mejor alternativa, que cuesta ").append(costoDespues - costoAntes).append(" min más.\n");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------ Q7 y otras consultas

    /** Información de un vértice. Si no existe lanza IllegalArgumentException (la app la atrapa). */
    public String vertexInfo(String id) {
        Vertex v = graph.getVertex(id);
        return v + " - " + v.getType() + "\nSalidas: " + graph.getOutgoingEdges(id) + "\n";
    }

    public void blockEdge(String edgeId) {
        graph.blockEdge(edgeId);
    }

    public void unblockEdge(String edgeId) {
        graph.unblockEdge(edgeId);
    }

    public void changeWeight(String edgeId, int weight) {
        graph.setWeight(edgeId, weight);
    }

    public String componentsAndCycles() {
        List<List<String>> comps = DepthFirstSearch.weakComponents(graph);
        return "Componentes débilmente conexas: " + comps.size() + " -> " + comps + "\n"
                + "¿El grafo tiene ciclos (respetando la orientación)? "
                + (DepthFirstSearch.hasCycle(graph) ? "Sí (por ejemplo S01 -> S07 -> S01)" : "No") + "\n";
    }

    // ------------------------------------------------------------ medición

    /**
     * Medición reproducible: inserta n claves ORDENADAS (peor caso del ABB) en un ABB y en un AVL,
     * y mide cuánto tarda buscarlas todas. Siempre usa las mismas claves, así que se puede repetir.
     */
    public String benchmark(int n) {
        BinarySearchTree abb = new BinarySearchTree();
        AvlTree arbolAvl = new AvlTree();
        for (int i = 0; i < n; i++) {
            String clave = String.format("K%05d", i);
            abb.insert(clave, null);
            arbolAvl.insert(clave, null);
        }

        long inicio = System.nanoTime();
        for (int i = 0; i < n; i++) {
            abb.searchSteps(String.format("K%05d", i));
        }
        long tiempoAbb = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (int i = 0; i < n; i++) {
            arbolAvl.searchSteps(String.format("K%05d", i));
        }
        long tiempoAvl = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            graph.dijkstra("S02");
        }
        long tiempoDijkstra = (System.nanoTime() - inicio) / 1000;

        return "n = " + n + " claves insertadas en orden\n"
                + "ABB: altura " + abb.height() + ", buscar todas tomó " + tiempoAbb / 1_000_000.0 + " ms\n"
                + "AVL: altura " + arbolAvl.height() + ", buscar todas tomó " + tiempoAvl / 1_000_000.0 + " ms\n"
                + "Dijkstra desde S02 en el grafo de 12 vértices: " + tiempoDijkstra / 1000.0 + " microsegundos (promedio de 1000)\n"
                + "Conclusión: con claves ordenadas el ABB queda con altura n (O(n)) y el AVL con altura ~log2(n) (O(log n)).\n";
    }
}
