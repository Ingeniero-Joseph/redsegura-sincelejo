package com.redsegura;

/**
 * Clase fábrica: crea el grafo con los 12 lugares y las 18 conexiones del enunciado.
 * true = dirigida (arco), false = no dirigida (arista). Pesos en minutos.
 */
public class DataFactory {

    private DataFactory() {
        // no se instancia, solo se usa el método estático
    }

    public static MixedGraph createGraph() {
        MixedGraph g = new MixedGraph();

        g.addVertex("S01", "UAJS Sede C", "Universidad");
        g.addVertex("S02", "UAJS Sede E", "Universidad");
        g.addVertex("S03", "Universidad de Sucre", "Universidad");
        g.addVertex("S04", "CECAR", "Universidad");
        g.addVertex("S05", "Hospital Universitario", "Salud");
        g.addVertex("S06", "Clínica Las Américas", "Salud");
        g.addVertex("S07", "Estación Central SETP", "Transporte");
        g.addVertex("S08", "Terminal de Transporte", "Transporte");
        g.addVertex("S09", "Parque Santander", "Punto de encuentro");
        g.addVertex("S10", "Mercado de Sincelejo", "Comercio");
        g.addVertex("S11", "PEP Argelia", "Paradero");
        g.addVertex("S12", "Glorieta Sincelejito", "Movilidad");

        //          ID     origen destino dirigida peso etiqueta
        g.addEdge("R01", "S01", "S07", true, 4, "Corredor universitario");
        g.addEdge("R02", "S07", "S01", true, 6, "Retorno por vía alterna");
        g.addEdge("R03", "S01", "S09", false, 5, "Corredor peatonal");
        g.addEdge("R04", "S02", "S01", false, 8, "Conexión entre sedes");
        g.addEdge("R05", "S02", "S06", true, 10, "Ruta de atención");
        g.addEdge("R06", "S06", "S05", true, 4, "Corredor clínico");
        g.addEdge("R07", "S05", "S09", false, 6, "Vía hospitalaria");
        g.addEdge("R08", "S09", "S10", false, 3, "Centro histórico");
        g.addEdge("R09", "S10", "S08", true, 8, "Salida al terminal");
        g.addEdge("R10", "S08", "S10", true, 9, "Ingreso desde terminal");
        g.addEdge("R11", "S03", "S12", false, 4, "Corredor Puerta Roja");
        g.addEdge("R12", "S12", "S09", false, 7, "Troncal al centro");
        g.addEdge("R13", "S04", "S11", false, 6, "Conexión universitaria");
        g.addEdge("R14", "S11", "S07", true, 8, "Ruta SETP al centro");
        g.addEdge("R15", "S07", "S09", false, 3, "Transferencia central");
        g.addEdge("R16", "S07", "S08", true, 12, "Ruta SETP al terminal");
        g.addEdge("R17", "S09", "S03", true, 7, "Ruta hacia universidad");
        g.addEdge("R18", "S12", "S04", true, 9, "Ruta hacia CECAR");

        return g;
    }
}
