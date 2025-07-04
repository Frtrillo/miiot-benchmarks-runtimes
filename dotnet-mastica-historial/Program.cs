using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

class Program
{
    static void Main()
    {
        int n = 150000000;
        var interval = TimeSpan.FromMinutes(10);
        var sw = Stopwatch.StartNew();

        // Procesa los datos "al vuelo" sin almacenarlos en una lista
        var averages = ProcessLogsOnTheFly(n, interval);

        sw.Stop();
        Console.WriteLine($"C# .NET (Optimizado) - Tiempo: {sw.Elapsed.TotalSeconds:F3} s");
        Console.WriteLine($"Buckets calculados: {averages.Count}");
    }

    static List<double> ProcessLogsOnTheFly(int n, TimeSpan interval)
    {
        var grouped = new Dictionary<long, (double sum, int count)>();
        var start = DateTime.Now.AddDays(-7);
        var rnd = new Random();

        // No creamos una lista gigante. Generamos y procesamos en el mismo bucle.
        for (int i = 0; i < n; i++)
        {
            // Genera solo los datos que necesitas para esta iteración
            var timestamp = start.AddMinutes(i);
            var value = rnd.NextDouble() * 100;

            // El resto de la lógica de procesamiento es idéntica
            var bucket = (long)(timestamp.Ticks / interval.Ticks) * interval.Ticks;
            
            // Usamos TryGetValue para ser un poco más eficientes que ContainsKey + indexer
            if (!grouped.TryGetValue(bucket, out var current))
            {
                current = (0, 0);
            }
            
            grouped[bucket] = (current.sum + value, current.count + 1);
        }

        // El cálculo final es el mismo
        return grouped.Values.Select(g => g.sum / g.count).ToList();
    }
}