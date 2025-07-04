using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

class Program
{
    static void Main()
    {
        var logs = GenerateLogs(5000000);
        var interval = TimeSpan.FromMinutes(10);

        var sw = Stopwatch.StartNew();

        var grouped = new Dictionary<long, (double sum, int count)>();

        foreach (var log in logs)
        {
            var bucket = (long)(log.Timestamp.Ticks / interval.Ticks) * interval.Ticks;
            if (!grouped.ContainsKey(bucket))
            {
                grouped[bucket] = (0, 0);
            }
            var current = grouped[bucket];
            grouped[bucket] = (current.sum + log.Value, current.count + 1);
        }

        var averages = grouped.Values.Select(g => g.sum / g.count).ToList();

        sw.Stop();
        Console.WriteLine($"C# .NET - Tiempo: {sw.ElapsedMilliseconds} ms");
        Console.WriteLine($"Buckets calculados: {averages.Count}");
    }

    static List<Log> GenerateLogs(int n)
    {
        var logs = new List<Log>(n);
        var start = DateTime.Now.AddDays(-7);
        var rnd = new Random();

        for (int i = 0; i < n; i++)
        {
            logs.Add(new Log
            {
                Timestamp = start.AddMinutes(i),
                Value = rnd.NextDouble() * 100,
                UserId = rnd.Next(0, 1000),
                Action = rnd.NextDouble() > 0.5 ? "click" : "view",
                Temperature = rnd.NextDouble() * 30 + 15,
                Temperature2 = rnd.NextDouble() * 30 + 15,
                Name = $"User{rnd.Next(0,1000)}",
                Surname = $"Surname{rnd.Next(0,1000)}",
                Email = $"user{rnd.Next(0,1000)}@example.com"
            });
        }

        return logs;
    }
}

class Log
{
    public DateTime Timestamp { get; set; }
    public double Value { get; set; }
    public int UserId { get; set; }
    public string Action { get; set; } = "";
    public double Temperature { get; set; }
    public double Temperature2 { get; set; }
    public string Name { get; set; } = "";
    public string Surname { get; set; } = "";
    public string Email { get; set; } = "";
}
