package com.redsegura;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del ABB y del AVL. */
class TreesTest {

    private BinarySearchTree bstWith(String... keys) {
        BinarySearchTree t = new BinarySearchTree();
        for (String k : keys) {
            t.insert(k, null);
        }
        return t;
    }

    // ------------------------------------------------------------ ABB

    @Test
    void bstInsertAndSearch() {
        BinarySearchTree t = new BinarySearchTree();
        Vertex v = new Vertex("S05", "Hospital Universitario", "Salud");
        t.insert("Hospital Universitario", v);
        assertSame(v, t.search("Hospital Universitario"));
        assertNull(t.search("No existe"));
        assertEquals(1, t.size());
    }

    @Test
    void bstTraversalsAndHeight() {
        BinarySearchTree t = bstWith("50", "30", "70", "20", "40", "60", "80");
        assertEquals(List.of("20", "30", "40", "50", "60", "70", "80"), t.inOrder());
        assertEquals(List.of("50", "30", "20", "40", "70", "60", "80"), t.preOrder());
        assertEquals(List.of("20", "40", "30", "60", "80", "70", "50"), t.postOrder());
        assertEquals(3, t.height());
        assertEquals(0, new BinarySearchTree().height());
    }

    @Test
    void bstDeleteThreeCases() {
        BinarySearchTree t = bstWith("50", "30", "70", "20", "40", "60", "80", "65");
        assertTrue(t.delete("20"));   // hoja
        assertTrue(t.delete("60"));   // un hijo
        assertTrue(t.delete("50"));   // dos hijos (raíz)
        assertFalse(t.delete("99"));  // no existe
        assertEquals(List.of("30", "40", "65", "70", "80"), t.inOrder());
        assertEquals("65", t.getRootKey()); // el sucesor reemplazó a la raíz
    }

    @Test
    void bstDegeneratesWithSortedKeys() {
        BinarySearchTree t = bstWith("A", "B", "C", "D", "E");
        assertEquals(5, t.height());
        assertEquals(5, t.searchSteps("E"));
    }

    // ------------------------------------------------------------ AVL

    private AvlTree avlWith(String a, String b, String c) {
        AvlTree t = new AvlTree();
        t.insert(a, null);
        t.insert(b, null);
        t.insert(c, null);
        return t;
    }

    @Test
    void avlRotationLL() {
        AvlTree t = avlWith("30", "20", "10");
        assertEquals("20", t.getRootKey());
        assertTrue(t.getRotations().get(0).startsWith("LL"));
    }

    @Test
    void avlRotationRR() {
        AvlTree t = avlWith("10", "20", "30");
        assertEquals("20", t.getRootKey());
        assertTrue(t.getRotations().get(0).startsWith("RR"));
    }

    @Test
    void avlRotationLR() {
        AvlTree t = avlWith("30", "10", "20");
        assertEquals("20", t.getRootKey());
        assertTrue(t.getRotations().get(0).startsWith("LR"));
    }

    @Test
    void avlRotationRL() {
        AvlTree t = avlWith("10", "30", "20");
        assertEquals("20", t.getRootKey());
        assertTrue(t.getRotations().get(0).startsWith("RL"));
        assertEquals(List.of("20", "10", "30"), t.preOrder());
    }

    @Test
    void avlStaysBalancedAfterInsertAndDelete() {
        AvlTree t = new AvlTree();
        for (int i = 1; i <= 100; i++) {
            t.insert(String.format("%03d", i), null);
            assertTrue(t.isBalanced());
        }
        assertEquals(7, t.height()); // log2(100) ~ 6.6
        for (int i = 1; i <= 60; i++) {
            assertTrue(t.delete(String.format("%03d", i)));
            assertTrue(t.isBalanced());
        }
        assertEquals(40, t.size());
        assertFalse(t.contains("010"));
        assertTrue(t.contains("080"));
    }

    @Test
    void avlDeleteCausesRotation() {
        AvlTree t = avlWith("20", "10", "30");
        t.insert("40", null);
        t.clearRotations();
        t.delete("10"); // 20 queda cargado a la derecha -> RR
        assertTrue(t.getRotations().get(0).startsWith("RR"));
        assertEquals("30", t.getRootKey());
    }

    @Test
    void q1HospitalIsFoundInBothTrees() {
        RedSeguraCore core = new RedSeguraCore();
        String text = core.searchInTrees("Hospital Universitario");
        assertTrue(text.contains("S05"));
        assertThrows(IllegalArgumentException.class, () -> core.searchInTrees("Lugar inventado"));
    }
}
