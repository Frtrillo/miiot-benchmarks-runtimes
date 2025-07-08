// benchmark.js (Fair Fight Version)
const { performance } = require('perf_hooks');

// *** MODIFICATION #1: This function now MUTATES the `log` object. ***
// It no longer returns anything.
function populateFullHistoryLog(log, random) {
    const rBool = () => random() < 0.5;
    const rInt = (max) => Math.floor(random() * max);
    const rReal = (max) => random() * max;

    // Instead of "return { ... }", we set properties on the object that was passed in.
    log.vinculation_code = 'VINC-CODE-BENCHMARK';
    log.premium = random() > 0.1 ? rInt(2) : null;
    log.temperature = random() > 0.1 ? rReal(40) : null;
    log.temperature2 = random() > 0.1 ? rReal(40) : null;
    log.temperature3 = random() > 0.1 ? rReal(40) : null;
    log.humidity = random() > 0.1 ? rInt(100) : null;
    log.poweron = random() > 0.1 ? rBool() : null;
    log.fan = random() > 0.1 ? rBool() : null;
    log.pump = random() > 0.1 ? rBool() : null;
    log.speco = random() > 0.1 ? rReal(10) : null;
    log.spdhw = random() > 0.1 ? rReal(60) : null;
    log.defrost = random() > 0.1 ? rInt(5) : null;
    log.spantilegionella = random() > 0.1 ? rInt(70) : null;
    log.expansionvalveout = random() > 0.1 ? rBool() : null;
    log.modbusglobalfails = random() > 0.1 ? rInt(10) : null;
    log.refrigerant_liquid = random() > 0.1 ? rReal(5) : null;
    log.alarm_dischargetemp = random() > 0.1 ? rInt(2) : null;
    log.alarm_driverinverter = random() > 0.1 ? rInt(2) : null;
    log.compressorpercentage = random() > 0.1 ? rInt(100) : null;
    log.discharge_temperature = random() > 0.1 ? rReal(120) : null;
    log.antilegionella_dayweek = random() > 0.1 ? rInt(7) : null;
    log.pumpflow = random() > 0.1 ? rReal(1000) : null;
    log.pr1 = random() > 0.1 ? rReal(30) : null;
    log.pr2 = random() > 0.1 ? rReal(30) : null;
    log.pr3 = random() > 0.1 ? rReal(30) : null;
    log.compressor = random() > 0.1 ? rBool() : null;
    log.compressorhz = random() > 0.1 ? rReal(90) : null;
    log.auxgroupwatts = random() > 0.1 ? rInt(6000) : null;
    log.automode = random() > 0.1 ? rInt(3) : null;
    log.heatfocus = random() > 0.1 ? rInt(2) : null;
    log.tempheating = random() > 0.1 ? rReal(50) : null;
    log.returntemp = random() > 0.1 ? rReal(45) : null;
    log.opmodeheating = random() > 0.1 ? rInt(4) : null;
    log.evaporatortemp = random() > 0.1 ? rReal(10) : null;
    log.evaporationtemp = random() > 0.1 ? rReal(5) : null;
    log.air_inlet = random() > 0.1 ? rReal(25) : null;
    log.air_outlet = random() > 0.1 ? rReal(15) : null;
    log.setpointair = random() > 0.1 ? rReal(22) : null;
    log.outside_temp = random() > 0.1 ? rReal(45) : null;
    log.alarm_active = random() > 0.1 ? (random() < 0.02 ? rInt(10) : 0) : null;
    log.activepower = random() > 0.1 ? rReal(3500) : null;
    log.totalactivepower = random() > 0.1 ? rReal(100000) : null;
    log.t1temp = random() > 0.1 ? rReal(50) : null;
    log.t2temp = random() > 0.1 ? rReal(40) : null;
    log.thermaldiff = random() > 0.1 ? rReal(10) : null;
    log.thermalpower = random() > 0.1 ? rReal(5000) : null;
    log.totalthermalenergy = random() > 0.1 ? rReal(200000) : null;
    log.litersperhourflow = random() > 0.1 ? rReal(1500) : null;
    log.copinstant = random() > 0.1 ? rReal(6) : null;
    log.pumppercent = random() > 0.1 ? rInt(100) : null;
    log.resistors = random() > 0.1 ? rBool() : null;
    log.spheating = random() > 0.1 ? rReal(55) : null;
    log.spmode = random() > 0.1 ? rInt(3) : null;
    log.dhwspmode = random() > 0.1 ? rInt(3) : null;
    log.autominspdhw = random() > 0.1 ? rReal(45) : null;
    log.alarm_pumpflow = random() > 0.1 ? rInt(2) : null;
    log.alarm_adcboard = random() > 0.1 ? rInt(2) : null;
    log.alarm_lp = random() > 0.1 ? rInt(2) : null;
    log.alarm_hp = random() > 0.1 ? rInt(2) : null;
    log.sp1 = random() > 0.1 ? rReal(50) : null;
    log.poweronheating = random() > 0.1 ? rInt(2) : null;
    log.antilegionella = random() > 0.1 ? rBool() : null;
    log.z1_sp = random() > 0.1 ? rReal(25) : null;
    log.z2_sp = random() > 0.1 ? rReal(25) : null;
    log.powerondhw = random() > 0.1 ? rInt(2) : null;
    log.cooling_heating_mode = random() > 0.1 ? rInt(3) : null;
    log.valve_z1 = random() > 0.1 ? rBool() : null;
    log.valve_z2 = random() > 0.1 ? rBool() : null;
    log.totalizer_wh = random() > 0.1 ? rReal(300000) : null;
    log.gas_suction = random() > 0.1 ? rInt(10) : null;
    log.runningfanpercent = random() > 0.1 ? rInt(100) : null;
}

