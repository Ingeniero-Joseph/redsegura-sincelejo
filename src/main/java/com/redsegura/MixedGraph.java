package com.redsegura;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grafo mixto: tiene conexiones dirigidas (arcos) y no dirigidas (aristas) en la misma estructura.
 *
 * Se guarda con una lista de adyacencia:
 *   adjacency.get("S01") = conexiones que tocan a S01 y por las que se podría salir de S01.
 * Un arco se guarda solo en la lista de su origen; una arista se guarda en la lista de sus dos extremos.
 *
 * Se usa LinkedHashMap para que los vértices y conexiones conserven el orden en que se agregaron
 * (S01..S12 y R01..R18). Así las matrices y los recorridos siempre salen en el mismo orden.
 */
public class MixedGraph {

    private final Map<String, Vertex> vertices = new LinkedHashMap<>();
    private final Map<String, Edge> edges = new LinkedHashMap<>();
    private final Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    /** Agrega un vértice. Error si el ID ya existe. O(1). */
    public void addVertex(String id, String label, String type) {
        if (vertices.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un vértice con el ID " + id + ".");
        }
        vertices.put(id, new Vertex(id, label, type));
        adjacency.put(id, new ArrayList<>());
    }

    /** Agrega una conexión. Error si el ID ya existe, si un vértice no existe o si el peso es inválido. O(1). */
    public void addEdge(String id, String origin, String destination, boolean directed, int weight, String label) {
        if (edges.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe una conexión con el ID " + id + ".");
        }
        getVertex(origin);       // lanza error si no existe
        getVertex(destination);  // lanza error si no existe

        Edge edge = new Edge(id, origin, destination, directed, weight, label);
        edges.put(id, edge);
        adjacency.get(origin).add(edge);
        if (!directed) {
            adjacency.get(destination).add(edge); // la arista también sale desde el destino
        }
    }

    /** Busca un vértice por ID. Error con mensaje claro si no existe. */
    public Vertex getVertex(String id) {
        Vertex v = vertices.get(id);
        if (v == null) {
            throw new IllegalArgumentException("El vértice " + id + " no existe en el grafo.");
        }
        return v;
    }

    /** Busca una conexión por ID. Error con mensaje claro si no existe. */
    public Edge getEdge(String id) {
        Edge e = edges.get(id);
        if (e == null) {
            throw new IllegalArgumentException("La conexión " + id + " no existe en el grafo.");
        }
        return e;
    }

    public boolean hasVertex(String id) {
        return vertices.containsKey(id);
    }

    /** Copia de la lista de vértices (así nadie modifica el mapa interno). */
    public List<Vertex> getVertices() {
        return new ArrayList<>(vertices.values());
    }

    /** Copia de todas las conexiones, incluidas las bloqueadas. */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges.values());
    }

    /** Conexiones que no están bloqueadas. */
    public List<Edge> getActiveEdges() {
        List<Edge> result = new ArrayList<>();
        for (Edge e : edges.values()) {
            if (!e.isBlocked()) {
                result.add(e);
            }
        }
        return result;
    }

    /** Conexiones por las que se puede salir de un vértice (respeta orientación y bloqueos). O(grado). */
    public List<Edge> getOutgoingEdges(String id) {
        getVertex(id);
        List<Edge> result = new ArrayList<>();
        for (Edge e : adjacency.get(id)) {
            if (e.canGoFrom(id)) {
                result.add(e);
            }
        }
        return result;
    }

    /** IDs de los vértices a los que se puede llegar en un paso desde id. O(grado). */
    public List<String> getNeighbors(String id) {
        List<String> result = new ArrayList<>();
        for (Edge e : getOutgoingEdges(id)) {
            result.add(e.other(id));
        }
        return result;
    }

    public void blockEdge(String id) {
        getEdge(id).setBlocked(true);
    }

    public void unblockEdge(String id) {
        getEdge(id).setBlocked(false);
    }

    public void setWeight(String edgeId, int weight) {
        getEdge(edgeId).setWeight(weight);
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    // ----- El grafo "proporciona" los algoritmos (contrato de integración) -----

    public BreadthFirstSearch bfs(String start) {
        return new BreadthFirstSearch(this, start);
    }

    public DepthFirstSearch dfs(String start) {
        return new DepthFirstSearch(this, start);
    }

    public DijkstraSolver dijkstra(String origin) {
        return new DijkstraSolver(this, origin);
    }

    public GraphRepresentations representations() {
        return new GraphRepresentations(this);
    }
}
