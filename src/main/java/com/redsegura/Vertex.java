package com.redsegura;

/**
 * Vértice del grafo: representa un lugar de Sincelejo.
 * Ejemplo: S05, "Hospital Universitario", "Salud".
 */
public class Vertex {

    private final String id;
    private final String label;
    private final String type;

    public Vertex(String id, String label, String type) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del vértice no puede estar vacío.");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("La etiqueta del vértice " + id + " no puede estar vacía.");
        }
        this.id = id;
        this.label = label;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + " (" + label + ")";
    }
}
