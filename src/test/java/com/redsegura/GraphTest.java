package com.redsegura;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del modelo (Vertex, Edge), del grafo mixto y de sus representaciones. */
class GraphTest {

    private MixedGraph graph;

    @BeforeEach
    void setUp() {
        graph = DataFactory.createGraph();
    }

    @Test
    void datasetIsLoaded() {
        assertEquals(12, graph.vertexCount());
        assertEquals(18, graph.edgeCount());
        assertEquals("Hospital Universitario", graph.getVertex("S05").getLabel());
        assertTrue(graph.getEdge("R01").isDirected());
        assertFalse(graph.getEdge("R03").isDirected());
    }

    @Test
    void duplicateIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> graph.addVertex("S01", "Otro", "X"));
        assertThrows(IllegalArgumentException.class,
                () -> graph.addEdge("R01", "S01", "S02", true, 3, "Repetida"));
    }

    @Test
    void nonexistentVertexOrEdgeIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> graph.getVertex("S99"));
        assertTrue(e.getMessage().contains("S99"));
        assertThrows(IllegalArgumentException.class, () -> graph.getEdge("R99"));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("R50", "S01", "S99", true, 3, "x"));
    }

    @Test
    void invalidWeightsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("R50", "S01", "S02", true, 0, "cero"));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("R51", "S01", "S02", true, -5, "negativo"));
        assertThrows(IllegalArgumentException.class, () -> graph.setWeight("R16", 0));
        assertEquals(12, graph.getEdge("R16").getWeight()); // el peso no cambió
    }

    @Test
    void emptyLabelsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Vertex("", "X", "Y"));
        assertThrows(IllegalArgumentException.class, () -> new Vertex("S20", " ", "Y"));
    }

    @Test
    void neighborsRespectOrientation() {
        // R05 es S02 -> S06 (dirigida): S06 no puede volver a S02
        assertTrue(graph.getNeighbors("S02").contains("S06"));
        assertEquals(List.of("S05"), graph.getNeighbors("S06"));
        // R04 es no dirigida: S01 sí puede ir a S02
        assertTrue(graph.getNeighbors("S01").contains("S02"));
    }

    @Test
    void blockedEdgeIsNotANeighbor() {
        graph.blockEdge("R16");
        assertFalse(graph.getNeighbors("S07").contains("S08"));
        graph.unblockEdge("R16");
        assertTrue(graph.getNeighbors("S07").contains("S08"));
    }

    @Test
    void adjacencyMatrixIsCorrect() {
        int[][] m = graph.representations().adjacencyMatrix();
        assertEquals(12, m.length);
        assertEquals(4, m[0][6]);   // R01: S01 -> S07
        assertEquals(6, m[6][0]);   // R02: S07 -> S01
        assertEquals(5, m[0][8]);   // R03 no dirigida: S01 - S09
        assertEquals(5, m[8][0]);
        assertEquals(10, m[1][5]);  // R05: S02 -> S06
        assertEquals(0, m[5][1]);   // pero no S06 -> S02
    }

    @Test
    void incidenceMatrixIsCorrect() {
        int[][] m = graph.representations().incidenceMatrix();
        assertEquals(12, m.length);
        assertEquals(18, m[0].length);
        assertEquals(1, m[0][0]);   // R01 sale de S01
        assertEquals(-1, m[6][0]);  // R01 llega a S07
        assertEquals(1, m[0][2]);   // R03 no dirigida: 1 en S01
        assertEquals(1, m[8][2]);   // y 1 en S09
    }

    @Test
    void matricesUseTheSameEdgesWhenBlocked() {
        graph.blockEdge("R16");
        GraphRepresentations r = graph.representations();
        assertEquals(17, r.incidenceMatrix()[0].length);
        assertEquals(0, r.adjacencyMatrix()[6][7]); // S07 -> S08 ya no aparece
    }
}
