package com.redsegura;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol binario de búsqueda (ABB).
 * Clave: la etiqueta del lugar (String). Valor: el vértice.
 * Regla: lo menor va a la izquierda y lo mayor a la derecha.
 *
 * Altura: árbol vacío = 0, una hoja = 1.
 * Complejidad: insert, search y delete cuestan O(h). Si el árbol está balanceado h = log n,
 * pero si las claves llegan ordenadas el árbol se vuelve una "lista" y h = n.
 */
public class BinarySearchTree {

    private class Node {
        String key;
        Vertex value;
        Node left;
        Node right;

        Node(String key, Vertex value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    // ------------------------------------------------------------ insertar

    /** Inserta una clave. Si ya existe, actualiza su valor. */
    public void insert(String key, Vertex value) {
        root = insert(root, key, value);
    }

    private Node insert(Node node, String key, Vertex value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value; // clave repetida: solo se actualiza
        }
        return node;
    }

    // ------------------------------------------------------------ buscar

    /** Devuelve el valor de la clave, o null si no está. */
    public Vertex search(String key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return current.value;
            }
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }

    /** Cuántos nodos se comparan para buscar la clave (sirve para comparar ABB vs AVL). */
    public int searchSteps(String key) {
        int steps = 0;
        Node current = root;
        while (current != null) {
            steps++;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return steps;
            }
            current = (cmp < 0) ? current.left : current.right;
        }
        return steps; // no encontrado: pasos hasta llegar a null
    }

    /** Dice si la clave está en el árbol (aunque su valor sea null). */
    public boolean contains(String key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return true;
            }
            current = (cmp < 0) ? current.left : current.right;
        }
        return false;
    }

    // ------------------------------------------------------------ eliminar

    /** Elimina la clave. Devuelve true si existía. */
    public boolean delete(String key) {
        if (!contains(key)) {
            return false;
        }
        root = delete(root, key);
        size--;
        return true;
    }

    private Node delete(Node node, String key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            // Caso 1 y 2: cero o un hijo -> se reemplaza por el hijo
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            // Caso 3: dos hijos -> se copia el sucesor (el menor del subárbol derecho) y se borra
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.key = successor.key;
            node.value = successor.value;
            node.right = delete(node.right, successor.key);
        }
        return node;
    }

    // ------------------------------------------------------------ recorridos

    /** Izquierda, raíz, derecha: sale en orden alfabético. */
    public List<String> inOrder() {
        List<String> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<String> result) {
        if (node != null) {
            inOrder(node.left, result);
            result.add(node.key);
            inOrder(node.right, result);
        }
    }

    /** Raíz, izquierda, derecha. */
    public List<String> preOrder() {
        List<String> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    private void preOrder(Node node, List<String> result) {
        if (node != null) {
            result.add(node.key);
            preOrder(node.left, result);
            preOrder(node.right, result);
        }
    }

    /** Izquierda, derecha, raíz. */
    public List<String> postOrder() {
        List<String> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    private void postOrder(Node node, List<String> result) {
        if (node != null) {
            postOrder(node.left, result);
            postOrder(node.right, result);
            result.add(node.key);
        }
    }

    // ------------------------------------------------------------ altura y tamaño

    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public int size() {
        return size;
    }

    public String getRootKey() {
        return root == null ? null : root.key;
    }
}
