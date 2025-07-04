// Función generadora que "cede" un log a la vez, sin crear un array.
function* generateLogsStream(n) {
  const start = Date.now() - 7 * 24 * 60 * 60 * 1000; // 7 días atrás
  for (let i = 0; i < n; i++) {
    // 'yield' entrega el objeto y pausa la función hasta la siguiente iteración del bucle que lo consume.
    yield {
      timestamp: start + i * 60000,
      value: Math.random() * 100,
      // Los otros campos no son necesarios para el cálculo, así que podemos omitirlos
      // para que la generación sea aún más rápida en ambos lenguajes.
      // Si los necesitaras, los añadirías aquí.
    };
  }
}

function benchmarkOptimized() {
  const n = 50000000;
  const interval = 10 * 60 * 1000; // 10 minutos en ms

  console.log("Iniciando benchmark optimizado de Node.js/Bun...");
  const start = performance.now();

  const grouped = {};
  
  // Creamos el generador. No se ejecuta nada todavía.
  const logGenerator = generateLogsStream(n);

  // El bucle 'for...of' consumirá el generador, pidiendo un log a la vez.
  // ¡No se almacena ningún array de 50 millones de logs!
  for (const log of logGenerator) {
    const bucket = Math.floor(log.timestamp / interval) * interval;
    if (!grouped[bucket]) {
      grouped[bucket] = { sum: 0, count: 0 };
    }
    grouped[bucket].sum += log.value;
    grouped[bucket].count++;
  }

  const averages = Object.values(grouped).map(g => g.sum / g.count);

  const end = performance.now();
  console.log(`Bun/Node JS (Optimizado) - Tiempo: ${((end - start) / 1000).toFixed(3)} s`);
  console.log(`Buckets calculados: ${averages.length}`);
}

benchmarkOptimized();