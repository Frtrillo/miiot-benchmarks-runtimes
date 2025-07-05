// Program.cs
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

/// <summary>
/// Representa la estructura de datos completa de un registro de log,
/// coincidiendo con todo el esquema de la tabla de la base de datos.
/// </summary>
public class HistoryLogFull
{
    public string VinculationCode { get; set; } = "VINC-CODE-BENCHMARK";
    public DateTime CreatedAt { get; set; }
    public short? Premium { get; set; }
    public float? Temperature { get; set; }
    public float? Temperature2 { get; set; }
    public float? Temperature3 { get; set; }
    public short? Humidity { get; set; }
    public bool? PowerOn { get; set; }
    public bool? Fan { get; set; }
    public bool? Pump { get; set; }
    public float? Speco { get; set; }
    public float? Spdhw { get; set; }
    public short? Defrost { get; set; }
    public short? Spantilegionella { get; set; }
    public bool? ExpansionValveOut { get; set; }
    public int? ModbusGlobalFails { get; set; }
    public float? RefrigerantLiquid { get; set; }
    public short? AlarmDischargeTemp { get; set; }
    public short? AlarmDriverInverter { get; set; }
    public short? CompressorPercentage { get; set; }
    public float? DischargeTemperature { get; set; }
    public short? AntilegionellaDayWeek { get; set; }
    public float? PumpFlow { get; set; }
    public float? Pr1 { get; set; }
    public float? Pr2 { get; set; }
    public float? Pr3 { get; set; }
    public bool? Compressor { get; set; }
    public float? CompressorHz { get; set; }
    public short? AuxGroupWatts { get; set; }
    public short? AutoMode { get; set; }
    public short? HeatFocus { get; set; }
    public float? TempHeating { get; set; }
    public float? ReturnTemp { get; set; }
    public short? OpModeHeating { get; set; }
    public float? EvaporatorTemp { get; set; }
    public float? EvaporationTemp { get; set; }
    public float? AirInlet { get; set; }
    public float? AirOutlet { get; set; }
    public float? SetPointAir { get; set; }
    public float? OutsideTemp { get; set; }
    public short? AlarmActive { get; set; }
    public float? ActivePower { get; set; }
    public float? TotalActivePower { get; set; }
    public float? T1Temp { get; set; }
    public float? T2Temp { get; set; }
    public float? ThermalDiff { get; set; }
    public float? ThermalPower { get; set; }
    public float? TotalThermalEnergy { get; set; }
    public float? LitersPerHourFlow { get; set; }
    public float? CopInstant { get; set; }
    public short? PumpPercent { get; set; }
    public bool? Resistors { get; set; }
    public float? SpHeating { get; set; }
    public short? SpMode { get; set; }
    public short? DhwSpMode { get; set; }
    public float? AutoMinSpDhw { get; set; }
    public short? AlarmPumpFlow { get; set; }
    public short? AlarmAdcBoard { get; set; }
    public short? AlarmLp { get; set; }
    public short? AlarmHp { get; set; }
    public float? Sp1 { get; set; }
    public short? PowerOnHeating { get; set; }
    public bool? Antilegionella { get; set; }
    public float? Z1Sp { get; set; }
    public float? Z2Sp { get; set; }
    public short? PowerOnDhw { get; set; }
    public short? CoolingHeatingMode { get; set; }
    public bool? ValveZ1 { get; set; }
    public bool? ValveZ2 { get; set; }
    public float? TotalizerWh { get; set; }
    public short? GasSuction { get; set; }
    public short? RunningFanPercent { get; set; }
}

/// <summary>
/// Clase mutable para agregar los valores de forma eficiente.
/// </summary>
public class BucketAggregate
{
    public double SumTemperature { get; set; }
    public int CountTemperature { get; set; }
    public double SumActivePower { get; set; }
    public int CountActivePower { get; set; }
    public double SumCompressorHz { get; set; }
    public int CountCompressorHz { get; set; }
    public int AlarmCount { get; set; }
    public int TotalCount { get; set; }
}

class Program
{
    static void Main()
    {
        int n = 50000000;
        var interval = TimeSpan.FromMinutes(10);

        Console.WriteLine($"Procesando {n:N0} registros con ESQUEMA COMPLETO...");

        var sw = Stopwatch.StartNew();
        var results = ProcessComplexLogsOnTheFly(n, interval);
        sw.Stop();

        Console.WriteLine($"C# .NET (Optimizado) - Tiempo: {sw.Elapsed.TotalSeconds:F3} s");
        Console.WriteLine($"Buckets calculados: {results.Count}");
    }

    static List<object> ProcessComplexLogsOnTheFly(int n, TimeSpan interval)
    {
        var grouped = new Dictionary<long, BucketAggregate>();
        var start = DateTime.Now.AddDays(-7);
        var rnd = new Random();

        for (int i = 0; i < n; i++)
        {
            var timestamp = start.AddMinutes(i);
            var log = GenerateFullHistoryLog(rnd, timestamp);

            var bucket = (long)(timestamp.Ticks / interval.Ticks) * interval.Ticks;

            if (!grouped.TryGetValue(bucket, out var aggregate))
            {
                aggregate = new BucketAggregate();
                grouped[bucket] = aggregate;
            }

            ProcessLog(aggregate, log);
        }

        // El cálculo final
        return grouped.Values.Select(g => new
        {
            AvgTemperature = g.CountTemperature > 0 ? g.SumTemperature / g.CountTemperature : 0,
            AvgActivePower = g.CountActivePower > 0 ? g.SumActivePower / g.CountActivePower : 0,
            AvgCompressorHz = g.CountCompressorHz > 0 ? g.SumCompressorHz / g.CountCompressorHz : 0,
            AlarmRatio = g.TotalCount > 0 ? (double)g.AlarmCount / g.TotalCount : 0
        }).Cast<object>().ToList();
    }
    
