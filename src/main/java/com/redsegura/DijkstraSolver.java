package com.redsegura;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Ruta mínima con Dijkstra desde un vértice origen hacia todos los demás.
 *
 * Pasos:
 *  1. Todas las distancias empiezan en "infinito" (Integer.MAX_VALUE), menos el origen que es 0.
 *  2. La PriorityQueue siempre entrega el vértice con la menor distancia conocida.
 *  3. Relajación: para cada conexión u -> v, si dist[u] + peso < dist[v], se mejora dist[v]
 *     y se guarda en "previous" la conexión por la que se llegó a v.
 *  4. El camino se reconstruye desde el destino hacia atrás usando "previous".
 *
 * Como la comparación es estricta (<), si hay dos caminos con el mismo costo se queda el
 * primero que se encontró.
 *
 * Complejidad: tiempo O((V + E) log V) por la cola de prioridad; espacio O(V + E).
 * Solo funciona con pesos positivos (la clase Edge ya no deja crear pesos <= 0).
 */
public class DijkstraSolver {

    /** Elemento de la cola: un vértice con la distancia con la que se metió. */
    private static class QueueItem {
        String vertexId;
        int distance;

        QueueItem(String vertexId, int distance) {
            this.vertexId = vertexId;
            this.distance = distance;
        }
    }

    private final MixedGraph graph;
    private final String origin;
    private final Map<String, Integer> distance = new HashMap<>();
    private final Map<String, Edge> previous = new HashMap<>(); // mapa de predecesores

    public DijkstraSolver(MixedGraph graph, String origin) {
        graph.getVertex(origin); // lanza error si no existe
        this.graph = graph;
        this.origin = origin;
        run();
    }

    private void run() {
        for (Vertex v : graph.getVertices()) {
            distance.put(v.getId(), Integer.MAX_VALUE);
        }
        distance.put(origin, 0);

        PriorityQueue<QueueItem> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.distance, b.distance));
        Set<String> visited = new HashSet<>();
        queue.add(new QueueItem(origin, 0));

        while (!queue.isEmpty()) {
            QueueItem item = queue.poll();
            String u = item.vertexId;
            if (visited.contains(u)) {
                continue; // ya se procesó con una distancia menor
            }
            visited.add(u);

            for (Edge e : graph.getOutgoingEdges(u)) {
                String v = e.other(u);
                int newDistance = distance.get(u) + e.getWeight();
                if (newDistance < distance.get(v)) { // relajación
                    distance.put(v, newDistance);
                    previous.put(v, e);
                    queue.add(new QueueItem(v, newDistance));
                }
            }
        }
    }

    /** Minutos desde el origen hasta el destino, o -1 si no hay camino. */
    public int distanceTo(String destination) {
        graph.getVertex(destination);
        int d = distance.get(destination);
        return d == Integer.MAX_VALUE ? -1 : d;
    }

    public boolean hasPathTo(String destination) {
        return distanceTo(destination) != -1;
    }

    /** Vértices del camino desde el origen hasta el destino. Lista vacía si no hay camino. */
    public List<String> pathTo(String destination) {
        LinkedList<String> path = new LinkedList<>();
        if (!hasPathTo(destination)) {
            return path;
        }
        String current = destination;
        while (!current.equals(origin)) {
            path.addFirst(current);
            current = previous.get(current).other(current); // se retrocede por la conexión guardada
        }
        path.addFirst(origin);
        return path;
    }

    /** IDs de las conexiones usadas en el camino. */
    public List<String> edgesTo(String destination) {
        LinkedList<String> edges = new LinkedList<>();
        if (!hasPathTo(destination)) {
            return edges;
        }
        String current = destination;
        while (!current.equals(origin)) {
            Edge e = previous.get(current);
            edges.addFirst(e.getId());
            current = e.other(current);
        }
        return edges;
    }
}
