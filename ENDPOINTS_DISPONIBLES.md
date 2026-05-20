# 🚀 ENDPOINTS DISPONIBLES - Sistema Completo de MySQL + MongoDB

## 📋 TABLA DE CONTENIDOS
1. [Sincronización](#sincronización)
2. [Auditoría](#auditoría)
3. [Búsqueda Avanzada](#búsqueda-avanzada)
4. [Reportes y Analytics](#reportes-y-analytics)

---

## 🔄 SINCRONIZACIÓN
*Sincroniza datos de MySQL a MongoDB automáticamente*

### POST Endpoints

#### Sincronizar Buses MySQL → MongoDB
```
POST /api/admin/sincronizar/buses
```
**Respuesta:**
```json
{
  "exitoso": true,
  "sincronizados": 900,
  "errores": 0,
  "total": 900,
  "timestamp": "2026-04-18T14:00:00"
}
```

#### Sincronizar Usuarios MySQL → MongoDB
```
POST /api/admin/sincronizar/usuarios
```

#### Sincronizar Rutas MySQL → MongoDB
```
POST /api/admin/sincronizar/rutas
```

#### Sincronizar TODO (Buses + Usuarios + Rutas)
```
POST /api/admin/sincronizar/todo
```
**Respuesta:**
```json
{
  "buses": { "exitoso": true, "sincronizados": 900 },
  "usuarios": { "exitoso": true, "sincronizados": 3000 },
  "rutas": { "exitoso": true, "sincronizados": 300 },
  "timestamp": "2026-04-18T14:00:00"
}
```

---

## 📊 AUDITORÍA
*Registra y consulta todos los cambios del sistema*

### GET Endpoints

#### Obtener Historial Completo (últimos 50 eventos)
```
GET /api/admin/auditoria/historial?limite=50
```
**Respuesta:**
```json
[
  {
    "id": "507f...",
    "tipo": "ACTUALIZACION",
    "descripcion": "Se actualizó Bus ABC-123",
    "usuario": "admin",
    "idEntidad": "123",
    "fecha": "2026-04-18T14:30:00",
    "ip": "192.168.1.100"
  }
]
```

#### Historial de un Usuario Específico
```
GET /api/admin/auditoria/usuario/{usuario}

Ejemplo:
GET /api/admin/auditoria/usuario/juan_admin
```

#### Historial de una Entidad Específica
```
GET /api/admin/auditoria/entidad/{idEntidad}

Ejemplo:
GET /api/admin/auditoria/entidad/123
```

#### Cambios por Tipo (CREACION, ACTUALIZACION, ELIMINACION)
```
GET /api/admin/auditoria/tipo/{tipo}

Ejemplo:
GET /api/admin/auditoria/tipo/CREACION
```

#### Estadísticas de Auditoría
```
GET /api/admin/auditoria/estadisticas
```
**Respuesta:**
```json
{
  "totalEventos": 15430,
  "creaciones": 3000,
  "actualizaciones": 10230,
  "eliminaciones": 1200,
  "ultimoEvento": "2026-04-18T14:35:00"
}
```

---

## 🔍 BÚSQUEDA AVANZADA
*Busca en MySQL y MongoDB simultáneamente*

### GET Endpoints

#### Búsqueda Global
```
GET /api/admin/buscar?termino=Mercedes

Busca en: buses, rutas, usuarios, barrios
```
**Respuesta:**
```json
{
  "buses_mysql": [...],
  "buses_mongodb": [...],
  "rutas": [...],
  "usuarios": [...],
  "barrios": [...],
  "total": 45
}
```

#### Búsqueda Avanzada de Buses con Filtros
```
GET /api/admin/buscar/buses?placa=ABC&conductor=Juan&color=Azul&ruta=Centro

Parámetros opcionales:
- placa: búsqueda parcial
- conductor: búsqueda parcial
- color: búsqueda exacta
- ruta: búsqueda parcial
```

#### Búsqueda de Rutas
```
GET /api/admin/buscar/rutas?termino=Centro
```

#### Búsqueda de Usuarios
```
GET /api/admin/buscar/usuarios?termino=juan
```

#### Búsqueda de Barrios
```
GET /api/admin/buscar/barrios?termino=Getsemaní
```

---

## 📈 REPORTES Y ANALYTICS
*Genera reportes completos del sistema*

### GET Endpoints

#### Reporte General del Sistema
```
GET /api/admin/reportes/general
```
**Respuesta:**
```json
{
  "estadisticas": {
    "totalUsuarios": 3000,
    "totalRutas": 300,
    "totalBusesMySQL": 900,
    "totalBusesMongoDB": 150,
    "totalBusesGlobal": 1050
  },
  "detalles": {
    "usuarios": {...},
    "rutas": {...},
    "buses": {...}
  },
  "timestamp": "2026-04-18T14:40:00"
}
```

#### Reporte de Auditoría
```
GET /api/admin/reportes/auditoria
```
**Incluye:**
- Total de eventos
- Eventos por tipo
- Usuarios activos
- Eventos recientes

#### Reporte de Usuario Específico
```
GET /api/admin/reportes/usuario/{usuario}

Ejemplo:
GET /api/admin/reportes/usuario/juan_admin
```
**Incluye:**
- Datos del usuario
- Total de acciones
- Tipos de acciones realizadas
- Acciones recientes

#### Estadísticas para Dashboard
```
GET /api/admin/estadisticas/dashboard
```
**Respuesta:**
```json
{
  "distribucion_buses": {
    "mysql": 900,
    "mongodb": 150
  },
  "crecimiento_usuarios": {
    "total": 3000,
    "este_mes": 300,
    "porcentaje_crecimiento": 10
  },
  "rutas_top_5": [
    {
      "nombre": "Centro 1",
      "buses": 45
    }
  ],
  "timestamp": "2026-04-18T14:45:00"
}
```

#### Detalles de Usuarios
```
GET /api/admin/reportes/usuarios
```

#### Detalles de Rutas
```
GET /api/admin/reportes/rutas
```

#### Detalles de Buses
```
GET /api/admin/reportes/buses
```

---

## 🔗 ENDPOINTS ADICIONALES (ya existentes)

### Buses Unificados
```
GET /api/buses/todos                          # Todos los buses (MySQL + MongoDB)
GET /api/buses/por-ruta/{nombreRuta}          # Buses de una ruta
GET /api/buses/estadisticas                   # Estadísticas de buses
```

### Buses MongoDB
```
GET /api/buses-mongo                          # Todos los buses de MongoDB
GET /api/buses-mongo/{id}                     # Bus específico
POST /api/buses-mongo                         # Crear bus en MongoDB
DELETE /api/buses-mongo/{id}                  # Eliminar bus
```

---

## 📝 EJEMPLO DE USO COMPLETO

### 1. Sincronizar datos
```bash
curl -X POST http://localhost:8080/api/admin/sincronizar/todo
```

### 2. Buscar un bus
```bash
curl "http://localhost:8080/api/admin/buscar/buses?placa=ABC&conductor=Juan"
```

### 3. Ver auditoría
```bash
curl "http://localhost:8080/api/admin/auditoria/historial?limite=10"
```

### 4. Generar reporte
```bash
curl "http://localhost:8080/api/admin/reportes/general"
```

### 5. Ver estadísticas del dashboard
```bash
curl "http://localhost:8080/api/admin/estadisticas/dashboard"
```

---

## 🎯 CASOS DE USO

### Caso 1: Administrador revisa cambios recientes
```
GET /api/admin/auditoria/historial?limite=20
```

### Caso 2: Auditoría de un usuario específico
```
GET /api/admin/auditoria/usuario/juan
GET /api/admin/reportes/usuario/juan
```

### Caso 3: Buscar buses de un conductor
```
GET /api/admin/buscar/buses?conductor=Luis
```

### Caso 4: Sincronizar datos antes de generar reportes
```
POST /api/admin/sincronizar/todo
GET /api/admin/reportes/general
```

### Caso 5: Monitorear actividad del sistema
```
GET /api/admin/estadisticas/dashboard
GET /api/admin/auditoria/estadisticas
```

---

## ✅ CARACTERÍSTICAS IMPLEMENTADAS

✅ Sincronización automática MySQL → MongoDB
✅ Historial completo de cambios (auditoría)
✅ Búsqueda avanzada en ambas BD
✅ Filtros complejos para buses
✅ Reportes detallados del sistema
✅ Analytics y estadísticas
✅ Historial por usuario
✅ Historial por entidad

---

**Última actualización:** 18 de abril de 2026