    /// <summary>
    /// Agrega un único log al bucket correspondiente.
    /// </summary>
    static void ProcessLog(BucketAggregate aggregate, HistoryLogFull log)
    {
        if (log.Temperature.HasValue)
        {
            aggregate.SumTemperature += log.Temperature.Value;
            aggregate.CountTemperature++;
        }
        if (log.ActivePower.HasValue)
        {
            aggregate.SumActivePower += log.ActivePower.Value;
            aggregate.CountActivePower++;
        }
        if (log.CompressorHz.HasValue)
        {
            aggregate.SumCompressorHz += log.CompressorHz.Value;
            aggregate.CountCompressorHz++;
        }
        if (log.AlarmActive.HasValue && log.AlarmActive.Value > 0)
        {
            aggregate.AlarmCount++;
        }
        aggregate.TotalCount++;
    }

    /// <summary>
    /// Generador para el objeto completo y pesado. Usa un enfoque directo para máximo rendimiento.
    /// </summary>
    private static HistoryLogFull GenerateFullHistoryLog(Random rnd, DateTime timestamp)
    {
        return new HistoryLogFull
        {
            CreatedAt = timestamp,
            Premium = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            Temperature = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 40) : null,
            Temperature2 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 40) : null,
            Temperature3 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 40) : null,
            Humidity = rnd.NextDouble() > 0.1 ? (short)rnd.Next(100) : null,
            PowerOn = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            Fan = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            Pump = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            Speco = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 10) : null,
            Spdhw = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 60) : null,
            Defrost = rnd.NextDouble() > 0.1 ? (short)rnd.Next(5) : null,
            Spantilegionella = rnd.NextDouble() > 0.1 ? (short)rnd.Next(70) : null,
            ExpansionValveOut = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            ModbusGlobalFails = rnd.NextDouble() > 0.1 ? rnd.Next(10) : null,
            RefrigerantLiquid = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 5) : null,
            AlarmDischargeTemp = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            AlarmDriverInverter = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            CompressorPercentage = rnd.NextDouble() > 0.1 ? (short)rnd.Next(100) : null,
            DischargeTemperature = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 120) : null,
            AntilegionellaDayWeek = rnd.NextDouble() > 0.1 ? (short)rnd.Next(7) : null,
            PumpFlow = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 1000) : null,
            Pr1 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 30) : null,
            Pr2 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 30) : null,
            Pr3 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 30) : null,
            Compressor = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            CompressorHz = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 90) : null,
            AuxGroupWatts = rnd.NextDouble() > 0.1 ? (short)rnd.Next(6000) : null,
            AutoMode = rnd.NextDouble() > 0.1 ? (short)rnd.Next(3) : null,
            HeatFocus = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            TempHeating = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 50) : null,
            ReturnTemp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 45) : null,
            OpModeHeating = rnd.NextDouble() > 0.1 ? (short)rnd.Next(4) : null,
            EvaporatorTemp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 10) : null,
            EvaporationTemp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 5) : null,
            AirInlet = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 25) : null,
            AirOutlet = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 15) : null,
            SetPointAir = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 22) : null,
            OutsideTemp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 45) : null,
            AlarmActive = rnd.NextDouble() > 0.1 ? (short)(rnd.NextDouble() < 0.02 ? rnd.Next(10) : 0) : null,
            ActivePower = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 3500) : null,
            TotalActivePower = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 100000) : null,
            T1Temp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 50) : null,
            T2Temp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 40) : null,
            ThermalDiff = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 10) : null,
            ThermalPower = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 5000) : null,
            TotalThermalEnergy = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 200000) : null,
            LitersPerHourFlow = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 1500) : null,
            CopInstant = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 6) : null,
            PumpPercent = rnd.NextDouble() > 0.1 ? (short)rnd.Next(100) : null,
            Resistors = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            SpHeating = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 55) : null,
            SpMode = rnd.NextDouble() > 0.1 ? (short)rnd.Next(3) : null,
            DhwSpMode = rnd.NextDouble() > 0.1 ? (short)rnd.Next(3) : null,
            AutoMinSpDhw = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 45) : null,
            AlarmPumpFlow = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            AlarmAdcBoard = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            AlarmLp = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            AlarmHp = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            Sp1 = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 50) : null,
            PowerOnHeating = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            Antilegionella = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            Z1Sp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 25) : null,
            Z2Sp = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 25) : null,
            PowerOnDhw = rnd.NextDouble() > 0.1 ? (short)rnd.Next(2) : null,
            CoolingHeatingMode = rnd.NextDouble() > 0.1 ? (short)rnd.Next(3) : null,
            ValveZ1 = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            ValveZ2 = rnd.NextDouble() > 0.1 ? rnd.Next(2) == 0 : null,
            TotalizerWh = rnd.NextDouble() > 0.1 ? (float)(rnd.NextDouble() * 300000) : null,
            GasSuction = rnd.NextDouble() > 0.1 ? (short)rnd.Next(10) : null,
            RunningFanPercent = rnd.NextDouble() > 0.1 ? (short)rnd.Next(100) : null
        };
    }
}