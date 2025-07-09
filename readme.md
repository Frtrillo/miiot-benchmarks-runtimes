# Benchmark de Rendimiento: .NET vs. Java vs. Bun vs. Node.js by Francisco Trillo 🔥

En este documento presento los resultados de un benchmark riguroso que diseñé para medir el rendimiento de **.NET (C#), Java, Bun y Node.js** en una tarea que simula mi caso de uso real: el procesamiento masivo de **objetos de datos complejos** de forma intensiva y puramente computacional (CPU-bound).

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

*   **Algoritmo y Estructura de Datos Idénticos:** La lógica y la estructura del objeto `HistoryLogFull` son una réplica 1:1 en C#, Java y JavaScript.
*   **Ejecución en un Solo Hilo:** Todas las pruebas se ejecutan en un único hilo para una comparación equitativa del rendimiento por núcleo.
*   **Modos de Ejecución Claros:** Se prueba .NET tanto en modo **Release** (con todas las optimizaciones del compilador) como en modo **Debug** (sin optimizaciones) para ilustrar el impacto de la compilación. Java se ejecuta directamente sobre su código compilado (`.class`), que por defecto está altamente optimizado por su JIT (HotSpot).

### Entorno de Pruebas
*   **CPU x86:** AMD Ryzen 7 5800X (8 núcleos, 16 hilos)
*   **CPU ARM:** Apple Silicon M1 (8 núcleos, 8 hilos de rendimiento + 2 hilos de eficiencia)
*   **RAM:** 16 GB DDR4 3600 MHz (x86) / 16 GB LPDDR4X (ARM)
*   **Sistema Operativo:** Windows 11 (x86) / macOS (ARM)

## Cómo Ejecutar el Benchmark
```powershell
# .NET (Modo Release - Optimizado para producción)
dotnet run -c Release --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj

# Java (Compilar primero)
javac ./java-mastica-historial/Benchmark.java
# Java (Ejecutar - Requiere heap grande)
java -Xmx8g -cp ./java-mastica-historial/ Benchmark

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
- **Java (OpenJDK):** 21+

## Resultados con 50 Millones de Registros (Test de Estrés)

Para llevar los runtimes al límite, se ejecutó la prueba con 50 millones de registros. Los resultados no solo confirman las tendencias, sino que también posicionan a los lenguajes compilados estáticamente de forma muy favorable.

### Resultados en x86 (AMD Ryzen 7 5800X)

| Runtime         | Tiempo (segundos) | Observaciones                                                                                                                        |
| :-------------- | :---------------- | :----------------------------------------------------------------------------------------------------------------------------------- |
| **Bun**         | **23.564 s**       | 🥇 **El rey de la velocidad en JavaScript**. Demuestra una eficiencia extraordinaria en la creación y acceso a propiedades de millones de objetos, consolidando su liderazgo sobre Node.js. |
| **Java**        | **26.076 s**       | 🥈 **Rendimiento sobresaliente**. La JVM HotSpot demuestra su poder de optimización en tiempo de ejecución para cargas masivas. |
| **.NET (Release)** | **40.492 s**    | 🥉 **Rendimiento sólido y predecible**. Extremadamente competitivo y con una escalabilidad robusta. Un pilar de fiabilidad para sistemas de producción. |
| **Node.js**     | **78.345 s**       | **La brecha se amplía**. A esta escala, Node.js es **3.3 veces más lento que Bun** y significativamente más lento que .NET y Java. La sobrecarga en la gestión de objetos dinámicos de V8 se hace muy pronunciada. |

### Resultados en ARM (Apple Silicon M1)

| Runtime | Tiempo (segundos) | Observaciones |
| :--- | :--- | :--- |
| **Bun** | **21.825 s** | 🥇 **Excelente rendimiento en ARM**. Bun demuestra una optimización excepcional para Apple Silicon, siendo significativamente más rápido que en x86 (~7.4% mejor). El motor JavaScriptCore está muy bien optimizado para la arquitectura ARM. |
| **Java** | **28.765 s** | 🥈 **Rendimiento sólido en ARM**. OpenJDK 21 muestra un rendimiento competitivo en Apple Silicon, siendo ligeramente más lento que en x86 (~10% más lento). La JVM HotSpot funciona bien en ARM, aunque no aprovecha completamente las optimizaciones específicas de la arquitectura. |
| **.NET (Release)** | **31.281 s** | 🥉 **Rendimiento excepcional en ARM**. .NET muestra una optimización extraordinaria para Apple Silicon, siendo significativamente más rápido que en x86 (~23% mejor). La compilación nativa para ARM está aprovechando completamente las optimizaciones de la arquitectura. |
| **Node.js** | **N/A** | **No probado en esta ronda**. Los resultados anteriores mostraban que Node.js mantenía una brecha significativa con Bun en ARM. |

### Comparación Arquitectónica

**Observaciones clave entre x86 y ARM:**

*   **Bun en ARM es más rápido:** La diferencia de ~10.8 segundos (21.8s vs 32.6s) confirma que Bun está excepcionalmente bien optimizado para Apple Silicon, siendo ~33% más rápido en ARM que en x86.
*   **.NET en ARM es excepcional:** Muestra una mejora del ~23% respecto a x86, aprovechando completamente las optimizaciones de ARM.
*   **Java en ARM es competitivo:** OpenJDK 21 muestra un rendimiento ligeramente inferior (~10% más lento) en ARM que en x86, pero sigue siendo muy competitivo y funcional.
*   **Node.js no fue probado:** En esta ronda de pruebas no se incluyó Node.js, pero los resultados anteriores mostraban una brecha significativa con Bun en ambas arquitecturas.

![Resultados del Benchmark](benchmark-result.png)

### Análisis del Consumo de Memoria (Test de 50M)

La velocidad no es el único factor. El consumo de memoria revela una historia diferente y muy importante, destacando la eficiencia de los runtimes con tipado estático.

#### Consumo de Memoria en x86 (AMD Ryzen 7 5800X)

| Runtime | Consumo de RAM (Aprox.) | Observaciones |
| :--- | :--- | :--- |
| **.NET (Release)** | **~560 MB** | 🏆 **El campeón de la eficiencia**. Un consumo de memoria extraordinariamente bajo. La gestión de memoria de .NET, sus tipos de valor (`structs`) y su recolector de basura son de primera clase. |
| **Java** | **~1.06 GB** | **Consumo muy eficiente**. Aunque usa más RAM que .NET (debido a que todo es un objeto en el heap), sigue siendo mucho más eficiente que los runtimes de JS. Su recolector de basura (G1 GC) gestiona la carga sin problemas. |
| **Node.js** | **~1.2 GB** | **Consumo moderado**. Utiliza más del doble de RAM que .NET, un coste esperado por la naturaleza dinámica de los objetos en V8. |
| **Bun** | **~2.0 GB** | **El más intensivo en RAM**. Aunque es el más rápido en CPU, es el que más memoria consume. Esto podría deberse a una gestión de memoria menos madura o a un memory leak, ya que aún es un runtime joven y no es la primera vez que me he encontrado con un leak corriendo otros proyectos de nodejs con bun. |

## Conclusiones Finales

### Rendimiento por Arquitectura

1.  **Bun se Consolida como el Rey de la Velocidad (en CPU):** En este escenario de procesamiento masivo, **Bun es el campeón indiscutible en tiempo de ejecución** en ambas arquitecturas. Su liderazgo sobre Node.js es abrumador, demostrando el poder del motor JavaScriptCore para este tipo de tarea. **Notablemente, Bun es aún más rápido en ARM (Apple Silicon M1) que en x86**, mostrando una optimización excepcional para la arquitectura ARM.

2.  **El Rendimiento No es Solo Velocidad: .NET y Java Lideran en Eficiencia:** Si bien Bun gana en velocidad, **.NET es el claro ganador en eficiencia de memoria** en x86, consumiendo casi 4 veces menos RAM. **Java se posiciona como una opción intermedia excelente**, ofreciendo un rendimiento en CPU ligeramente superior a .NET con un consumo de memoria muy controlado. Ambos (.NET y Java) son opciones increíblemente atractivas para entornos de producción donde el equilibrio entre velocidad y recursos es crítico.

3.  **Node.js Muestra sus Límites en Cargas CPU-Intensivas:** Aunque es el runtime más popular, para este tipo de tarea computacional la brecha de rendimiento con sus competidores se hace más grande a medida que aumenta la carga. **En ARM, Node.js mantiene una brecha similar con Bun**, siendo aproximadamente 2.5 veces más lento.

4.  **Optimizaciones Específicas de Arquitectura:**
    *   **Bun en ARM:** Muestra una optimización excepcional para Apple Silicon, siendo ~33% más rápido que en x86.
    *   **.NET en ARM:** Demuestra una optimización extraordinaria, siendo ~23% más rápido que en x86 y con mejor eficiencia de memoria.
    *   **Node.js en ARM:** No probado en esta ronda, pero los resultados anteriores mostraban una brecha significativa con Bun.
    *   **Java en ARM:** Rendimiento competitivo, siendo solo ~10% más lento que en x86, lo que indica una buena optimización para Apple Silicon.

### Recomendaciones por Contexto

**La Elección Depende del Contexto:**
*   Para tareas de procesamiento en frío donde la **velocidad máxima de CPU es la única prioridad** y la memoria es abundante, **Bun** es la mejor opción, especialmente en ARM.
*   Para sistemas de producción que exigen la **máxima eficiencia de memoria**, **.NET (Release)** es el ganador indiscutible en x86.
*   Para un **excelente equilibrio entre velocidad de élite, eficiencia de memoria y un ecosistema maduro y robusto**, **Java** es una opción formidable.
*   **Node.js** sigue siendo una herramienta fantástica para aplicaciones I/O-bound (servidores web, APIs), pero para este caso de uso específico, es superado en ambas arquitecturas.

### Un Benchmark para un Caso de Uso Específico
Es crucial recordar que diseñé "Mastica-Historial" para mi caso de uso específico: el procesamiento intensivo de datos históricos de dispositivos. Los resultados son válidos para tareas similares, pero no deben extrapolarse a todos los escenarios (p. ej., servidores web).

Para evaluaciones de rendimiento en otros ámbitos, recomiendo consultar benchmarks especializados:

*   **[TechEmpower Web Framework Benchmarks](https://www.techempower.com/benchmarks/):** El estándar de la industria para comparar frameworks web.
*   **[SharkBench](https://sharkbench.dev/):** Otro gran recurso que compara el rendimiento de diferentes runtimes de JavaScript.