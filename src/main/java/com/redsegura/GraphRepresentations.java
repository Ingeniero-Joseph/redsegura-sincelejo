package com.redsegura;

import java.util.ArrayList;
import java.util.List;

/**
 * Las tres representaciones del grafo. Solo se usan las conexiones activas (no bloqueadas)
 * y siempre el mismo orden: vértices S01..S12 y conexiones R01..R18.
 *
 * - Lista de adyacencia: cada vértice con las conexiones por las que puede salir.
 * - Matriz de adyacencia ponderada (V x V): m[i][j] = minutos de i a j, 0 si no hay conexión.
 *   Un arco llena solo m[origen][destino]; una arista llena las dos casillas.
 * - Matriz de incidencia (V x E): en la columna de cada conexión:
 *   arco   -> +1 en el origen y -1 en el destino
 *   arista ->  1 en los dos extremos
 */
public class GraphRepresentations {

    private final MixedGraph graph;
    private final List<String> vertexIds = new ArrayList<>();

    public GraphRepresentations(MixedGraph graph) {
        this.graph = graph;
        for (Vertex v : graph.getVertices()) {
            vertexIds.add(v.getId());
        }
    }

    /** Matriz de adyacencia ponderada. Tiempo O(V² + E), espacio O(V²). */
    public int[][] adjacencyMatrix() {
        int n = vertexIds.size();
        int[][] m = new int[n][n];
        for (Edge e : graph.getActiveEdges()) {
            int o = vertexIds.indexOf(e.getOrigin());
            int d = vertexIds.indexOf(e.getDestination());
            m[o][d] = e.getWeight();
            if (!e.isDirected()) {
                m[d][o] = e.getWeight();
            }
        }
        return m;
    }

    /** Matriz de incidencia. Tiempo O(V·E), espacio O(V·E). */
    public int[][] incidenceMatrix() {
        List<Edge> edges = graph.getActiveEdges();
        int[][] m = new int[vertexIds.size()][edges.size()];
        for (int col = 0; col < edges.size(); col++) {
            Edge e = edges.get(col);
            int o = vertexIds.indexOf(e.getOrigin());
            int d = vertexIds.indexOf(e.getDestination());
            if (e.isDirected()) {
                m[o][col] = 1;
                m[d][col] = -1;
            } else {
                m[o][col] = 1;
                m[d][col] = 1;
            }
        }
        return m;
    }

    /** Texto de la lista de adyacencia. Tiempo O(V + E). */
    public String adjacencyListText() {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : graph.getVertices()) {
            sb.append(String.format("%-4s %-24s -> ", v.getId(), v.getLabel()));
            List<Edge> salidas = graph.getOutgoingEdges(v.getId());
            if (salidas.isEmpty()) {
                sb.append("(sin salidas)");
            }
            for (Edge e : salidas) {
                sb.append(e.other(v.getId())).append("(").append(e.getWeight()).append(" min, ")
                        .append(e.getId()).append(")  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String adjacencyMatrixText() {
        int[][] m = adjacencyMatrix();
        StringBuilder sb = new StringBuilder("     ");
        for (String id : vertexIds) {
            sb.append(String.format("%4s", id));
        }
        sb.append("\n");
        for (int i = 0; i < m.length; i++) {
            sb.append(String.format("%-5s", vertexIds.get(i)));
            for (int j = 0; j < m[i].length; j++) {
                sb.append(String.format("%4s", m[i][j] == 0 ? "." : m[i][j]));
            }
            sb.append("\n");
        }
        sb.append("(fila = origen, columna = destino, '.' = sin conexión)\n");
        return sb.toString();
    }

    public String incidenceMatrixText() {
        int[][] m = incidenceMatrix();
        List<Edge> edges = graph.getActiveEdges();
        StringBuilder sb = new StringBuilder("     ");
        for (Edge e : edges) {
            sb.append(String.format("%4s", e.getId()));
        }
        sb.append("\n");
        for (int i = 0; i < m.length; i++) {
            sb.append(String.format("%-5s", vertexIds.get(i)));
            for (int j = 0; j < edges.size(); j++) {
                sb.append(String.format("%4s", m[i][j] == 0 ? "." : m[i][j]));
            }
            sb.append("\n");
        }
        sb.append("(arco: +1 origen y -1 destino; arista: 1 en ambos extremos)\n");
        return sb.toString();
    }
}
