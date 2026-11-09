package com.redsegura;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de BFS, DFS, Dijkstra y del núcleo RedSeguraCore. */
class AlgorithmsTest {

    private MixedGraph graph;

    @BeforeEach
    void setUp() {
        graph = DataFactory.createGraph();
    }

    /** Grafo desconectado: A - B y C solo. */
    private MixedGraph disconnectedGraph() {
        MixedGraph g = new MixedGraph();
        g.addVertex("A", "Lugar A", "X");
        g.addVertex("B", "Lugar B", "X");
        g.addVertex("C", "Lugar C", "X");
        g.addEdge("E1", "A", "B", false, 5, "A-B");
        return g;
    }

    // ------------------------------------------------------------ BFS

    @Test
    void bfsFromS02VisitsEveryVertexByLevels() {
        BreadthFirstSearch bfs = graph.bfs("S02");
        List<String> order = bfs.getOrder();
        assertEquals(12, order.size());
        assertEquals(List.of("S02", "S01", "S06"), order.subList(0, 3));
        assertEquals(1, bfs.getLevel("S01"));
        assertEquals("S02", bfs.getParent("S01"));
        assertEquals(2, bfs.getLevel("S05")); // S02 -> S06 -> S05
    }

    @Test
    void bfsRespectsOrientation() {
        // En el grafo original, S06 solo tiene una salida: R06 hacia S05 (R05 llega, no sale)
        assertEquals(List.of("S05"), graph.getNeighbors("S06"));

        // Grafo pequeño A -> B: desde B no se puede volver a A
        MixedGraph g = new MixedGraph();
        g.addVertex("A", "A", "X");
        g.addVertex("B", "B", "X");
        g.addEdge("E1", "A", "B", true, 3, "arco");
        assertEquals(List.of("A", "B"), g.bfs("A").getOrder());
        assertFalse(g.bfs("B").reached("A"));
    }

    @Test
    void bfsInDisconnectedGraph() {
        BreadthFirstSearch bfs = disconnectedGraph().bfs("A");
        assertEquals(List.of("A", "B"), bfs.getOrder());
        assertFalse(bfs.reached("C"));
        assertEquals(-1, bfs.getLevel("C"));
    }

    @Test
    void bfsWithNonexistentVertex() {
        assertThrows(IllegalArgumentException.class, () -> graph.bfs("S99"));
    }

    // ------------------------------------------------------------ DFS

    @Test
    void dfsFromS02GoesDeepFirst() {
        DepthFirstSearch dfs = graph.dfs("S02");
        List<String> order = dfs.getOrder();
        assertEquals(12, order.size());
        assertEquals(List.of("S02", "S01", "S07", "S09"), order.subList(0, 4));
        assertEquals("S06", order.get(11)); // S06 queda de último
    }

    @Test
    void dfsWithNonexistentVertex() {
        assertThrows(IllegalArgumentException.class, () -> graph.dfs("S99"));
    }

    @Test
    void cyclesAreDetected() {
        assertTrue(DepthFirstSearch.hasCycle(graph)); // S01 -> S07 -> S01

        MixedGraph chain = new MixedGraph();
        chain.addVertex("A", "A", "X");
        chain.addVertex("B", "B", "X");
        chain.addVertex("C", "C", "X");
        chain.addEdge("E1", "A", "B", true, 1, "");
        chain.addEdge("E2", "B", "C", false, 1, ""); // ir y volver por la misma arista no es ciclo
        assertFalse(DepthFirstSearch.hasCycle(chain));

        chain.addEdge("E3", "C", "A", true, 1, "");
        assertTrue(DepthFirstSearch.hasCycle(chain));
    }

    @Test
    void weakComponents() {
        assertEquals(1, DepthFirstSearch.weakComponents(graph).size());
        assertEquals(2, DepthFirstSearch.weakComponents(disconnectedGraph()).size());
    }

    // ------------------------------------------------------------ Dijkstra

    @Test
    void dijkstraS02ToS08Is24Minutes() {
        DijkstraSolver d = graph.dijkstra("S02");
        assertEquals(24, d.distanceTo("S08"));
        assertEquals(List.of("S02", "S01", "S07", "S08"), d.pathTo("S08"));
        assertEquals(List.of("R04", "R01", "R16"), d.edgesTo("S08"));
    }

    @Test
    void dijkstraWithR16Blocked() {
        graph.blockEdge("R16");
        DijkstraSolver d = graph.dijkstra("S02");
        assertEquals(24, d.distanceTo("S08"));
        assertEquals(List.of("S02", "S01", "S09", "S10", "S08"), d.pathTo("S08"));
    }

    @Test
    void dijkstraWithoutRoute() {
        // A S08 solo se llega por R09 y R16. Si se bloquean las dos, no hay ruta.
        graph.blockEdge("R16");
        graph.blockEdge("R09");
        DijkstraSolver d = graph.dijkstra("S02");
        assertEquals(-1, d.distanceTo("S08"));
        assertTrue(d.pathTo("S08").isEmpty());
    }

    @Test
    void dijkstraAfterWeightChange() {
        graph.setWeight("R04", 2); // S02 - S01 ahora cuesta 2
        assertEquals(18, graph.dijkstra("S02").distanceTo("S08"));
    }

    @Test
    void dijkstraInDisconnectedGraph() {
        DijkstraSolver d = disconnectedGraph().dijkstra("A");
        assertEquals(5, d.distanceTo("B"));
        assertEquals(-1, d.distanceTo("C"));
    }

    @Test
    void dijkstraWithNonexistentVertex() {
        assertThrows(IllegalArgumentException.class, () -> graph.dijkstra("S99"));
        assertThrows(IllegalArgumentException.class, () -> graph.dijkstra("S02").distanceTo("S99"));
    }

    // ------------------------------------------------------------ Core

    @Test
    void coreAnswersTheControlQueries() {
        RedSeguraCore core = new RedSeguraCore();
        assertEquals(24, core.distance("S02", "S08"));
        String q6 = core.blockAndRecalculate("R16", "S02", "S08");
        assertTrue(q6.contains("S02 -> S01 -> S09 -> S10 -> S08 = 24 min"));
        assertThrows(IllegalArgumentException.class, () -> core.vertexInfo("S99")); // Q7
        core.reset();
        assertEquals(List.of("S02", "S01", "S07", "S08"), core.path("S02", "S08"));
    }
}
