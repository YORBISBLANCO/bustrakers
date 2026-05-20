# 🔍 Solución: Paradas y Buses no Aparecen

## Problema Identificado

Las paradas (y posiblemente los buses) no se muestran en la página de detalle de ruta o en la reserva. Esto ocurre cuando:

1. **No hay datos en MongoDB** para esa ruta
2. **Los datos están embebidos** en la ruta pero el controlador no los reconoce
3. **Los datos están en colecciones separadas** pero el `rutaId` no coincide

## Solución Implementada

### 1. Mejorar el controlador
- El controlador ahora prioriza datos embebidos
- Si no encuentra datos embebidos, busca en colecciones separadas
- Se agregó un endpoint de debugging para ver qué datos existen

### 2. Crear Datos de Prueba

**Opción A: Usar el endpoint automático**

1. Compila y ejecuta la aplicación
2. Visita: `http://localhost:8080/api/debug/crear-datos-prueba`
3. Verás una respuesta JSON con:
   - `rutaId`: ID de la ruta creada
   - `debugUrl`: URL para ver los datos (ej: `/debug/ruta/{rutaId}`)
   - `detalleUrl`: URL para ver el detalle (ej: `/ruta/{rutaId}`)

**Opción B: Crear datos manualmente**

Si prefieres crear las rutas a través de la aplicación:
1. Accede a `/agregar_rutas` (panel de administración)
2. Crea una nueva ruta
3. Crea buses y paradas

**Opción C: Script de MongoDB**

Si tienes acceso directo a MongoDB:
```javascript
use aula  // o tu base de datos

// 1. Crear una ruta
db.rutas.insertOne({
  nombre: "Ruta Centro - Morros",
  horaAproximada: { _t: "java.time.LocalTime", value: "08:30" },
  barrios: ["Centro", "Morros"],
  buses: [],
  paradas: [],
  activa: true,
  fechaCreacion: new Date().getTime()
})

// 2. Obtener el ID de la ruta creada
const rutaId = db.rutas.findOne({nombre: "Ruta Centro - Morros"})._id.toString()

// 3. Crear paradas
db.paradas.insertMany([
  {
    nombre: "Terminal Centro",
    ubicacion: "Calle 10 #5-20",
    referencia: "Frente a Carrefour",
    orden: 1,
    rutaId: rutaId,
    rutaNombre: "Ruta Centro - Morros",
    activa: true,
    fechaCreacion: new Date().getTime()
  },
  {
    nombre: "Parada Morros",
    ubicacion: "Cra 30 #80-50",
    referencia: "Esquina con Cra 31",
    orden: 2,
    rutaId: rutaId,
    rutaNombre: "Ruta Centro - Morros",
    activa: true,
    fechaCreacion: new Date().getTime()
  }
])

// 4. Crear buses
db.buses.insertMany([
  {
    placa: "ABC-123",
    marca: "Volvo",
    modelo: "B8R",
    color: "Azul",
    conductor: "Juan Pérez",
    rutaId: rutaId,
    rutaNombre: "Ruta Centro - Morros",
    estado: "activo",
    activo: true,
    fechaRegistro: new Date().getTime()
  }
])
```

### 3. Verificar los Datos

**Debugging con interfaz visual:**
```
http://localhost:8080/debug/ruta/{rutaId}
```

Reemplaza `{rutaId}` con el ID real. Verás:
- ✅ Datos embebidos en la ruta
- ✅ Datos en colecciones separadas
- ✅ Diagnóstico automático

### 4. Ver el Resultado

Una vez que los datos existan:
- **Detalle de Ruta:** `http://localhost:8080/ruta/{rutaId}`
- **Reservar:** `http://localhost:8080/reservar/ruta/{rutaId}`

## Estructura Esperada

### Opción 1: Datos Embebidos (Recomendado)
```
Ruta {
  id: "xxx",
  nombre: "Ruta Centro",
  buses: [
    { id: "b1", placa: "ABC-123", rutaId: "xxx" },
    { id: "b2", placa: "XYZ-789", rutaId: "xxx" }
  ],
  paradas: [
    { id: "p1", nombre: "Terminal", orden: 1, rutaId: "xxx" },
    { id: "p2", nombre: "Parada 2", orden: 2, rutaId: "xxx" }
  ]
}
```

### Opción 2: Datos Separados
```
Colección "rutas":
  Ruta { id: "xxx", nombre: "Ruta Centro" }

Colección "buses":
  Bus { id: "b1", placa: "ABC-123", rutaId: "xxx" }

Colección "paradas":
  Parada { id: "p1", nombre: "Terminal", rutaId: "xxx" }
```

## Solución de Problemas

| Problema | Causa | Solución |
|----------|-------|----------|
| Sigue mostrando 0 paradas | No hay datos | Crea datos con `/api/debug/crear-datos-prueba` |
| Error 500 en reserva | Falta el controlador | Ya está solucionado en `/reservar/ruta/{id}` |
| Las búsquedas no funcionan | Filtro incorrecto | Usa `/rutas?buscar=nombre` |

## Limpiar Datos de Prueba

```
http://localhost:8080/api/debug/limpiar-datos
```

## Logs de Debugging

Cuando accedas a `/debug/ruta/{id}`, la aplicación imprime en la consola:
```
=== DEBUG RUTA: Ruta Centro - Morros ===
ID: xxxx
Buses Embebidos: 2
Paradas Embebidas: 3
Buses Separados (por rutaId): 2
Paradas Separadas (por rutaId): 3
```

Esto te ayuda a entender dónde están los datos y por qué sí o no aparecen.
