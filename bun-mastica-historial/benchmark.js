function generateLogs(n) {
  const logs = [];
  const start = Date.now() - 7 * 24 * 60 * 60 * 1000; // 7 días atrás
  for (let i = 0; i < n; i++) {
    logs.push({
      timestamp: start + i * 60000, // 1 log por minuto
      value: Math.random() * 100,
        userId: Math.floor(Math.random() * 1000), // Simula 1000 usuarios
        action: Math.random() > 0.5 ? 'click' : 'view', // Simula dos tipos de acciones
        temperature: Math.random() * 30 + 15, // Temperatura entre 15 y 45 grados
        temperature2: Math.random() * 30 + 15, // Temperatura entre 15 y 45 grados
        name: `User${Math.floor(Math.random() * 1000)}`, // Nombre de usuario simulado
        surname: `Surname${Math.floor(Math.random() * 1000)}`, // Apellido de usuario simulado
        email: `user${Math.floor(Math.random() * 1000)}@example.com`, // Email simulado
    });
  }
  return logs;
}

function benchmark() {
  const logs = generateLogs(5000000);
  const interval = 10 * 60 * 1000; // 10 minutos en ms

  const start = performance.now();

  const grouped = {};
  for (const log of logs) {
    const bucket = Math.floor(log.timestamp / interval) * interval;
    if (!grouped[bucket]) {
      grouped[bucket] = { sum: 0, count: 0 };
    }
    grouped[bucket].sum += log.value;
    grouped[bucket].count++;
  }

  const averages = Object.values(grouped).map(g => g.sum / g.count);

  const end = performance.now();
  console.log(`Bun JS - Tiempo: ${(end - start).toFixed(2)} ms`);
  console.log(`Buckets calculados: ${averages.length}`);
}

benchmark();
