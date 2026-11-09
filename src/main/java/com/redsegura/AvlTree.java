package com.redsegura;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol AVL: es un ABB que se balancea solo.
 *
 * Cada nodo guarda su altura. El factor de balance es:
 *     FB = altura(izquierdo) - altura(derecho)
 * Si después de insertar o eliminar un nodo queda con FB = 2 o FB = -2, se rota:
 *
 *   FB = +2 e hijo izquierdo con FB >= 0  -> caso LL -> rotación simple a la derecha
 *   FB = +2 e hijo izquierdo con FB <  0  -> caso LR -> izquierda en el hijo y derecha en el nodo
 *   FB = -2 e hijo derecho   con FB <= 0  -> caso RR -> rotación simple a la izquierda
 *   FB = -2 e hijo derecho   con FB >  0  -> caso RL -> derecha en el hijo e izquierda en el nodo
 *
 * Como la altura siempre es O(log n), insert, search y delete cuestan O(log n).
 */
public class AvlTree {

    private class Node {
        String key;
        Vertex value;
        Node left;
        Node right;
        int height = 1; // una hoja tiene altura 1

        Node(String key, Vertex value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;
    private final List<String> rotations = new ArrayList<>(); // registro de rotaciones hechas

    // ------------------------------------------------------------ utilidades

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    // ------------------------------------------------------------ rotaciones

    /**
     * Rotación a la derecha sobre y.
     *        y            x
     *       /            / \
     *      x      ->    z   y
     *     /
     *    z
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;
        x.right = y;
        y.left = t2;
        updateHeight(y); // primero y porque ahora está abajo
        updateHeight(x);
        return x;
    }

    /**
     * Rotación a la izquierda sobre x.
     *    x                y
     *     \              / \
     *      y      ->    x   z
     *       \
     *        z
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;
        y.left = x;
        x.right = t2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    /** Actualiza la altura del nodo y, si está desbalanceado, aplica la rotación que corresponde. */
    private Node balance(Node node) {
        updateHeight(node);
        int fb = balanceFactor(node);

        if (fb > 1) { // cargado a la izquierda
            if (balanceFactor(node.left) >= 0) {
                rotations.add("LL en el nodo " + node.key + " (rotación simple a la derecha)");
                return rotateRight(node);
            } else {
                rotations.add("LR en el nodo " + node.key + " (izquierda en " + node.left.key + " y derecha en " + node.key + ")");
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }
        if (fb < -1) { // cargado a la derecha
            if (balanceFactor(node.right) <= 0) {
                rotations.add("RR en el nodo " + node.key + " (rotación simple a la izquierda)");
                return rotateLeft(node);
            } else {
                rotations.add("RL en el nodo " + node.key + " (derecha en " + node.right.key + " e izquierda en " + node.key + ")");
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }
        return node; // ya estaba balanceado
    }

    // ------------------------------------------------------------ insertar

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
            node.value = value;
            return node;
        }
        return balance(node); // al volver de la recursión se revisa cada ancestro
    }

    // ------------------------------------------------------------ eliminar

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
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.key = successor.key;
            node.value = successor.value;
            node.right = delete(node.right, successor.key);
        }
        return balance(node);
    }

    // ------------------------------------------------------------ buscar

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
        return steps;
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

    // ------------------------------------------------------------ recorridos

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

    // ------------------------------------------------------------ consultas

    public int height() {
        return height(root);
    }

    public int size() {
        return size;
    }

    public String getRootKey() {
        return root == null ? null : root.key;
    }

    public List<String> getRotations() {
        return new ArrayList<>(rotations);
    }

    public void clearRotations() {
        rotations.clear();
    }

    /** Revisa que todos los nodos tengan FB entre -1 y 1 (se usa en las pruebas). */
    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(Node node) {
        if (node == null) {
            return true;
        }
        int fb = balanceFactor(node);
        return fb >= -1 && fb <= 1 && isBalanced(node.left) && isBalanced(node.right);
    }
}
