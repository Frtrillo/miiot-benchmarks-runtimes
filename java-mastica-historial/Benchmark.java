// File: BenchmarkFairFight.java

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Benchmark {

    // A lightweight, primitive-based data holder WITH ALL 70+ FIELDS.
    // This now matches the workload of the .NET and Node.js versions.
    static class HistoryLogFullPrimitive {
        // --- Fields used in aggregation ---
        float temperature;              boolean hasTemperature;
        float activePower;              boolean hasActivePower;
        float compressorHz;             boolean hasCompressorHz;
        short alarmActive;              boolean hasAlarmActive;

        // --- All other fields, to make the data generation work equivalent ---
        String vinculationCode = "VINC-CODE-BENCHMARK";
        // long createdAt; // We handle this separately as a primitive in the loop
        short premium;                  boolean hasPremium;
        float temperature2;             boolean hasTemperature2;
        float temperature3;             boolean hasTemperature3;
        short humidity;                 boolean hasHumidity;
        boolean powerOn;                boolean hasPowerOn;
        boolean fan;                    boolean hasFan;
        boolean pump;                   boolean hasPump;
        float speco;                    boolean hasSpeco;
        float spdhw;                    boolean hasSpdhw;
        short defrost;                  boolean hasDefrost;
        short spantilegionella;         boolean hasSpantilegionella;
        boolean expansionValveOut;      boolean hasExpansionValveOut;
        int modbusGlobalFails;          boolean hasModbusGlobalFails;
        float refrigerantLiquid;        boolean hasRefrigerantLiquid;
        short alarmDischargeTemp;       boolean hasAlarmDischargeTemp;
        short alarmDriverInverter;      boolean hasAlarmDriverInverter;
        short compressorPercentage;     boolean hasCompressorPercentage;
        float dischargeTemperature;     boolean hasDischargeTemperature;
        short antilegionellaDayWeek;    boolean hasAntilegionellaDayWeek;
        float pumpFlow;                 boolean hasPumpFlow;
        float pr1;                      boolean hasPr1;
        float pr2;                      boolean hasPr2;
        float pr3;                      boolean hasPr3;
        boolean compressor;             boolean hasCompressor;
        short auxGroupWatts;            boolean hasAuxGroupWatts;
        short autoMode;                 boolean hasAutoMode;
        short heatFocus;                boolean hasHeatFocus;
        float tempHeating;              boolean hasTempHeating;
        float returnTemp;               boolean hasReturnTemp;
        short opModeHeating;            boolean hasOpModeHeating;
        float evaporatorTemp;           boolean hasEvaporatorTemp;
        float evaporationTemp;          boolean hasEvaporationTemp;
        float airInlet;                 boolean hasAirInlet;
        float airOutlet;                boolean hasAirOutlet;
        float setPointAir;              boolean hasSetPointAir;
        float outsideTemp;              boolean hasOutsideTemp;
        float totalActivePower;         boolean hasTotalActivePower;
        float t1Temp;                   boolean hasT1Temp;
        float t2Temp;                   boolean hasT2Temp;
        float thermalDiff;              boolean hasThermalDiff;
        float thermalPower;             boolean hasThermalPower;
        float totalThermalEnergy;       boolean hasTotalThermalEnergy;
        float litersPerHourFlow;        boolean hasLitersPerHourFlow;
        float copInstant;               boolean hasCopInstant;
        short pumpPercent;              boolean hasPumpPercent;
        boolean resistors;              boolean hasResistors;
        float spHeating;                boolean hasSpHeating;
        short spMode;                   boolean hasSpMode;
        short dhwSpMode;                boolean hasDhwSpMode;
        float autoMinSpDhw;             boolean hasAutoMinSpDhw;
        short alarmPumpFlow;            boolean hasAlarmPumpFlow;
        short alarmAdcBoard;            boolean hasAlarmAdcBoard;
        short alarmLp;                  boolean hasAlarmLp;
        short alarmHp;                  boolean hasAlarmHp;
        float sp1;                      boolean hasSp1;
        short powerOnHeating;           boolean hasPowerOnHeating;
        boolean antilegionella;         boolean hasAntilegionella;
        float z1Sp;                     boolean hasZ1Sp;
        float z2Sp;                     boolean hasZ2Sp;
        short powerOnDhw;               boolean hasPowerOnDhw;
        short coolingHeatingMode;       boolean hasCoolingHeatingMode;
        boolean valveZ1;                boolean hasValveZ1;
        boolean valveZ2;                boolean hasValveZ2;
        float totalizerWh;              boolean hasTotalizerWh;
        short gasSuction;               boolean hasGasSuction;
        short runningFanPercent;        boolean hasRunningFanPercent;
    }

    static class BucketAggregate {
        public double sumTemperature, sumActivePower, sumCompressorHz;
        public int countTemperature, countActivePower, countCompressorHz, alarmCount, totalCount;
    }

    record FinalResult(double avgTemperature, double avgActivePower, double avgCompressorHz, double alarmRatio) {}

    public static void main(String[] args) {
        int n = 50_000_000;
        long intervalNanos = Duration.ofMinutes(10).toNanos();

        System.out.printf("--- Running BenchmarkFairFight (Full Object, Primitives) ---%n");
        System.out.printf("Processing %,d records with FULL SCHEMA...%n", n);
        
        System.out.println("Warming up the JVM...");
        processComplexLogsOnTheFly(10_000_000, intervalNanos);
        System.out.println("Warm-up complete. Starting benchmark.");
        
        System.gc();

        long startTime = System.nanoTime();
        var results = processComplexLogsOnTheFly(n, intervalNanos);
        long endTime = System.nanoTime();

        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;

        System.out.printf("Java (Fair Fight) - Time: %.3f s%n", elapsedSeconds);
        System.out.printf("Buckets calculated: %d%n", results.size());
    }

    static List<FinalResult> processComplexLogsOnTheFly(int n, long intervalNanos) {
        var grouped = new HashMap<Long, BucketAggregate>(1_000_000);
        long startNanos = System.currentTimeMillis() * 1_000_000L;
        long minuteNanos = 60 * 1_000_000_000L;
        var rnd = ThreadLocalRandom.current();
        var log = new HistoryLogFullPrimitive(); // REUSE the same object

        for (int i = 0; i < n; i++) {
            long timestampNanos = startNanos + (i * minuteNanos);
            generateFullHistoryLog(rnd, log); // Populate all 70+ fields

            long bucketKey = timestampNanos / intervalNanos;
            var aggregate = grouped.computeIfAbsent(bucketKey, k -> new BucketAggregate());
            processLog(aggregate, log);
        }

        return grouped.values().stream()
            .map(g -> new FinalResult(
                g.countTemperature > 0 ? g.sumTemperature / g.countTemperature : 0,
                g.countActivePower > 0 ? g.sumActivePower / g.countActivePower : 0,
                g.countCompressorHz > 0 ? g.sumCompressorHz / g.countCompressorHz : 0,
                g.totalCount > 0 ? (double) g.alarmCount / g.totalCount : 0
            )).collect(Collectors.toList());
    }
    
    static void processLog(BucketAggregate aggregate, HistoryLogFullPrimitive log) {
        if (log.hasTemperature) { aggregate.sumTemperature += log.temperature; aggregate.countTemperature++; }
        if (log.hasActivePower) { aggregate.sumActivePower += log.activePower; aggregate.countActivePower++; }
        if (log.hasCompressorHz) { aggregate.sumCompressorHz += log.compressorHz; aggregate.countCompressorHz++; }
        if (log.hasAlarmActive && log.alarmActive > 0) { aggregate.alarmCount++; }
        aggregate.totalCount++;
    }

    // THIS METHOD NOW POPULATES ALL FIELDS, MATCHING THE OTHER BENCHMARKS' WORKLOAD.
    private static void generateFullHistoryLog(ThreadLocalRandom rnd, HistoryLogFullPrimitive log) {
        if (rnd.nextDouble() > 0.1) { log.premium = (short)rnd.nextInt(2); log.hasPremium = true; } else { log.hasPremium = false; }
        if (rnd.nextDouble() > 0.1) { log.temperature = (float)(rnd.nextDouble() * 40); log.hasTemperature = true; } else { log.hasTemperature = false; }
        if (rnd.nextDouble() > 0.1) { log.temperature2 = (float)(rnd.nextDouble() * 40); log.hasTemperature2 = true; } else { log.hasTemperature2 = false; }
        if (rnd.nextDouble() > 0.1) { log.temperature3 = (float)(rnd.nextDouble() * 40); log.hasTemperature3 = true; } else { log.hasTemperature3 = false; }
        if (rnd.nextDouble() > 0.1) { log.humidity = (short)rnd.nextInt(100); log.hasHumidity = true; } else { log.hasHumidity = false; }
        if (rnd.nextDouble() > 0.1) { log.powerOn = rnd.nextBoolean(); log.hasPowerOn = true; } else { log.hasPowerOn = false; }
        if (rnd.nextDouble() > 0.1) { log.fan = rnd.nextBoolean(); log.hasFan = true; } else { log.hasFan = false; }
        if (rnd.nextDouble() > 0.1) { log.pump = rnd.nextBoolean(); log.hasPump = true; } else { log.hasPump = false; }
        if (rnd.nextDouble() > 0.1) { log.speco = (float)(rnd.nextDouble() * 10); log.hasSpeco = true; } else { log.hasSpeco = false; }
        if (rnd.nextDouble() > 0.1) { log.spdhw = (float)(rnd.nextDouble() * 60); log.hasSpdhw = true; } else { log.hasSpdhw = false; }
        if (rnd.nextDouble() > 0.1) { log.defrost = (short)rnd.nextInt(5); log.hasDefrost = true; } else { log.hasDefrost = false; }
        if (rnd.nextDouble() > 0.1) { log.spantilegionella = (short)rnd.nextInt(70); log.hasSpantilegionella = true; } else { log.hasSpantilegionella = false; }
        if (rnd.nextDouble() > 0.1) { log.expansionValveOut = rnd.nextBoolean(); log.hasExpansionValveOut = true; } else { log.hasExpansionValveOut = false; }
        if (rnd.nextDouble() > 0.1) { log.modbusGlobalFails = rnd.nextInt(10); log.hasModbusGlobalFails = true; } else { log.hasModbusGlobalFails = false; }
        if (rnd.nextDouble() > 0.1) { log.refrigerantLiquid = (float)(rnd.nextDouble() * 5); log.hasRefrigerantLiquid = true; } else { log.hasRefrigerantLiquid = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmDischargeTemp = (short)rnd.nextInt(2); log.hasAlarmDischargeTemp = true; } else { log.hasAlarmDischargeTemp = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmDriverInverter = (short)rnd.nextInt(2); log.hasAlarmDriverInverter = true; } else { log.hasAlarmDriverInverter = false; }
        if (rnd.nextDouble() > 0.1) { log.compressorPercentage = (short)rnd.nextInt(100); log.hasCompressorPercentage = true; } else { log.hasCompressorPercentage = false; }
        if (rnd.nextDouble() > 0.1) { log.dischargeTemperature = (float)(rnd.nextDouble() * 120); log.hasDischargeTemperature = true; } else { log.hasDischargeTemperature = false; }
        if (rnd.nextDouble() > 0.1) { log.antilegionellaDayWeek = (short)rnd.nextInt(7); log.hasAntilegionellaDayWeek = true; } else { log.hasAntilegionellaDayWeek = false; }
        if (rnd.nextDouble() > 0.1) { log.pumpFlow = (float)(rnd.nextDouble() * 1000); log.hasPumpFlow = true; } else { log.hasPumpFlow = false; }
        if (rnd.nextDouble() > 0.1) { log.pr1 = (float)(rnd.nextDouble() * 30); log.hasPr1 = true; } else { log.hasPr1 = false; }
        if (rnd.nextDouble() > 0.1) { log.pr2 = (float)(rnd.nextDouble() * 30); log.hasPr2 = true; } else { log.hasPr2 = false; }
        if (rnd.nextDouble() > 0.1) { log.pr3 = (float)(rnd.nextDouble() * 30); log.hasPr3 = true; } else { log.hasPr3 = false; }
        if (rnd.nextDouble() > 0.1) { log.compressor = rnd.nextBoolean(); log.hasCompressor = true; } else { log.hasCompressor = false; }
        if (rnd.nextDouble() > 0.1) { log.compressorHz = (float)(rnd.nextDouble() * 90); log.hasCompressorHz = true; } else { log.hasCompressorHz = false; }
        if (rnd.nextDouble() > 0.1) { log.auxGroupWatts = (short)rnd.nextInt(6000); log.hasAuxGroupWatts = true; } else { log.hasAuxGroupWatts = false; }
        if (rnd.nextDouble() > 0.1) { log.autoMode = (short)rnd.nextInt(3); log.hasAutoMode = true; } else { log.hasAutoMode = false; }
        if (rnd.nextDouble() > 0.1) { log.heatFocus = (short)rnd.nextInt(2); log.hasHeatFocus = true; } else { log.hasHeatFocus = false; }
        if (rnd.nextDouble() > 0.1) { log.tempHeating = (float)(rnd.nextDouble() * 50); log.hasTempHeating = true; } else { log.hasTempHeating = false; }
        if (rnd.nextDouble() > 0.1) { log.returnTemp = (float)(rnd.nextDouble() * 45); log.hasReturnTemp = true; } else { log.hasReturnTemp = false; }
        if (rnd.nextDouble() > 0.1) { log.opModeHeating = (short)rnd.nextInt(4); log.hasOpModeHeating = true; } else { log.hasOpModeHeating = false; }
        if (rnd.nextDouble() > 0.1) { log.evaporatorTemp = (float)(rnd.nextDouble() * 10); log.hasEvaporatorTemp = true; } else { log.hasEvaporatorTemp = false; }
        if (rnd.nextDouble() > 0.1) { log.evaporationTemp = (float)(rnd.nextDouble() * 5); log.hasEvaporationTemp = true; } else { log.hasEvaporationTemp = false; }
        if (rnd.nextDouble() > 0.1) { log.airInlet = (float)(rnd.nextDouble() * 25); log.hasAirInlet = true; } else { log.hasAirInlet = false; }
        if (rnd.nextDouble() > 0.1) { log.airOutlet = (float)(rnd.nextDouble() * 15); log.hasAirOutlet = true; } else { log.hasAirOutlet = false; }
        if (rnd.nextDouble() > 0.1) { log.setPointAir = (float)(rnd.nextDouble() * 22); log.hasSetPointAir = true; } else { log.hasSetPointAir = false; }
        if (rnd.nextDouble() > 0.1) { log.outsideTemp = (float)(rnd.nextDouble() * 45); log.hasOutsideTemp = true; } else { log.hasOutsideTemp = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmActive = (short)(rnd.nextDouble() < 0.02 ? rnd.nextInt(10) : 0); log.hasAlarmActive = true; } else { log.hasAlarmActive = false; }
        if (rnd.nextDouble() > 0.1) { log.activePower = (float)(rnd.nextDouble() * 3500); log.hasActivePower = true; } else { log.hasActivePower = false; }
        if (rnd.nextDouble() > 0.1) { log.totalActivePower = (float)(rnd.nextDouble() * 100000); log.hasTotalActivePower = true; } else { log.hasTotalActivePower = false; }
        if (rnd.nextDouble() > 0.1) { log.t1Temp = (float)(rnd.nextDouble() * 50); log.hasT1Temp = true; } else { log.hasT1Temp = false; }
        if (rnd.nextDouble() > 0.1) { log.t2Temp = (float)(rnd.nextDouble() * 40); log.hasT2Temp = true; } else { log.hasT2Temp = false; }
        if (rnd.nextDouble() > 0.1) { log.thermalDiff = (float)(rnd.nextDouble() * 10); log.hasThermalDiff = true; } else { log.hasThermalDiff = false; }
        if (rnd.nextDouble() > 0.1) { log.thermalPower = (float)(rnd.nextDouble() * 5000); log.hasThermalPower = true; } else { log.hasThermalPower = false; }
        if (rnd.nextDouble() > 0.1) { log.totalThermalEnergy = (float)(rnd.nextDouble() * 200000); log.hasTotalThermalEnergy = true; } else { log.hasTotalThermalEnergy = false; }
        if (rnd.nextDouble() > 0.1) { log.litersPerHourFlow = (float)(rnd.nextDouble() * 1500); log.hasLitersPerHourFlow = true; } else { log.hasLitersPerHourFlow = false; }
        if (rnd.nextDouble() > 0.1) { log.copInstant = (float)(rnd.nextDouble() * 6); log.hasCopInstant = true; } else { log.hasCopInstant = false; }
        if (rnd.nextDouble() > 0.1) { log.pumpPercent = (short)rnd.nextInt(100); log.hasPumpPercent = true; } else { log.hasPumpPercent = false; }
        if (rnd.nextDouble() > 0.1) { log.resistors = rnd.nextBoolean(); log.hasResistors = true; } else { log.hasResistors = false; }
        if (rnd.nextDouble() > 0.1) { log.spHeating = (float)(rnd.nextDouble() * 55); log.hasSpHeating = true; } else { log.hasSpHeating = false; }
        if (rnd.nextDouble() > 0.1) { log.spMode = (short)rnd.nextInt(3); log.hasSpMode = true; } else { log.hasSpMode = false; }
        if (rnd.nextDouble() > 0.1) { log.dhwSpMode = (short)rnd.nextInt(3); log.hasDhwSpMode = true; } else { log.hasDhwSpMode = false; }
        if (rnd.nextDouble() > 0.1) { log.autoMinSpDhw = (float)(rnd.nextDouble() * 45); log.hasAutoMinSpDhw = true; } else { log.hasAutoMinSpDhw = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmPumpFlow = (short)rnd.nextInt(2); log.hasAlarmPumpFlow = true; } else { log.hasAlarmPumpFlow = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmAdcBoard = (short)rnd.nextInt(2); log.hasAlarmAdcBoard = true; } else { log.hasAlarmAdcBoard = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmLp = (short)rnd.nextInt(2); log.hasAlarmLp = true; } else { log.hasAlarmLp = false; }
        if (rnd.nextDouble() > 0.1) { log.alarmHp = (short)rnd.nextInt(2); log.hasAlarmHp = true; } else { log.hasAlarmHp = false; }
        if (rnd.nextDouble() > 0.1) { log.sp1 = (float)(rnd.nextDouble() * 50); log.hasSp1 = true; } else { log.hasSp1 = false; }
        if (rnd.nextDouble() > 0.1) { log.powerOnHeating = (short)rnd.nextInt(2); log.hasPowerOnHeating = true; } else { log.hasPowerOnHeating = false; }
        if (rnd.nextDouble() > 0.1) { log.antilegionella = rnd.nextBoolean(); log.hasAntilegionella = true; } else { log.hasAntilegionella = false; }
        if (rnd.nextDouble() > 0.1) { log.z1Sp = (float)(rnd.nextDouble() * 25); log.hasZ1Sp = true; } else { log.hasZ1Sp = false; }
        if (rnd.nextDouble() > 0.1) { log.z2Sp = (float)(rnd.nextDouble() * 25); log.hasZ2Sp = true; } else { log.hasZ2Sp = false; }
        if (rnd.nextDouble() > 0.1) { log.powerOnDhw = (short)rnd.nextInt(2); log.hasPowerOnDhw = true; } else { log.hasPowerOnDhw = false; }
        if (rnd.nextDouble() > 0.1) { log.coolingHeatingMode = (short)rnd.nextInt(3); log.hasCoolingHeatingMode = true; } else { log.hasCoolingHeatingMode = false; }
        if (rnd.nextDouble() > 0.1) { log.valveZ1 = rnd.nextBoolean(); log.hasValveZ1 = true; } else { log.hasValveZ1 = false; }
        if (rnd.nextDouble() > 0.1) { log.valveZ2 = rnd.nextBoolean(); log.hasValveZ2 = true; } else { log.hasValveZ2 = false; }
        if (rnd.nextDouble() > 0.1) { log.totalizerWh = (float)(rnd.nextDouble() * 300000); log.hasTotalizerWh = true; } else { log.hasTotalizerWh = false; }
        if (rnd.nextDouble() > 0.1) { log.gasSuction = (short)rnd.nextInt(10); log.hasGasSuction = true; } else { log.hasGasSuction = false; }
        if (rnd.nextDouble() > 0.1) { log.runningFanPercent = (short)rnd.nextInt(100); log.hasRunningFanPercent = true; } else { log.hasRunningFanPercent = false; }
    }
}