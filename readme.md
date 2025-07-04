
# Benchmark de Rendimiento: .NET vs. Node.js vs. Bun BY TRILLO 🔥

Este documento presenta los resultados de un benchmark diseñado para medir el rendimiento de **.NET (C#), Node.js y Bun** en una tarea de procesamiento de datos intensiva y puramente computacional (CPU-bound).

## El Desafío: "Mastica-Historial"

El algoritmo probado simula una tarea común de procesamiento de logs o series temporales:
1.  Genera 50 millones de registros de datos, cada uno con una marca de tiempo y un valor numérico.
2.  Agrupa estos registros en "buckets" de 10 minutos.
3.  Calcula la suma y el conteo de valores para cada bucket.
4.  Finalmente, calcula el promedio de cada bucket.

Esta prueba está diseñada para estresar el **rendimiento del bucle principal, las operaciones matemáticas y el acceso a una estructura de datos de tipo diccionario/mapa (tabla hash)**. No involucra operaciones de I/O (disco o red).

## Metodología y Garantía de Justicia

Para asegurar una comparación justa y precisa, se siguieron los siguientes principios:

*   **Algoritmo Idéntico:** La lógica de programación es una réplica fiel en C# y JavaScript, utilizando las estructuras de datos y operaciones equivalentes en cada ecosistema.
*   **Ejecución en un Solo Hilo:** Todas las pruebas se ejecutaron utilizando **un único hilo de procesamiento (single-thread)**. Esto garantiza una comparación **justa y equitativa** del rendimiento por núcleo de cada runtime.
*   **Compilación Optimizada:** La prueba principal de .NET se ejecutó en modo **Release**, que aplica las máximas optimizaciones. El modo Debug se incluye como referencia.

## Cómo Ejecutar el Benchmark

Para replicar estos resultados, puedes usar los siguientes comandos desde la raíz del proyecto. Asegúrate de tener instalados .NET, Node.js y Bun.

### .NET (Modo Release - Optimizado)
Este comando compila y ejecuta el proyecto en su configuración más rápida.

```powershell
dotnet run -c Release --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj
```

### .NET (Modo Debug - Referencia)
Este comando ejecuta el proyecto sin optimizaciones, útil para ver el impacto de la compilación.

```powershell
dotnet run --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj
```

### Bun
Bun ejecuta directamente el archivo JavaScript con su runtime de alto rendimiento.

```powershell
bun run ./bun-mastica-historial/aot-processing.js
```

### Node.js
Se usa el ejecutable estándar de Node.js para correr el mismo script.

```powershell
node ./bun-mastica-historial/aot-processing.js
```

---

## Resultados Finales (Mejores Tiempos)

| Runtime | Tiempo (segundos) | Observaciones |
| :--- | :--- | :--- |
| **Bun** | **1.482 s** | 🥇 **El ganador.** Rendimiento excepcional en un solo hilo para esta tarea. |
| **.NET (Release)** | **1.540 s** | 🥈 **Empate técnico.** Rendimiento de primer nivel con optimizaciones completas. |
| **.NET (Debug)** | **2.249 s** | 🥉 **Más rápido que Node.js,** incluso sin las optimizaciones del modo Release. |
| **Node.js** | **4.407 s** | 🐢 El más lento en este escenario CPU-bound, casi 3 veces más que sus competidores directos. |

![Gráfico de Resultados](benchmark-result.png)
*Gráfico comparando los mejores tiempos de ejecución. Menor es mejor.*

---

## Análisis Detallado de los Resultados

### ¿Por qué Bun es tan rápido?

El sorprendente rendimiento de Bun se debe a dos factores clave:

1.  **Motor JavaScriptCore (JSC):** A diferencia de Node.js (V8), Bun utiliza el motor de Safari (JSC). Para este tipo de bucle numérico "caliente", el compilador JIT de JSC demuestra ser extraordinariamente eficiente.
2.  **Implementación Nativa en Zig:** Gran parte de las APIs de Bun, incluyendo `Map`, están reescritas en Zig, un lenguaje de bajo nivel. Esto reduce la sobrecarga y optimiza al máximo operaciones críticas.

### .NET: Potencia y Consistencia

.NET confirma su estatus como una plataforma de alto rendimiento.

*   **Compilador RyuJIT:** En modo `Release`, el compilador de .NET realiza optimizaciones muy avanzadas, llevando el código C# a un rendimiento de élite. El hecho de que supere a Node.js incluso en modo Debug es un testimonio de la eficiencia base del runtime.
*   **Potencial de Escalabilidad:** .NET tiene una ventaja no explotada aquí: el paralelismo. Con un simple cambio a `Parallel.For`, podría haber distribuido la carga entre todos los cores, reduciendo drásticamente el tiempo de ejecución.

### ¿Por qué Node.js se queda atrás en *esta* prueba?

Este resultado no significa que Node.js sea lento. Node.js es una plataforma increíblemente rápida para su principal caso de uso: **aplicaciones I/O-bound** (servidores web, APIs).

Sin embargo, este benchmark es **100% CPU-bound**. En este escenario, las optimizaciones del motor V8 no resultan tan eficaces como las de Bun o .NET.

## Conclusiones

1.  **Bun es un competidor formidable:** Para tareas de cómputo intensivo en JavaScript, Bun ofrece un rendimiento de vanguardia que desafía a los runtimes más establecidos.

2.  **.NET es un pilar de rendimiento:** Sigue siendo una de las mejores opciones para backends que requieren alto rendimiento, con un ecosistema maduro y excelentes capacidades de paralelización.

3.  **Elige la herramienta adecuada para el trabajo:**
    *   Para **servidores API y tareas asíncronas (I/O)**, **Node.js** sigue siendo una opción excelente y robusta.
    *   Para **algoritmos de procesamiento de datos y tareas CPU-intensivas**, **.NET** o **Bun** ofrecen un rendimiento significativamente superior.

4.  **¡Compila en modo Release!** La diferencia entre .NET Debug (**2.249 s**) y Release (**1.540 s**) es una **mejora de rendimiento de casi el 50%**. Esto subraya la importancia crítica de nunca medir el rendimiento en una compilación de desarrollo.