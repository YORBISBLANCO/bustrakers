# 📋 Guía Completa: Migración de MySQL a MongoDB

## 🎯 Estado de la Migración

### ✅ Completado:
1. **Documentos MongoDB** - Creados para todas las entidades
   - Usuario.java
   - Ruta.java
   - Bus.java
   - Parada.java
   - Barrio.java
   - ContactoMensaje.java
   - AdminCode.java

2. **Repositorios MongoDB** - Creados en `Repository/mongodb/`
   - UsuarioRepository.java
   - RutaRepository.java
   - BusRepository.java
   - ParadaRepository.java
   - BarrioRepository.java
   - ContactoMensajeRepository.java
   - AdminCodeRepository.java

3. **application.properties** - Configuración actualizada
   - Eliminadas todas las propiedades de MySQL/JPA
   - MongoDB como única BD

4. **pom.xml** - Dependencias limpias
   - Eliminada: `spring-boot-starter-data-jpa`
   - Eliminada: `mysql-connector-j`
   - Mantenida: `spring-boot-starter-data-mongodb`

5. **Services Actualizados**:
   - ✅ UsuarioService.java
   - ✅ RutaService.java

6. **Controllers Actualizados**:
   - ✅ UsuarioController.java

---

## 📝 Pasos para Completar la Migración

### **Paso 1: Actualizar Services Restantes**

#### Para cada Service, reemplaza:

**ANTES:**
```java
import com.proaula.aula.Entity.XYZ;
import com.proaula.aula.Repository.XYZRepository;

@Autowired
private XYZRepository repository;
```

**DESPUÉS:**
```java
import com.proaula.aula.document.XYZ;
import com.proaula.aula.Repository.mongodb.XYZRepository;

@Autowired
private XYZRepository repository;
```

#### Services a actualizar:
- [ ] BusService.java
- [ ] BarrioService.java
- [ ] ContactoMensajeService.java
- [ ] AdminCodeService.java
- [ ] RutaService.java (✅ Ya hecho)
- [ ] CustomUserDetailsService.java
- [ ] DataSeederService.java
- [ ] SincronizacionService.java
- [ ] AuditoriaService.java
- [ ] ReportesAnalyticsService.java

---

### **Paso 2: Actualizar Controllers**

Para cada Controller, reemplaza:

**ANTES:**
```java
import com.proaula.aula.Entity.XYZ;
```

**DESPUÉS:**
```java
import com.proaula.aula.document.XYZ;
```

#### Controllers a actualizar:
- [ ] UsuarioController.java (✅ Ya hecho)
- [ ] RutaRestController.java
- [ ] BusRestController.java
- [ ] BarrioController.java
- [ ] ContactoController.java
- [ ] DashboardController.java
- [ ] AdminLoginController.java
- [ ] AdminSystemController.java
- [ ] BusController.java
- [ ] BusesUnificadosController.java
- [ ] BusMongoController.java
- [ ] HomeController.java
- [ ] PerfilController.java
- [ ] PersonaController.java

---

## 🔄 Cambios en Métodos Comunes

### **IDs - Cambio de Long a String**

**Ruta:**
```java
// ANTES
public Ruta getRutaById(Long id) { ... }

// DESPUÉS
public Ruta getRutaById(String id) { ... }
```

### **Optional en Repositorios MongoDB**

**Cambio de retorno directo a Optional:**
```java
// ANTES
Usuario user = usuarioRepository.findByUsername(username);
if (user != null) { ... }

// DESPUÉS
Optional<Usuario> user = usuarioRepository.findByUsername(username);
if (user.isPresent()) {
    Usuario u = user.get();
    // usar u
}
```

### **Eliminación de @Transactional**

MongoDB no usa transacciones como JPA, elimina:
```java
// ANTES
@Transactional
public void método() { ... }

// DESPUÉS
public void método() { ... }
```

### **Eliminación de Relaciones JPA**

**ANTES (JPA con relaciones):**
```java
@ManyToOne
@JoinColumn(name = "ruta_id")
private Ruta ruta;
```

**DESPUÉS (MongoDB - documentos denormalizados):**
```java
private String rutaId;
private String rutaNombre;
```

---

## 📊 Ejemplo Completo: BusService

```java
package com.proaula.aula.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Repository.mongodb.BusRepository;

@Service
public class BusService {
    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(String id) {
        return busRepository.findById(id).orElse(null);
    }

    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    public void deleteBus(String id) {
        busRepository.deleteById(id);
    }

    public List<Bus> findByRuta(String rutaId) {
        return busRepository.findByRutaId(rutaId);
    }

    public List<Bus> findByConductor(String conductor) {
        return busRepository.findByConductor(conductor);
    }

    public List<Bus> findByRutaNombre(String rutaNombre) {
        return busRepository.findByRutaNombre(rutaNombre);
    }

    public long count() {
        return busRepository.count();
    }
}
```

---

## 🧪 Pruebas Post-Migración

### 1. Verificar conexión a MongoDB
```bash
# Terminal MongoDB
mongo
use proyectobd
db.usuarios.find()
```

### 2. Compilar proyecto
```bash
cd aula
mvn clean compile
```

### 3. Ejecutar aplicación
```bash
mvn spring-boot:run
```

### 4. Probar endpoints clave
```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/usuarios/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123","email":"test@test.com","nombres":"Test","apellidos":"User"}'

# Login
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

---

## ⚠️ Notas Importantes

### Diferencias clave MongoDB vs MySQL/JPA:

| Aspecto | MySQL/JPA | MongoDB |
|--------|-----------|---------|
| **ID** | Long (autogenerado) | String (ObjectId) |
| **Relaciones** | @ManyToOne, @OneToMany | Documentos denormalizados |
| **Transacciones** | @Transactional | No soportadas por defecto |
| **Validación** | Validadores JPA | Lombok + Validadores |
| **Queries** | JPQL/SQL | Métodos en repository |

### Considerar:
1. **Datos existentes** - Necesita script de migración de MySQL a MongoDB
2. **Índices** - Crear índices en MongoDB para performance
3. **Denormalización** - MongoDB favorece documentos denormalizados
4. **Logging** - Validar logs de conexión a MongoDB

---

## 📞 Checklist Final

- [ ] Todos los Services actualizados con imports de `document`
- [ ] Todos los Controllers actualizados con imports de `document`
- [ ] Eliminar carpeta `Entity/` si no es usada por otras integraciones
- [ ] Pruebas de CRUD funcionando
- [ ] Datos migrando correctamente desde MySQL a MongoDB
- [ ] Validar que no haya imports de `Entity` en el código nuevo

---

## 🚀 Comando para buscar imports antiguos:

```bash
# Buscar todos los imports de Entity en los Services
grep -r "import com.proaula.aula.Entity" src/main/java/com/proaula/aula/Service/

# Buscar todos los imports de Entity en los Controllers
grep -r "import com.proaula.aula.Entity" src/main/java/com/proaula/aula/Controller/
```

---

**Última actualización:** 18 de abril de 2026
