# Informe: RedSegura Sincelejo

## 1. Decisiones de diseño

1. **Un solo grafo para los dos tipos de conexión.** Cada `Edge` guarda si es dirigida o no. El método
   `canGoFrom(v)` decide por dónde se puede salir: un arco solo sale de su origen y una arista sale de los dos extremos.
   BFS, DFS, Dijkstra y la lista de adyacencia usan ese mismo método, así que siempre respetan la orientación.

2. **Lista de adyacencia como estructura principal.** `MixedGraph` guarda un `Map<String, List<Edge>>`. Un arco se
   agrega solo a la lista del origen y una arista a la lista de sus dos extremos. Así, consultar los vecinos de un
   vértice cuesta O(grado) y no hay que recorrer todas las conexiones.

3. **Mismo orden en todas las representaciones.** Se usa `LinkedHashMap` para que vértices y conexiones queden en el
   orden en que se cargaron (S01..S12, R01..R18). Las dos matrices y la lista usan ese orden.

4. **Bloquear sin borrar.** Una conexión bloqueada sigue guardada, pero `canGoFrom` devuelve `false` y no aparece en
   las matrices. Así se puede desbloquear durante la demostración.

5. **Validaciones.** IDs duplicados, vértices o conexiones inexistentes, pesos menores o iguales a 0 y etiquetas
   vacías lanzan `IllegalArgumentException` con un mensaje claro. La aplicación atrapa la excepción, muestra el
   mensaje y continúa.

6. **Árboles con clave = etiqueta.** El ABB y el AVL guardan la etiqueta del lugar como clave y el `Vertex` como valor.
   Se insertan en orden de ID (S01..S12). La altura de un árbol vacío es 0 y la de una hoja es 1.

7. **Dijkstra con desempate estable.** La relajación usa `<` estricto. Entre S02 y S08 hay dos rutas de 24 minutos;
   como S07 (distancia 12) sale de la cola antes que S10 (distancia 16), S08 se alcanza primero desde S07 y se
   conserva la ruta del enunciado: S02 → S01 → S07 → S08.

## 2. Complejidad

| Componente | Operación | Tiempo | Espacio | Justificación |
|---|---|---|---|---|
| ABB | insert, search, delete | O(h) | O(h) | Se baja un nivel por comparación. Con claves ordenadas h = n. |
| AVL | insert, search, delete | O(log n) | O(log n) | La altura de un AVL es como máximo ~1.44·log₂ n; las rotaciones son O(1). |
| Árboles | recorridos | O(n) | O(h) | Cada nodo se visita una vez; la recursión usa h marcos de pila. |
| Grafo | vecinos de v | O(grado(v)) | O(grado(v)) | Se recorre solo la lista de v. |
| Representaciones | lista / matriz adyacencia / incidencia | O(V+E) / O(V²+E) / O(V·E) | igual | Tamaño de cada estructura. |
| BFS / DFS | recorrido | O(V+E) | O(V) | Cada vértice se visita una vez gracias al conjunto de visitados. |
| Dijkstra | ruta mínima | O((V+E) log V) | O(V+E) | Cada relajación puede meter un elemento en la PriorityQueue (log V). |

## 3. Medición reproducible (opción 11 del menú)

Se insertan 2000 claves **ordenadas** (`K00000`..`K01999`) en un ABB y en un AVL, y se buscan todas. Las claves son
siempre las mismas, así que el experimento se puede repetir. Resultado en este equipo:

| Estructura | Altura | Buscar las 2000 claves |
|---|---|---|
| ABB | 2000 | ~11 ms |
| AVL | 11 | ~3.6 ms |

Dijkstra desde S02 en el grafo de 12 vértices tarda unos 11 microsegundos (promedio de 1000 ejecuciones).
Esto confirma la teoría: con claves ordenadas el ABB degenera en una lista (O(n)) y el AVL se mantiene en O(log n).
Los tiempos exactos cambian según el computador, pero las alturas siempre son las mismas.

## 4. Resultados de control

| Consulta | Resultado obtenido |
|---|---|
| Q1 | ABB: 3 pasos (altura 7). AVL: 1 paso (altura 4). |
| Q2 | LL, RR, LR y RL demostradas; la raíz final siempre es 20. |
| Q3 | Matriz de adyacencia 12×12 y de incidencia 12×18 con el mismo orden de vértices y conexiones. |
| Q4 | BFS y DFS desde S02 visitan los 12 vértices en distinto orden (cola contra recursión). |
| Q5 | S02 → S01 → S07 → S08 = **24 minutos**. |
| Q6 | Con R16 bloqueada: S02 → S01 → S09 → S10 → S08 = 24 minutos (ruta empatada). |
| Q7 | S99 genera un error controlado y el programa sigue. |

## 5. Pruebas

36 pruebas JUnit 5, todas aprobadas:

- `TreesTest` (11): inserción, búsqueda, los tres casos de eliminación, recorridos y altura del ABB; las cuatro
  rotaciones; eliminación con rotación; balance después de 100 inserciones y 60 eliminaciones.
- `GraphTest` (10): carga de datos, IDs duplicados, vértices y conexiones inexistentes, pesos 0 y negativos, etiquetas
  vacías, orientación, bloqueos y valores de las dos matrices.
- `AlgorithmsTest` (15): BFS y DFS (orden, niveles, orientación, vértice inexistente, grafo desconectado), ciclos,
  componentes débiles y Dijkstra (24 minutos, bloqueo de R16, sin ruta, cambio de peso y grafo desconectado).
