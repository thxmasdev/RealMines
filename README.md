# RealMines en Folia 1.21.x (resumen humano)

Llevé el plugin a Folia 1.21.x para que todo lo que toca bloques o jugadores se ejecute en el hilo correcto. Dejamos atrás el viejo `BukkitScheduler` y usamos los planificadores de Folia/Paper:

- `AsyncScheduler` para tareas periódicas (resets, títulos, etc.).
- `RegionScheduler` para cualquier modificación del mundo (rellenos, limpieza, carteles).
- `Player.getScheduler()` para callbacks de GUI y acciones ligadas al jugador.

También actualicé el proyecto a Java 21, `api-version: 1.21`, `folia-supported: true` y dependencias modernas (Paper API y Adventure). Si usas WorldEdit/FAWE, sigue siendo parte del flujo: lo usamos para leer selecciones y, si lo configuras, para colocar bloques.

Para probar: crea una mina con WorldEdit, abre los paneles y lanza un reset. No deberías ver advertencias de concurrencia y el resaltado/reset deberían ir finos.

author: ThxmasDev - discord: zlthomas
