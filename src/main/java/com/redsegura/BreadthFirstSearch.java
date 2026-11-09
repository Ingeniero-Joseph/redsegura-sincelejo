package com.redsegura;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Búsqueda por anchura (BFS).
 * Usa una COLA: primero visita todos los vecinos del origen (nivel 1), luego los vecinos
 * de esos (nivel 2), y así sucesivamente. Solo sigue los arcos en su sentido.
 *
 * Además guarda el árbol BFS: para cada vértice, desde qué vértice se descubrió (su padre).
 *
 * Complejidad: tiempo O(V + E) porque cada vértice entra una vez a la cola y cada conexión
 * se revisa una vez. Espacio O(V) por la cola, los visitados y el árbol.
 */
public class BreadthFirstSearch {

    private final String start;
    private final List<String> order = new ArrayList<>();
    private final Map<String, String> parent = new LinkedHashMap<>();
    private final Map<String, Integer> level = new LinkedHashMap<>();

    public BreadthFirstSearch(MixedGraph graph, String start) {
        graph.getVertex(start); // lanza error si el vértice no existe
        this.start = start;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(start);
        level.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            order.add(current);
            for (String next : graph.getNeighbors(current)) {
                if (!visited.contains(next)) {
                    visited.add(next); // se marca al encolar para no encolarlo dos veces
                    parent.put(next, current);
                    level.put(next, level.get(current) + 1);
                    queue.add(next);
                }
            }
        }
    }

    /** Orden en que se visitaron los vértices. */
    public List<String> getOrder() {
        return new ArrayList<>(order);
    }

    /** Nivel (número de saltos desde el origen). -1 si no se alcanzó. */
    public int getLevel(String vertexId) {
        return level.getOrDefault(vertexId, -1);
    }

    /** Padre en el árbol BFS. null para el origen o si no se alcanzó. */
    public String getParent(String vertexId) {
        return parent.get(vertexId);
    }

    public boolean reached(String vertexId) {
        return level.containsKey(vertexId);
    }

    /** Árbol BFS como texto, una línea por vértice. */
    public String treeText() {
        StringBuilder sb = new StringBuilder();
        for (String v : order) {
            if (v.equals(start)) {
                sb.append("  ").append(v).append(" (raíz, nivel 0)\n");
            } else {
                sb.append("  ").append(v).append(" <- descubierto desde ").append(parent.get(v))
                        .append(" (nivel ").append(level.get(v)).append(")\n");
            }
        }
        return sb.toString();
    }
}
