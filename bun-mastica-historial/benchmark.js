// benchmark.js
const { performance } = require('perf_hooks');

/**
 * Genera un objeto que simula un registro completo de la tabla 'history_logs'.
 * Esta versión es más rápida al usar el operador ternario directamente.
 * @param {() => number} random - La función de generación de números aleatorios (Math.random).
 * @returns {object} Un objeto de log completo.
 */
function generateFullHistoryLog(random) {
    const rBool = () => random() < 0.5;
    const rInt = (max) => Math.floor(random() * max);
    const rReal = (max) => random() * max;

    return {
        vinculation_code: 'VINC-CODE-BENCHMARK',
        premium: random() > 0.1 ? rInt(2) : null,
        temperature: random() > 0.1 ? rReal(40) : null,
        temperature2: random() > 0.1 ? rReal(40) : null,
        temperature3: random() > 0.1 ? rReal(40) : null,
        humidity: random() > 0.1 ? rInt(100) : null,
        poweron: random() > 0.1 ? rBool() : null,
        fan: random() > 0.1 ? rBool() : null,
        pump: random() > 0.1 ? rBool() : null,
        speco: random() > 0.1 ? rReal(10) : null,
        spdhw: random() > 0.1 ? rReal(60) : null,
        defrost: random() > 0.1 ? rInt(5) : null,
        spantilegionella: random() > 0.1 ? rInt(70) : null,
        expansionvalveout: random() > 0.1 ? rBool() : null,
        modbusglobalfails: random() > 0.1 ? rInt(10) : null,
        refrigerant_liquid: random() > 0.1 ? rReal(5) : null,
        alarm_dischargetemp: random() > 0.1 ? rInt(2) : null,
        alarm_driverinverter: random() > 0.1 ? rInt(2) : null,
        compressorpercentage: random() > 0.1 ? rInt(100) : null,
        discharge_temperature: random() > 0.1 ? rReal(120) : null,
        antilegionella_dayweek: random() > 0.1 ? rInt(7) : null,
        pumpflow: random() > 0.1 ? rReal(1000) : null,
        pr1: random() > 0.1 ? rReal(30) : null,
        pr2: random() > 0.1 ? rReal(30) : null,
        pr3: random() > 0.1 ? rReal(30) : null,
        compressor: random() > 0.1 ? rBool() : null,
        compressorhz: random() > 0.1 ? rReal(90) : null,
        auxgroupwatts: random() > 0.1 ? rInt(6000) : null,
        automode: random() > 0.1 ? rInt(3) : null,
        heatfocus: random() > 0.1 ? rInt(2) : null,
        tempheating: random() > 0.1 ? rReal(50) : null,
        returntemp: random() > 0.1 ? rReal(45) : null,
        opmodeheating: random() > 0.1 ? rInt(4) : null,
        evaporatortemp: random() > 0.1 ? rReal(10) : null,
        evaporationtemp: random() > 0.1 ? rReal(5) : null,
        air_inlet: random() > 0.1 ? rReal(25) : null,
        air_outlet: random() > 0.1 ? rReal(15) : null,
        setpointair: random() > 0.1 ? rReal(22) : null,
        outside_temp: random() > 0.1 ? rReal(45) : null,
        alarm_active: random() > 0.1 ? (random() < 0.02 ? rInt(10) : 0) : null,
        activepower: random() > 0.1 ? rReal(3500) : null,
        totalactivepower: random() > 0.1 ? rReal(100000) : null,
        t1temp: random() > 0.1 ? rReal(50) : null,
        t2temp: random() > 0.1 ? rReal(40) : null,
        thermaldiff: random() > 0.1 ? rReal(10) : null,
        thermalpower: random() > 0.1 ? rReal(5000) : null,
        totalthermalenergy: random() > 0.1 ? rReal(200000) : null,
        litersperhourflow: random() > 0.1 ? rReal(1500) : null,
        copinstant: random() > 0.1 ? rReal(6) : null,
        pumppercent: random() > 0.1 ? rInt(100) : null,
        resistors: random() > 0.1 ? rBool() : null,
        spheating: random() > 0.1 ? rReal(55) : null,
        spmode: random() > 0.1 ? rInt(3) : null,
        dhwspmode: random() > 0.1 ? rInt(3) : null,
        autominspdhw: random() > 0.1 ? rReal(45) : null,
        alarm_pumpflow: random() > 0.1 ? rInt(2) : null,
        alarm_adcboard: random() > 0.1 ? rInt(2) : null,
        alarm_lp: random() > 0.1 ? rInt(2) : null,
        alarm_hp: random() > 0.1 ? rInt(2) : null,
        sp1: random() > 0.1 ? rReal(50) : null,
        poweronheating: random() > 0.1 ? rInt(2) : null,
        antilegionella: random() > 0.1 ? rBool() : null,
        z1_sp: random() > 0.1 ? rReal(25) : null,
        z2_sp: random() > 0.1 ? rReal(25) : null,
        powerondhw: random() > 0.1 ? rInt(2) : null,
        cooling_heating_mode: random() > 0.1 ? rInt(3) : null,
        valve_z1: random() > 0.1 ? rBool() : null,
        valve_z2: random() > 0.1 ? rBool() : null,
        totalizer_wh: random() > 0.1 ? rReal(300000) : null,
        gas_suction: random() > 0.1 ? rInt(10) : null,
        runningfanpercent: random() > 0.1 ? rInt(100) : null,
    };
}

function processComplexLogsOnTheFly(n, intervalMilliseconds) {
    const grouped = new Map();
    const start = new Date();
    start.setDate(start.getDate() - 7);
    const startTime = start.getTime();

    const rnd = Math.random;

    for (let i = 0; i < n; i++) {
        const timestamp_ms = startTime + (i * 60 * 1000);
        const log = generateFullHistoryLog(rnd);
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
        
        processLog(current, log);
    }

    // Función auxiliar para agregar.
    function processLog(aggregate, log) {
        if (log.temperature != null) {
            aggregate.sum_temperature += log.temperature;
            aggregate.count_temperature++;
        }
        if (log.activepower != null) {
            aggregate.sum_activepower += log.activepower;
            aggregate.count_activepower++;
        }
        if (log.compressorhz != null) {
            aggregate.sum_compressorhz += log.compressorhz;
            aggregate.count_compressorhz++;
        }
        if (log.alarm_active != null && log.alarm_active > 0) {
            aggregate.alarm_count++;
        }
        aggregate.total_count++;
    }

    // Cálculo final.
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

    console.log(`Procesando ${n.toLocaleString()} registros con ESQUEMA COMPLETO...`);
    
    const startTime = performance.now();
    const results = processComplexLogsOnTheFly(n, intervalMilliseconds);
    const endTime = performance.now();
    
    const durationSeconds = (endTime - startTime) / 1000;

    console.log(`Node.js - Tiempo: ${durationSeconds.toFixed(3)} s`);
    console.log(`Buckets calculados: ${results.length}`);
}

main();