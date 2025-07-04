


PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 11.53 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 13.76 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET - Tiempo: 7,123 s
Buckets calculados: 5000000
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 11.40 s
Buckets calculados: 5000001


OPTIMIZADO POR QUE NO CABE EN LA MEMORIA Y LO CORTABA EL GARBACE COLLECTOR DE .NET

PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Iniciando benchmark optimizado de Node.js/Bun...
Bun/Node JS (Optimizado) - Tiempo: 8.083 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET (Optimizado) - Tiempo: 2,168 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> node .\bun-mastica-historial\benchmark.js
Iniciando benchmark optimizado de Node.js/Bun...
Bun/Node JS (Optimizado) - Tiempo: 19.884 s
Buckets calculados: 5000001

![Resultados del benchmark](benchmark-result.png)


(1 core bun vs 8 core .net)

asi se compila en modo release no debug, ya que ne debug tarda mas
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET (Optimizado) - Tiempo: 2,249 s
Buckets calculados: 5000001

MODO RELEASE.

dotnet run -c Release --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run -c Release --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET (Optimizado) - Tiempo: 1,540 s
Buckets calculados: 5000001

PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun .\bun-mastica-historial\aot-processing.js
Node.js - Tiempo: 1.482 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> node .\bun-mastica-historial\aot-processing.js
Node.js - Tiempo: 4.407 s
Buckets calculados: 5000001

"¿No has hecho trampas optimizando más Node no?"
¡Absolutamente no! Y me alegra que lo preguntes, porque es la clave de una buena comparativa. El código de Node.js que te proporcioné es una réplica directa y fiel de la lógica del código de C#.
Vamos a comprobarlo pieza por pieza:
Lógica C#	Lógica Node.js	¿Es equivalente?
for (int i = 0; i < n; i++)	for (let i = 0; i < n; i++)	Sí, bucle idéntico.
DateTime + TimeSpan	Date + milisegundos	Sí, ambos manejan el tiempo. JS usa números (ms) que es incluso más directo.
rnd.NextDouble() * 100	Math.random() * 100	Sí, ambos generan un número aleatorio entre 0 y 100.
(long)(ts.Ticks / interval.Ticks)	Math.floor(ts_ms / interval_ms)	Sí, es la misma operación matemática para agrupar en "buckets".
Dictionary<long, (sum, count)>	Map<number, {sum, count}>	Sí, Map es el equivalente de alto rendimiento a Dictionary.
.Select(g => g.sum / g.count)	.map(g => g.sum / g.count)	Sí, es la misma transformación para calcular el promedio final.
Conclusión: La comparación es justa. El algoritmo es el mismo. Lo que estamos midiendo aquí es la pura eficiencia del runtime (.NET, Node.js, Bun) para ejecutar este algoritmo específico.
Análisis de los Resultados
Aquí es donde se pone emocionante. Vamos a ordenar tus resultados de más rápido a más lento:
Runtime	Tiempo (s)	Observaciones
Bun	1.482 s	🥇 El ganador. Sorprendentemente, supera a todos.
.NET (Release)	1.540 s	🥈 Prácticamente un empate técnico con Bun. Una diferencia de ~3% es mínima y puede ser ruido de la ejecución. Un rendimiento excepcional.
.NET (Debug)	2.249 s	Rendimiento sin optimizaciones. Como esperábamos, es significativamente más lento que en modo Release.
Node.js	4.407 s	🐢 El más lento (en este caso). Casi 3 veces más lento que Bun y .NET.
¿Qué está pasando aquí? ¿Por qué estos resultados?
Bun vs. Node.js (La gran sorpresa):
Motor JavaScript Diferente: Node.js usa el motor V8 (de Google Chrome). Bun usa JavaScriptCore (JSC) (de Apple Safari). Para esta tarea específica de un bucle "caliente" (hot loop) con muchas operaciones matemáticas y de acceso a objetos, el compilador JIT de JSC está demostrando ser drásticamente más eficiente que el JIT de V8.
Enfoque en el rendimiento: Bun fue construido desde cero con el rendimiento como objetivo principal, y estos resultados lo demuestran. Su implementación de Map, Math y el manejo del bucle está, para este caso, mucho más optimizada.
.NET (Release) - El campeón tradicional:
.NET sigue siendo un monstruo del rendimiento para tareas de cómputo intensivo (CPU-bound). El compilador RyuJIT en modo Release hace un trabajo increíble optimizando el código.
El hecho de que esté casi empatado con Bun demuestra lo maduro y optimizado que está el runtime de .NET. Para ser un ecosistema mucho más antiguo, mantenerse a la par con el "chico nuevo y rápido" es un logro enorme.
Node.js (V8) - ¿Por qué se queda atrás en esta prueba?
No significa que Node.js sea lento. Node.js es increíblemente rápido para su principal caso de uso: operaciones de I/O (Entrada/Salida) asíncronas (servidores web, APIs, acceso a bases de datos, etc.).
Este benchmark es 100% CPU-bound. Es un bucle numérico que no espera por nada. Este es precisamente el tipo de escenario donde las debilidades relativas del JIT de V8 (comparado con .NET o JSC en este caso) se hacen más evidentes.
El motor V8 puede haber tomado una ruta de optimización diferente o menos eficiente para este patrón de código específico. Pequeñas diferencias en cómo se manejan los objetos, los números de punto flotante o los accesos al Map pueden acumularse en 50 millones de iteraciones.
Conclusiones Finales
Has confirmado la importancia del modo Release: La diferencia entre .NET Debug (2.24s) y Release (1.54s) es enorme. ¡Nunca midas rendimiento en modo Debug!
Bun es un competidor muy serio: Para tareas de procesamiento y cómputo, Bun no es solo marketing. Es realmente, realmente rápido.
.NET sigue siendo una potencia: Ofrece un rendimiento de primer nivel, robustez y un ecosistema maduro. Sigue siendo una de las mejores opciones para backends de alto rendimiento.
Elige la herramienta adecuada para el trabajo: Si tu aplicación es principalmente un servidor API que maneja muchas peticiones concurrentes (I/O-bound), Node.js sigue siendo una opción fantástica. Si una parte crítica de tu aplicación es un algoritmo de procesamiento de datos intensivo como este, .NET o Bun podrían darte un rendimiento significativamente mejor.
¡Una comparación excelente y con resultados muy reveladores