// aot-processing.js

// Importamos performance de perf_hooks para la medición más precisa
const { performance } = require('perf_hooks');

function processLogsOnTheFly(n, intervalMilliseconds) {
    // Map es el equivalente de alto rendimiento a Dictionary<,> en C#
    const grouped = new Map();

    // Establecemos una fecha de inicio, igual que en el ejemplo de C#
    const start = new Date();
    start.setDate(start.getDate() - 7);
    const startTime = start.getTime(); // Obtenemos el tiempo en ms para cálculos más rápidos

    // El bucle principal que genera y procesa los datos "al vuelo"
    for (let i = 0; i < n; i++) {
        // 1. Genera los datos para esta iteración
        // Sumamos i minutos (en milisegundos) a la fecha de inicio
        const timestamp_ms = startTime + (i * 60 * 1000);
        const value = Math.random() * 100;

        // 2. Lógica de procesamiento idéntica a la de C#
        // Se calcula el "bucket" truncando la división del tiempo por el intervalo.
        // Math.floor es el equivalente al casting a (long) para la división entera.
        const bucket = Math.floor(timestamp_ms / intervalMilliseconds) * intervalMilliseconds;

        // 3. Agrupación eficiente
        // Usamos Map.get() que es muy rápido.
        const current = grouped.get(bucket);

        if (!current) {
            // Si el bucket no existe, lo creamos
            grouped.set(bucket, { sum: value, count: 1 });
        } else {
            // Si ya existe, actualizamos los valores
            current.sum += value;
            current.count += 1;
            // No es necesario volver a hacer grouped.set() porque el objeto se modifica por referencia,
            // pero lo dejamos para claridad si se usaran tipos primitivos.
            // grouped.set(bucket, current); 
        }
    }

    // 4. Cálculo final de los promedios
    // Array.from(grouped.values()) convierte el iterador de valores del Map en un array.
    // Luego usamos .map() para calcular el promedio de cada grupo.
    return Array.from(grouped.values()).map(g => g.sum / g.count);
}

function main() {
    const n = 150000000;
    const intervalMinutes = 10;
    const intervalMilliseconds = intervalMinutes * 60 * 1000;

    const startTime = performance.now();

    const averages = processLogsOnTheFly(n, intervalMilliseconds);

    const endTime = performance.now();
    const durationSeconds = (endTime - startTime) / 1000;

    console.log(`Node.js - Tiempo: ${durationSeconds.toFixed(3)} s`);
    console.log(`Buckets calculados: ${averages.length}`);
}

main();