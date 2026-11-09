package com.redsegura;

/**
 * Conexión entre dos lugares.
 * - Dirigida (arco): solo se puede ir de origen a destino.
 * - No dirigida (arista): se puede ir en los dos sentidos.
 * El peso son los minutos de desplazamiento y debe ser mayor que 0.
 */
public class Edge {

    private final String id;
    private final String origin;       // ID del vértice origen
    private final String destination;  // ID del vértice destino
    private final boolean directed;
    private final String label;
    private int weight;
    private boolean blocked;           // una conexión bloqueada no se puede usar

    public Edge(String id, String origin, String destination, boolean directed, int weight, String label) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la conexión no puede estar vacío.");
        }
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("La conexión " + id + " necesita origen y destino.");
        }
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.directed = directed;
        this.label = label;
        setWeight(weight); // reutiliza la validación del peso
    }

    /** Cambia el peso. Lanza error si el peso es 0 o negativo. */
    public void setWeight(int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Peso inválido en " + id + ": " + weight
                    + ". El peso debe ser mayor que 0 minutos.");
        }
        this.weight = weight;
    }

    /**
     * Dice si se puede salir desde el vértice dado usando esta conexión.
     * Un arco solo sale de su origen; una arista sale de cualquiera de sus dos extremos.
     */
    public boolean canGoFrom(String vertexId) {
        if (blocked) {
            return false;
        }
        if (directed) {
            return origin.equals(vertexId);
        }
        return origin.equals(vertexId) || destination.equals(vertexId);
    }

    /** Devuelve el extremo contrario al vértice dado. */
    public String other(String vertexId) {
        if (origin.equals(vertexId)) {
            return destination;
        }
        return origin;
    }

    public String getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isDirected() {
        return directed;
    }

    public String getLabel() {
        return label;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        String flecha = directed ? " -> " : " -- ";
        return id + ": " + origin + flecha + destination + " (" + weight + " min, " + label + ")";
    }
}
