package com.redsegura;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Búsqueda por profundidad (DFS), versión recursiva.
 * Desde un vértice se va al primer vecino no visitado y se sigue bajando hasta que no
 * haya salida; ahí se devuelve (backtracking) y prueba el siguiente vecino.
 *
 * También tiene dos métodos estáticos:
 *  - hasCycle: dice si el grafo tiene un ciclo respetando la orientación.
 *  - weakComponents: componentes débilmente conexas (ignorando la dirección de los arcos).
 *
 * Complejidad: tiempo O(V + E), espacio O(V) por los visitados y la pila de recursión.
 */
public class DepthFirstSearch {

    private final MixedGraph graph;
    private final List<String> order = new ArrayList<>();
    private final Map<String, String> parent = new LinkedHashMap<>();
    private final Set<String> visited = new HashSet<>();

    public DepthFirstSearch(MixedGraph graph, String start) {
        graph.getVertex(start); // lanza error si no existe
        this.graph = graph;
        visit(start);
    }

    private void visit(String current) {
        visited.add(current);
        order.add(current);
        for (String next : graph.getNeighbors(current)) {
            if (!visited.contains(next)) {
                parent.put(next, current);
                visit(next);
            }
        }
    }

    public List<String> getOrder() {
        return new ArrayList<>(order);
    }

    public String getParent(String vertexId) {
        return parent.get(vertexId);
    }

    public boolean reached(String vertexId) {
        return visited.contains(vertexId);
    }

    // ------------------------------------------------------------ ciclos

    /**
     * Dice si hay un ciclo. Se hace un DFS guardando los vértices del camino actual (inPath).
     * Si se llega a un vértice que ya está en el camino actual, hay ciclo.
     * Para una arista (no dirigida) no se cuenta como ciclo devolverse por la MISMA arista.
     */
    public static boolean hasCycle(MixedGraph graph) {
        Set<String> visited = new HashSet<>();
        for (Vertex v : graph.getVertices()) {
            if (!visited.contains(v.getId())) {
                if (findCycle(graph, v.getId(), null, visited, new HashSet<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findCycle(MixedGraph graph, String current, Edge arrivedBy,
                                     Set<String> visited, Set<String> inPath) {
        visited.add(current);
        inPath.add(current);
        for (Edge e : graph.getOutgoingEdges(current)) {
            if (e == arrivedBy) {
                continue; // no devolverse por la misma arista
            }
            String next = e.other(current);
            if (inPath.contains(next)) {
                return true;
            }
            if (!visited.contains(next) && findCycle(graph, next, e, visited, inPath)) {
                return true;
            }
        }
        inPath.remove(current);
        return false;
    }

    // ------------------------------------------------------------ componentes débiles

    /**
     * Componentes débilmente conexas: grupos de vértices que quedan unidos si se ignora
     * la dirección de los arcos. Se usa un DFS iterativo con una pila.
     */
    public static List<List<String>> weakComponents(MixedGraph graph) {
        // 1. Se arma una lista de vecinos sin dirección (cada conexión activa en los dos sentidos)
        Map<String, List<String>> undirected = new LinkedHashMap<>();
        for (Vertex v : graph.getVertices()) {
            undirected.put(v.getId(), new ArrayList<>());
        }
        for (Edge e : graph.getActiveEdges()) {
            undirected.get(e.getOrigin()).add(e.getDestination());
            undirected.get(e.getDestination()).add(e.getOrigin());
        }

        // 2. DFS desde cada vértice no visitado: cada DFS encuentra una componente
        List<List<String>> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (String startId : undirected.keySet()) {
            if (visited.contains(startId)) {
                continue;
            }
            List<String> component = new ArrayList<>();
            Stack<String> stack = new Stack<>();
            stack.push(startId);
            visited.add(startId);
            while (!stack.isEmpty()) {
                String current = stack.pop();
                component.add(current);
                for (String next : undirected.get(current)) {
                    if (!visited.contains(next)) {
                        visited.add(next);
                        stack.push(next);
                    }
                }
            }
            components.add(component);
        }
        return components;
    }
}
