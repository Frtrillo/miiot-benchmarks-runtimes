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
*   **Ejecución en un Solo Hilo:** Todas las pruebas se ejecutaron utilizando **un único hilo de procesamiento (single-thread)**. Aunque el entorno de .NET tenía acceso a múltiples cores, el código del benchmark, por diseño, solo utilizó uno, al igual que Bun y Node.js. Esto garantiza una comparación **justa y equitativa** del rendimiento por núcleo de cada runtime.
*   **Compilación Optimizada:** La prueba de .NET se ejecutó en modo **Release**, que aplica las máximas optimizaciones del compilador, tal como se haría en un entorno de producción.

## Cómo Ejecutar el Benchmark

Para replicar estos resultados, puedes usar los siguientes comandos desde la raíz del proyecto. Asegúrate de tener instalados .NET, Node.js y Bun.

### .NET (Modo Release - Optimizado)
Este comando compila y ejecuta el proyecto en su configuración más rápida.

```powershell
dotnet run -c Release --project ./dotnet-mastica-historial/dotnet-mastica-historial.csproj
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
| **.NET (Release)** | **1.540 s** | 🥈 **Empate técnico.** Demuestra un rendimiento de primer nivel, casi idéntico al de Bun. |
| **Node.js** | **4.407 s** | 🐢 El más lento en este escenario, casi 3 veces más que sus competidores. |

![Gráfico de Resultados](benchmark-result.png)
*Gráfico comparando los mejores tiempos de ejecución. Menor es mejor.*

---

## Análisis Detallado de los Resultados

### ¿Por qué Bun es tan rápido?

El sorprendente rendimiento de Bun no es casualidad y se debe a dos factores clave:

1.  **Motor JavaScriptCore (JSC):** A diferencia de Node.js que usa V8 (de Google), Bun utiliza el motor de Safari (de Apple). Para este tipo de carga de trabajo específica —un bucle numérico "caliente" con constantes operaciones matemáticas—, el compilador JIT de JSC demuestra ser extraordinariamente eficiente.
2.  **Implementación Nativa en Zig:** Gran parte de las APIs de Bun, incluyendo `Map` y otras funciones internas, están reescritas en Zig, un lenguaje de bajo nivel. Esto reduce la sobrecarga y optimiza al máximo operaciones críticas como las que realiza el benchmark, dándole una ventaja medible.

### .NET: Potencia y Consistencia

.NET confirma su estatus como una plataforma de alto rendimiento.

*   **Compilador RyuJIT:** En modo `Release`, el compilador de .NET realiza optimizaciones muy avanzadas, llevando el código C# a un rendimiento casi a la par del metal.
*   **Potencial de Escalabilidad:** Es crucial notar que .NET tiene una ventaja no explotada en este benchmark: el paralelismo. Con un simple cambio en el código (usando `Parallel.For`), .NET podría haber distribuido la carga entre todos los cores disponibles, reduciendo drásticamente el tiempo de ejecución y superando a todos por un amplio margen.

### ¿Por qué Node.js se queda atrás en *esta* prueba?

Este resultado no significa que Node.js sea lento. Node.js es una plataforma increíblemente rápida y eficiente para su principal caso de uso: **aplicaciones I/O-bound** (servidores web, APIs, microservicios).

Sin embargo, este benchmark es **100% CPU-bound**. Es un bucle numérico que no espera por nada. En este escenario específico, las optimizaciones del motor V8 y la arquitectura general de Node.js no resultan tan eficaces como las de Bun o .NET.

## Conclusiones

1.  **Bun es un competidor formidable:** Para tareas de cómputo intensivo en JavaScript, Bun no es solo marketing. Ofrece un rendimiento de vanguardia que puede superar a runtimes muy establecidos.

2.  **.NET es un pilar de rendimiento:** Sigue siendo una de las mejores opciones para backends que requieren un alto rendimiento computacional, con la ventaja añadida de un ecosistema maduro y excelentes capacidades de paralelización.

3.  **Elige la herramienta adecuada para el trabajo:**
    *   Para **servidores API y tareas asíncronas (I/O)**, **Node.js** sigue siendo una opción excelente y robusta.
    *   Para **algoritmos de procesamiento de datos y tareas CPU-intensivas**, **.NET** o **Bun** ofrecen un rendimiento significativamente superior.

4.  **Optimiza siempre para producción:** La diferencia entre el modo Debug y Release en .NET es sustancial. Mide siempre el rendimiento con las mismas optimizaciones que usarías en producción.