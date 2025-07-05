
# Benchmark de Rendimiento: .NET vs. Bun vs. Node.js by Francisco Trillo 🔥

En este documento presento los resultados de un benchmark riguroso que diseñé para medir el rendimiento de **.NET (C#), Bun y Node.js** en una tarea que simula mi caso de uso real: el procesamiento masivo de **objetos de datos complejos** de forma intensiva y puramente computacional (CPU-bound).

## El Desafío: "Agregador de Telemetría Masiva (15 Millones de Registros)"

Esta versión del benchmark se centra en un escenario de carga pesada pero realista, abandonando pruebas de estrés extremo para analizar el rendimiento en un contexto concreto. El algoritmo:

1.  **Genera 15 millones de registros de telemetría**. Cada registro es un **objeto "pesado" con más de 70 propiedades** (números, booleanos, nulos), replicando fielmente el esquema de mi tabla `history_logs` de PostgreSQL.
2.  **Agrupa estos objetos complejos** en "buckets" de 10 minutos basándose en su marca de tiempo, generando aproximadamente **1.5 millones de claves únicas**.
3.  Para cada bucket, **realiza múltiples agregaciones** sobre los objetos, calculando promedios, gestionando valores nulos y contando eventos.

Esta prueba está diseñada para estresar factores críticos en aplicaciones del mundo real:
*   **Coste de asignación de memoria** para millones de objetos grandes.
*   **Presión sobre el Recolector de Basura (Garbage Collector)**.
*   **Eficiencia del acceso a propiedades** en objetos complejos.

## Metodología y Garantía de Justicia

Para asegurar una comparación justa y precisa, he seguido principios estrictos:

*   **Algoritmo y Estructura de Datos Idénticos:** La lógica y la estructura del objeto `HistoryLogFull` son una réplica 1:1 en C# y JavaScript.
*   **Ejecución en un Solo Hilo:** Todas las pruebas se ejecutan en un único hilo para una comparación equitativa del rendimiento por núcleo.
*   **Modos de Ejecución Claros:** Se prueba .NET tanto en modo **Release** (con todas las optimizaciones del compilador) como en modo **Debug** (sin optimizaciones) para ilustrar el impacto de la compilación.

### Entorno de Pruebas
*   **CPU:** AMD Ryzen 7 5800X (8 núcleos, 16 hilos)
*   **RAM:** 16 GB DDR4 3600 MHz
*   **Sistema Operativo:** Windows

## Cómo Ejecutar el Benchmark
```powershell
# .NET (Modo Release - Optimizado para producción)
dotnet run -c Release --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj

# .NET (Modo Debug - Sin optimizar, para desarrollo)
dotnet run --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj

# Bun
bun run ./bun-mastica-historial/benchmark.js

# Node.js
node ./bun-mastica-historial/benchmark.js
```

---

## Versiones de los Runtimes

- **Bun:** 1.2.15+
- **Node.js:** v22.14.0+
- **.NET SDK:** 9.0.301+

## Resultados con 15 Millones de Registros Complejos

Los resultados de esta prueba muestran un claro ganador en rendimiento y ofrecen una lección valiosa sobre la importancia de la compilación optimizada.

| Runtime | Tiempo (segundos) | Observaciones |
| :--- | :--- | :--- |
| **Bun** | **~9.8 s** | 🥇 **El claro ganador**. El motor JavaScriptCore de Bun demuestra una eficiencia extraordinaria en la creación y acceso a propiedades de millones de objetos pesados, superando a todos los contendientes. |
| **.NET (Release)** | **~13.1 s** | 🥈 **Rendimiento de élite**. La versión optimizada de .NET es extremadamente rápida, demostrando el poder de su compilador JIT (RyuJIT). Una opción muy robusta y de alto rendimiento. |
| **Node.js** | **~18.7 s** | 🐢 **Tercer lugar**. Completa la tarea, pero es casi 2 veces más lento que Bun. La sobrecarga del motor V8 en este tipo de manipulación de objetos dinámicos a gran escala es evidente. |
| **.NET (Debug)** | **~18.9 s** | 🐢 **El más lento**. Este resultado es esperado y crucial. Sin las optimizaciones del JIT, el rendimiento se degrada significativamente, quedando a la par con Node.js. |

---

## Conclusiones Finales

1.  **Bun se Impone como el Más Rápido para esta Tarea:** En este escenario específico de procesamiento masivo de objetos complejos, **Bun es el campeón indiscutible**. Su rendimiento demuestra que no es solo una herramienta para tareas simples, sino un competidor formidable para cargas de trabajo de CPU intensivas en el ecosistema JavaScript.

2.  **La Compilación Optimizada es Crucial: .NET Release vs. Debug:** La diferencia entre .NET en modo Release (~13s) y Debug (~19s) es abismal. Esto subraya un punto fundamental: **los benchmarks de rendimiento siempre deben ejecutarse con las optimizaciones de producción activadas**. El modo Debug está diseñado para la depuración, no para la velocidad, y los resultados lo confirman. .NET optimizado sigue siendo una potencia.

3.  **Node.js es Superado en Cargas de CPU Intensivas:** Aunque es el runtime de JavaScript más popular, para este tipo de tarea puramente computacional, Node.js es claramente superado tanto por Bun como por una versión optimizada de .NET. Su motor V8, aunque excelente para muchas cosas (especialmente I/O), muestra una mayor sobrecarga en este benchmark.

4.  **Para este Caso de Uso, la Elección es Clara:** Basado en estos resultados, para una tarea de procesamiento de datos en frío, CPU-bound y con objetos complejos, **Bun es la opción más performante**, seguido de cerca por **.NET (Release)**.

### Un Benchmark para un Caso de Uso Específico
Es crucial recordar que diseñé "Mastica-Historial" para mi caso de uso específico: el procesamiento intensivo de datos históricos de dispositivos. Los resultados son válidos para tareas similares, pero no deben extrapolarse a todos los escenarios (p. ej., servidores web).

Para evaluaciones de rendimiento en otros ámbitos, recomiendo consultar benchmarks especializados:

*   **[TechEmpower Web Framework Benchmarks](https://www.techempower.com/benchmarks/#section=data-r23y):** El estándar de la industria para comparar frameworks web.
*   **[SharkBench](https://sharkbench.dev/):** Otro gran recurso que compara el rendimiento de diferentes runtimes de JavaScript.