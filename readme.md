
# Benchmark de Rendimiento: .NET vs. Bun vs. Node.js by Francisco Trillo 🔥

En este documento presento los resultados de un benchmark riguroso que diseñé para medir el rendimiento de **.NET (C#), Bun y Node.js** en una tarea que simula mi caso de uso real: el procesamiento masivo de **objetos de datos complejos** de forma intensiva y puramente computacional (CPU-bound).

## El Desafío: "Agregador de Telemetría Masiva (hasta 50 Millones de Registros)"

Esta versión del benchmark se centra en un escenario de carga pesada pero realista, con pruebas que escalan desde 15 hasta 50 millones de registros para analizar el rendimiento en contextos concretos y de estrés. El algoritmo:

1.  **Genera una cantidad masiva de registros de telemetría**. Cada registro es un **objeto "pesado" con más de 70 propiedades** (números, booleanos, nulos), replicando fielmente el esquema de mi tabla `history_logs` de PostgreSQL.
2.  **Agrupa estos objetos complejos** en "buckets" de 10 minutos basándose en su marca de tiempo.
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
*   **Sistema Operativo:** Windows 11

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

## Resultados con 15 Millones de Registros Complejos (Carga Estándar)

Los resultados de esta prueba base muestran un claro ganador en rendimiento y ofrecen una lección valiosa sobre la importancia de la compilación optimizada.

| Runtime | Tiempo (segundos) | Observaciones |
| :--- | :--- | :--- |
| **Bun** | **~9.8 s** | 🥇 **El claro ganador**. El motor JavaScriptCore de Bun demuestra una eficiencia extraordinaria en la creación y acceso a propiedades de millones de objetos pesados. |
| **.NET (Release)** | **~13.1 s** | 🥈 **Rendimiento de élite**. La versión optimizada de .NET es extremadamente rápida, demostrando el poder de su compilador JIT (RyuJIT). |
| **Node.js** | **~18.7 s** | 🐢 **Tercer lugar**. Completa la tarea, pero es casi 2 veces más lento que Bun. La sobrecarga del motor V8 en este tipo de manipulación de objetos dinámicos es evidente. |
| **.NET (Debug)** | **~18.9 s** | 🐢 **El más lento**. Sin las optimizaciones del JIT, el rendimiento se degrada significativamente, quedando a la par con Node.js. |

---

## Ampliando la Carga: Resultados con 50 Millones de Registros (Test de Estrés)

Para llevar los runtimes al límite, se triplicó la carga. Los resultados no solo confirman las tendencias, sino que amplían las diferencias.

| Runtime | Tiempo (segundos) | Observaciones |
| :--- | :--- | :--- |
| **Bun** | **~32.6 s** | 🥇 **Consolida su liderazgo**. Mantiene su posición como el más rápido, demostrando que su rendimiento escala de manera excelente con cargas de trabajo mucho mayores. |
| **.NET (Release)** | **~43.4 s** | 🥈 **Rendimiento sólido y predecible**. Sigue siendo extremadamente competitivo y demuestra una escalabilidad robusta. Un pilar de fiabilidad. |
| **Node.js** | **~64.6 s** | 🐢 **La brecha se amplía**. A esta escala, Node.js es ahora **2 veces más lento que Bun** y un 50% más lento que .NET. La sobrecarga en la gestión de objetos se hace mucho más pronunciada. |

### Análisis del Consumo de Memoria (Test de 50M)

La velocidad no es el único factor. El consumo de memoria revela una historia diferente y muy importante.

| Runtime | Consumo de RAM (Aprox.) | Observaciones |
| :--- | :--- | :--- |
| **.NET (Release)** | **~560 MB** | 🏆 **El campeón de la eficiencia**. Un consumo de memoria extraordinariamente bajo. La gestión de memoria de .NET y su recolector de basura son de primera clase. |
| **Node.js** | **~1.2 GB** | **Consumo moderado**. Utiliza más del doble de RAM que .NET, un coste esperado por la naturaleza dinámica de los objetos en V8. |
| **Bun** | **~2.0 GB** | **El más intensivo en RAM**. Aunque es el más rápido en CPU, es el que más memoria consume. Esto podría deberse a una gestión de memoria menos madura o a un memory leak, ya que aún es un runtime joven y no es la primera vez que me he encontrado con un leak corriendo otros proyectos de nodejs con bun. |

---

## Conclusiones Finales

1.  **Bun se Consolida como el Rey de la Velocidad (en CPU):** En este escenario de procesamiento masivo, **Bun es el campeón indiscutible en tiempo de ejecución**. Su liderazgo no solo se mantiene, sino que se afianza a mayor escala, superando a Node.js por un margen de 2 a 1.

2.  **El Rendimiento No es Solo Velocidad: .NET Lidera en Eficiencia:** Si bien Bun gana en velocidad, **.NET es el claro ganador en eficiencia de memoria**, consumiendo casi 4 veces menos RAM. Esto lo convierte en una opción increíblemente atractiva para entornos de producción donde la memoria es un recurso crítico, ofreciendo un equilibrio casi perfecto entre alta velocidad y baja huella de recursos.

3.  **Node.js Muestra sus Límites en Cargas CPU-Intensivas:** Aunque es el runtime más popular, para este tipo de tarea computacional la brecha de rendimiento con sus competidores se hace más grande a medida que aumenta la carga.

4.  **La Elección Depende del Contexto:**
    *   Para tareas de procesamiento en frío donde la **velocidad máxima de CPU es la única prioridad** y la memoria es abundante, **Bun** es la mejor opción.
    *   Para sistemas de producción robustos que requieren un **excelente equilibrio entre velocidad, bajo consumo de memoria y madurez del ecosistema**, **.NET (Release)** es la opción superior.
    *   **Node.js** sigue siendo una herramienta fantástica para aplicaciones I/O-bound (servidores web, APIs), pero para este caso de uso específico, es superado.

### Un Benchmark para un Caso de Uso Específico
Es crucial recordar que diseñé "Mastica-Historial" para mi caso de uso específico: el procesamiento intensivo de datos históricos de dispositivos. Los resultados son válidos para tareas similares, pero no deben extrapolarse a todos los escenarios (p. ej., servidores web).

Para evaluaciones de rendimiento en otros ámbitos, recomiendo consultar benchmarks especializados:

*   **[TechEmpower Web Framework Benchmarks](https://www.techempower.com/benchmarks/#section=data-r23y):** El estándar de la industria para comparar frameworks web.
*   **[SharkBench](https://sharkbench.dev/):** Otro gran recurso que compara el rendimiento de diferentes runtimes de JavaScript.
