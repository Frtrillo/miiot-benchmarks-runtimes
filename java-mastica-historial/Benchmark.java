import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Benchmark {

    // --- Data Classes (equivalent to C# classes) ---

    /**
     * Represents the complete data structure of a log record,
     * matching the entire schema of the database table.
     */
    static class HistoryLogFull {
        private String vinculationCode = "VINC-CODE-BENCHMARK";
        private LocalDateTime createdAt;
        private Short premium;
        private Float temperature;
        private Float temperature2;
        private Float temperature3;
        private Short humidity;
        private Boolean powerOn;
        private Boolean fan;
        private Boolean pump;
        private Float speco;
        private Float spdhw;
        private Short defrost;
        private Short spantilegionella;
        private Boolean expansionValveOut;
        private Integer modbusGlobalFails;
        private Float refrigerantLiquid;
        private Short alarmDischargeTemp;
        private Short alarmDriverInverter;
        private Short compressorPercentage;
        private Float dischargeTemperature;
        private Short antilegionellaDayWeek;
        private Float pumpFlow;
        private Float pr1;
        private Float pr2;
        private Float pr3;
        private Boolean compressor;
        private Float compressorHz;
        private Short auxGroupWatts;
        private Short autoMode;
        private Short heatFocus;
        private Float tempHeating;
        private Float returnTemp;
        private Short opModeHeating;
        private Float evaporatorTemp;
        private Float evaporationTemp;
        private Float airInlet;
        private Float airOutlet;
        private Float setPointAir;
        private Float outsideTemp;
        private Short alarmActive;
        private Float activePower;
        private Float totalActivePower;
        private Float t1Temp;
        private Float t2Temp;
        private Float thermalDiff;
        private Float thermalPower;
        private Float totalThermalEnergy;
        private Float litersPerHourFlow;
        private Float copInstant;
        private Short pumpPercent;
        private Boolean resistors;
        private Float spHeating;
        private Short spMode;
        private Short dhwSpMode;
        private Float autoMinSpDhw;
        private Short alarmPumpFlow;
        private Short alarmAdcBoard;
        private Short alarmLp;
        private Short alarmHp;
        private Float sp1;
        private Short powerOnHeating;
        private Boolean antilegionella;
        private Float z1Sp;
        private Float z2Sp;
        private Short powerOnDhw;
        private Short coolingHeatingMode;
        private Boolean valveZ1;
        private Boolean valveZ2;
        private Float totalizerWh;
        private Short gasSuction;
        private Short runningFanPercent;
        // Getters and Setters are omitted for brevity but would be present in a real application
    }

    /**
     * Mutable class for efficiently aggregating values.
     */
    static class BucketAggregate {
        public double sumTemperature = 0;
        public int countTemperature = 0;
        public double sumActivePower = 0;
        public int countActivePower = 0;
        public double sumCompressorHz = 0;
        public int countCompressorHz = 0;
        public int alarmCount = 0;
        public int totalCount = 0;
    }

    /**
     * A record to hold the final computed results for a bucket.
     * This is a clean, immutable way to store final data.
     */
    record FinalResult(double avgTemperature, double avgActivePower, double avgCompressorHz, double alarmRatio) {}


    // --- Main Benchmark Logic ---

    public static void main(String[] args) {
        int n = 50_000_000;
        Duration interval = Duration.ofMinutes(10);

        System.out.printf("Processing %,d records with FULL SCHEMA...%n", n);

        long startTime = System.nanoTime();
        List<FinalResult> results = processComplexLogsOnTheFly(n, interval);
        long endTime = System.nanoTime();

        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;

        System.out.printf("Java (Optimized) - Time: %.3f s%n", elapsedSeconds);
        System.out.printf("Buckets calculated: %d%n", results.size());
    }

    static List<FinalResult> processComplexLogsOnTheFly(int n, Duration interval) {
        Map<Long, BucketAggregate> grouped = new HashMap<>();
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        Random rnd = new Random();
        long intervalNanos = interval.toNanos();

        for (int i = 0; i < n; i++) {
            LocalDateTime timestamp = start.plusMinutes(i);
            HistoryLogFull log = generateFullHistoryLog(rnd, timestamp);

            // Replicate C# ticks-based bucketing using nanoseconds for precision
            long timestampNanos = timestamp.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000L + timestamp.getNano();
            long bucketKey = (timestampNanos / intervalNanos); // Using the group as the key is sufficient and simpler

            // Efficiently get or create the aggregate for the bucket
            BucketAggregate aggregate = grouped.computeIfAbsent(bucketKey, k -> new BucketAggregate());

            processLog(aggregate, log);
        }

        // The final calculation using Java Streams (equivalent to LINQ)
        return grouped.values().stream()
                .map(g -> new FinalResult(
                        g.countTemperature > 0 ? g.sumTemperature / g.countTemperature : 0,
                        g.countActivePower > 0 ? g.sumActivePower / g.countActivePower : 0,
                        g.countCompressorHz > 0 ? g.sumCompressorHz / g.countCompressorHz : 0,
                        g.totalCount > 0 ? (double) g.alarmCount / g.totalCount : 0
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Aggregates a single log into the corresponding bucket.
     */
    static void processLog(BucketAggregate aggregate, HistoryLogFull log) {
        if (log.temperature != null) {
            aggregate.sumTemperature += log.temperature;
            aggregate.countTemperature++;
        }
        if (log.activePower != null) {
            aggregate.sumActivePower += log.activePower;
            aggregate.countActivePower++;
        }
        if (log.compressorHz != null) {
            aggregate.sumCompressorHz += log.compressorHz;
            aggregate.countCompressorHz++;
        }
        if (log.alarmActive != null && log.alarmActive > 0) {
            aggregate.alarmCount++;
        }
        aggregate.totalCount++;
    }

    /**
     * Generator for the complete, heavy object. Uses a direct approach for maximum performance.
     */
    private static HistoryLogFull generateFullHistoryLog(Random rnd, LocalDateTime timestamp) {
        HistoryLogFull log = new HistoryLogFull();
        log.createdAt = timestamp;
        log.premium = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.temperature = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 40) : null;
        log.temperature2 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 40) : null;
        log.temperature3 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 40) : null;
        log.humidity = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(100) : null;
        log.powerOn = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.fan = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.pump = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.speco = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 10) : null;
        log.spdhw = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 60) : null;
        log.defrost = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(5) : null;
        log.spantilegionella = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(70) : null;
        log.expansionValveOut = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.modbusGlobalFails = rnd.nextDouble() > 0.1 ? rnd.nextInt(10) : null;
        log.refrigerantLiquid = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 5) : null;
        log.alarmDischargeTemp = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.alarmDriverInverter = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.compressorPercentage = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(100) : null;
        log.dischargeTemperature = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 120) : null;
        log.antilegionellaDayWeek = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(7) : null;
        log.pumpFlow = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 1000) : null;
        log.pr1 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 30) : null;
        log.pr2 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 30) : null;
        log.pr3 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 30) : null;
        log.compressor = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.compressorHz = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 90) : null;
        log.auxGroupWatts = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(6000) : null;
        log.autoMode = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(3) : null;
        log.heatFocus = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.tempHeating = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 50) : null;
        log.returnTemp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 45) : null;
        log.opModeHeating = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(4) : null;
        log.evaporatorTemp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 10) : null;
        log.evaporationTemp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 5) : null;
        log.airInlet = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 25) : null;
        log.airOutlet = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 15) : null;
        log.setPointAir = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 22) : null;
        log.outsideTemp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 45) : null;
        log.alarmActive = rnd.nextDouble() > 0.1 ? (short)(rnd.nextDouble() < 0.02 ? rnd.nextInt(10) : 0) : null;
        log.activePower = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 3500) : null;
        log.totalActivePower = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 100000) : null;
        log.t1Temp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 50) : null;
        log.t2Temp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 40) : null;
        log.thermalDiff = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 10) : null;
        log.thermalPower = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 5000) : null;
        log.totalThermalEnergy = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 200000) : null;
        log.litersPerHourFlow = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 1500) : null;
        log.copInstant = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 6) : null;
        log.pumpPercent = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(100) : null;
        log.resistors = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.spHeating = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 55) : null;
        log.spMode = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(3) : null;
        log.dhwSpMode = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(3) : null;
        log.autoMinSpDhw = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 45) : null;
        log.alarmPumpFlow = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.alarmAdcBoard = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.alarmLp = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.alarmHp = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.sp1 = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 50) : null;
        log.powerOnHeating = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.antilegionella = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.z1Sp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 25) : null;
        log.z2Sp = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 25) : null;
        log.powerOnDhw = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(2) : null;
        log.coolingHeatingMode = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(3) : null;
        log.valveZ1 = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.valveZ2 = rnd.nextDouble() > 0.1 ? rnd.nextBoolean() : null;
        log.totalizerWh = rnd.nextDouble() > 0.1 ? (float)(rnd.nextDouble() * 300000) : null;
        log.gasSuction = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(10) : null;
        log.runningFanPercent = rnd.nextDouble() > 0.1 ? (short)rnd.nextInt(100) : null;
        return log;
    }
}