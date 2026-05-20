# MongoDB Database Diagram

Este archivo describe la estructura NoSQL que ya está implementada en el proyecto `aula` y documenta las colecciones de MongoDB usadas por la aplicación.

## Conexión MongoDB
- URI configurada en `BOOT-INF/classes/application-dev.properties`
- `spring.data.mongodb.uri=mongodb://localhost:27017/proyectobd`
- Base de datos: `proyectobd`

## Colecciones principales
- `usuarios`
- `rutas`
- `buses`
- `paradas`
- `barrios`
- `contacto_mensajes`
- `personas`
- `viaje` (embebido en usuarios)
- `admin_codes`
- `auditoria_logs`

## Diagrama de datos
```mermaid
erDiagram
    usuarios {
      String _id
      String username
      String password
      String email
      String nombres
      String apellidos
      String role
      long fechaRegistro
      boolean activo
    }
    rutas {
      String _id
      String nombre
      LocalTime horaAproximada
      Array barrios
      long fechaCreacion
      boolean activa
    }
    buses {
      String _id
      String placa
      String marca
      String modelo
      String color
      String conductor
      String rutaId
      String rutaNombre
      String estado
      long fechaRegistro
      boolean activo
    }
    paradas {
      String _id
      String nombre
      String ubicacion
      String referencia
      int orden
      String rutaId
      String rutaNombre
      long fechaCreacion
      boolean activa
    }
    barrios {
      String _id
      String nombre
      String localidad
      double latitud
      double longitud
      long fechaCreacion
      boolean activo
    }
    contacto_mensajes {
      String _id
      String nombre
      String apellido
      String telefono
      String email
      String mensaje
      long fechaCreacion
      boolean leido
    }
    personas {
      String _id
      String nombre
      int edad
    }
    viaje {
      String origen
      String destino
      String busPlaca
      String busRuta
      String fecha
      String hora
      int pasajeros
      long reservadoEn
    }
    admin_codes {
      String _id
      String codigo
      String descripcion
      long fechaCreacion
      boolean activo
    }
    auditoria_logs {
      String _id
      String tipo
      String descripcion
      String usuario
      String idEntidad
      LocalDateTime fecha
      String ip
      String detalles
    }

    rutas ||--o{ buses : "rutaId"
    rutas ||--o{ paradas : "rutaId"
```

## Población de datos
El proyecto incluye un script de migración en `migrate_to_mongodb.py` que toma datos de MySQL (`yorbisbd`) y los inserta en MongoDB (`proyectobd`).

## Ubicación de la implementación
- Documentos MongoDB: `src/main/java/com/proaula/aula/document`
- Repositorios MongoDB: `src/main/java/com/proaula/aula/Repository/mongodb`
- Documentación de migración: `GUIA_MYSQL_MONGODB.md`, `MIGRACION_MONGODB.md`
- Script de migración: `migrate_to_mongodb.py`
