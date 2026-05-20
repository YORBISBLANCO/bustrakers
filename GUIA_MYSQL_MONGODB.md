# 📊 GUÍA COMPLETA: MySQL vs MongoDB en ProAula

## 1. BASES DE DATOS DISPONIBLES

### 🗄️ MySQL (yorbisbd) - JPA Entities
Almacena datos estructurados y relacionales:

| Tabla | Descripción | Campos Principales |
|-------|-------------|-------------------|
| **usuario** | Usuarios registrados | id, username, password, email, nombres, apellidos, role |
| **ruta** | Rutas de buses | id, nombre, horaAproximada, barrios |
| **bus** | Buses del sistema | id, placa, modelo, color, conductor, ruta_id |
| **parada** | Paradas de cada ruta | id, nombre, ubicacion, referencia, orden, ruta_id |
| **barrio** | Barrios de Cartagena | id, nombre, localidad, latitud, longitud |
| **contacto_mensaje** | Mensajes de contacto | id, nombre, apellido, telefono, email, mensaje |
| **admin_code** | Códigos de administrador | id, codigo, descripcion |

### 📄 MongoDB (proyectobd) - Documents
Almacena datos no-estructurados o semi-estructurados:

| Colección | Descripción | Campos |
|----------|-------------|--------|
| **bustrakersbd** | Buses alternativos/históricos | id, placa, marca, modelo, color, conductor, ruta, estado |
| **personas** | Datos de personas | id, nombre, edad |

---

## 2. OPERACIONES CON MySQL

### ✅ Operaciones CRUD disponibles:

#### 🔍 LEER - GET Endpoints
```
GET /api/usuarios                           # Todos los usuarios
GET /api/usuarios/{id}                      # Usuario específico
GET /api/rutas                              # Todas las rutas
GET /api/rutas/{id}                         # Ruta específica
GET /api/buses                              # Todos los buses
GET /api/buses/{id}                         # Bus específico
GET /api/paradas                            # Todas las paradas
GET /api/barrios                            # Todos los barrios
GET /api/barrios/{id}                       # Barrio específico
GET /api/contactos                          # Todos los mensajes
GET /dashboard                              # Dashboard del usuario
GET /index_2                                # Dashboard del admin
```

#### ➕ CREAR - POST Endpoints
```
POST /api/usuarios
{
  "username": "juan123",
  "password": "password123",
  "email": "juan@example.com",
  "nombres": "Juan",
  "apellidos": "García",
  "role": "ROLE_USER"
}

POST /api/rutas
{
  "nombre": "Centro 1",
  "horaAproximada": "06:00",
  "barrios": ["Centro", "Getsemaní"]
}

POST /api/buses
{
  "placa": "ABC-123",
  "modelo": "Mercedes 2023",
  "color": "Blanco",
  "conductor": "Juan García",
  "ruta": 1
}

POST /api/paradas
{
  "nombre": "Estación Central",
  "ubicacion": "Centro",
  "referencia": "Frente al mercado",
  "orden": 1,
  "ruta": 1
}
```

#### ✏️ ACTUALIZAR - PUT Endpoints
```
PUT /api/usuarios/{id}
PUT /api/rutas/{id}
PUT /api/buses/{id}
PUT /api/paradas/{id}
```

#### ❌ ELIMINAR - DELETE Endpoints
```
DELETE /api/usuarios/{id}
DELETE /api/rutas/{id}
DELETE /api/buses/{id}
DELETE /api/paradas/{id}
```

---

## 3. OPERACIONES CON MongoDB

### ✅ Operaciones CRUD disponibles:

#### 🔍 LEER - GET Endpoints
```
GET /api/buses-mongo                        # Todos los buses de MongoDB
GET /api/buses-mongo/{id}                   # Bus específico
GET /personas                               # Todas las personas
GET /api/buses/todos                        # Buses de MySQL + MongoDB
GET /api/buses/por-ruta/{nombreRuta}        # Buses por ruta (ambas BD)
GET /api/buses/estadisticas                 # Estadísticas de buses
```

#### ➕ CREAR - POST Endpoints
```
POST /api/buses-mongo
{
  "placa": "XYZ-999",
  "marca": "Mercedes",
  "modelo": "2024",
  "color": "Rojo",
  "conductor": "Luis",
  "ruta": "Centro 1",
  "estado": "activo"
}

POST /personas
{
  "nombre": "Carlos López",
  "edad": 28
}
```

#### ✏️ ACTUALIZAR - PUT Endpoints
```
PUT /api/buses-mongo/{id}
PUT /personas/{id}
```

#### ❌ ELIMINAR - DELETE Endpoints
```
DELETE /api/buses-mongo/{id}
DELETE /personas/{id}
```

---

## 4. DIFERENCIAS CLAVE

| Aspecto | MySQL | MongoDB |
|--------|-------|---------|
| **Tipo** | Relacional | Documento (NoSQL) |
| **Estructura** | Rígida (tablas) | Flexible (documentos JSON) |
| **Relaciones** | @ManyToOne, @OneToMany | Embebidas o referencias |
| **Uso** | Datos estructurados | Datos históricos/flexibles |
| **Escalabilidad** | Vertical | Horizontal |
| **Transacciones** | ACID completo | ACID limitado |

---

## 5. CASOS DE USO

### 📍 MySQL - Perfecto para:
✅ Datos de usuarios y autenticación
✅ Información de rutas y paradas
✅ Asignación de buses a rutas
✅ Mensajes de contacto
✅ Datos con relaciones complejas

### 📍 MongoDB - Perfecto para:
✅ Historial de buses (bustrakersbd)
✅ Datos de seguimiento en tiempo real
✅ Información de pasajeros/personas
✅ Logs y eventos
✅ Datos semi-estructurados

---

## 6. RECOMENDACIONES DE INTEGRACIÓN

### Opción 1: Sincronización de Datos
Cuando un bus se crea en MySQL → Automáticamente se copia a MongoDB

### Opción 2: Historial
MySQL = datos actuales
MongoDB = historial/auditoría

### Opción 3: Búsqueda Dual
Los usuarios ven buses de ambas bases de datos

---

## 7. PRÓXIMOS PASOS

1. ✅ Ya creaste: **BusUnificadoService** (combina ambas BD)
2. 📝 Puedes crear: **HistorialService** (logs en MongoDB)
3. 🔄 Puedes hacer: **Sincronización automática** de datos
4. 📊 Puedes agregar: **Analytics/reportes** desde ambas BD