function processComplexLogsOnTheFly(n, intervalMilliseconds) {
    const grouped = new Map();
    const start = new Date();
    start.setDate(start.getDate() - 7);
    const startTime = start.getTime();
    const rnd = Math.random;

    // Create the log object ONCE, before the loop.
    const log = {};

    for (let i = 0; i < n; i++) {
        const timestamp_ms = startTime + (i * 60 * 1000);

        // *** MODIFICATION #2: Populate the existing object. ***
        populateFullHistoryLog(log, rnd);

        const bucket = Math.floor(timestamp_ms / intervalMilliseconds) * intervalMilliseconds;

        let current = grouped.get(bucket);
        if (!current) {
            current = {
                sum_temperature: 0, count_temperature: 0,
                sum_activepower: 0, count_activepower: 0,
                sum_compressorhz: 0, count_compressorhz: 0,
                alarm_count: 0,
                total_count: 0
            };
            grouped.set(bucket, current);
        }
        
        // This now correctly processes the mutated 'log' object.
        processLog(current, log);
    }

    function processLog(aggregate, log) {
        if (log.temperature != null) { aggregate.sum_temperature += log.temperature; aggregate.count_temperature++; }
        if (log.activepower != null) { aggregate.sum_activepower += log.activepower; aggregate.count_activepower++; }
        if (log.compressorhz != null) { aggregate.sum_compressorhz += log.compressorhz; aggregate.count_compressorhz++; }
        if (log.alarm_active != null && log.alarm_active > 0) { aggregate.alarm_count++; }
        aggregate.total_count++;
    }

    return Array.from(grouped.values()).map(g => ({
        avg_temperature: g.count_temperature > 0 ? g.sum_temperature / g.count_temperature : 0,
        avg_activepower: g.count_activepower > 0 ? g.sum_activepower / g.count_activepower : 0,
        avg_compressorhz: g.count_compressorhz > 0 ? g.sum_compressorhz / g.count_compressorhz : 0,
        alarm_ratio: g.total_count > 0 ? g.alarm_count / g.total_count : 0,
    }));
}

function main() {
    const n = 50000000;
    const intervalMinutes = 10;
    const intervalMilliseconds = intervalMinutes * 60 * 1000;

    console.log(`Procesando ${n.toLocaleString()} registros con ESQUEMA COMPLETO (Fair Fight)...`);
    
    // Warm-up
    console.log("Warming up the V8 JIT...");
    processComplexLogsOnTheFly(10_000_000, intervalMilliseconds);
    console.log("Warm-up complete. Starting benchmark.");
    
    // Explicitly ask for GC to get a cleaner measurement
    if (global.gc) {
        global.gc();
    }
    
    const startTime = performance.now();
    const results = processComplexLogsOnTheFly(n, intervalMilliseconds);
    const endTime = performance.now();
    
    const durationSeconds = (endTime - startTime) / 1000;

    console.log(`Node.js (Fair Fight) - Tiempo: ${durationSeconds.toFixed(3)} s`);
    console.log(`Buckets calculados: ${results.length}`);
}

main();