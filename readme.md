C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 570.95 ms
Buckets calculados: 500001

PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET - Tiempo: 389 ms
Buckets calculados: 500001



PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 11.53 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 13.76 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET - Tiempo: 7,123 s
Buckets calculados: 5000000
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Bun JS - Tiempo: 11.40 s
Buckets calculados: 5000001


OPTIMIZADO POR QUE NO CABE EN LA MEMORIA Y LO CORTABA EL GARBACE COLLECTOR DE .NET

PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> bun run .\bun-mastica-historial\benchmark.js
Iniciando benchmark optimizado de Node.js/Bun...
Bun/Node JS (Optimizado) - Tiempo: 8.083 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> dotnet run --project .\dotnet-mastica-historial\dotnet-mastica-historial.csproj
C# .NET (Optimizado) - Tiempo: 2,168 s
Buckets calculados: 5000001
PS C:\Users\frtri\Documents\benchmarksMiiot\miiot-benchmarks> node .\bun-mastica-historial\benchmark.js
Iniciando benchmark optimizado de Node.js/Bun...
Bun/Node JS (Optimizado) - Tiempo: 19.884 s
Buckets calculados: 5000001

![Resultados del benchmark](benchmark-result.png)


(1 core bun vs 8 core .net)