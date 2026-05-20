# 🔧 INSTRUCCIONES PASO A PASO: Completar la Migración

## 📋 Tareas Pendientes

### **LISTA DE SERVICIOS A ACTUALIZAR** (11 servicios)

#### 1. **BusService.java**
**Cambios requeridos:**
```java
// LÍNEA 1-10: Cambiar imports
- import com.proaula.aula.Entity.Bus;
+ import com.proaula.aula.document.Bus;

- import com.proaula.aula.Repository.BusRepository;
+ import com.proaula.aula.Repository.mongodb.BusRepository;

// LÍNEA ~20: Cambiar tipos de ID
- public Bus getBusById(Long id)
+ public Bus getBusById(String id)

- public void deleteBus(Long id)
+ public void deleteBus(String id)
```

#### 2. **BarrioService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.Barrio;
+ import com.proaula.aula.document.Barrio;

- import com.proaula.aula.Repository.BarrioRepository;
+ import com.proaula.aula.Repository.mongodb.BarrioRepository;
```

#### 3. **ContactoMensajeService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.ContactoMensaje;
+ import com.proaula.aula.document.ContactoMensaje;

- import com.proaula.aula.Repository.ContactoMensajeRepository;
+ import com.proaula.aula.Repository.mongodb.ContactoMensajeRepository;
```

#### 4. **AdminCodeService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.AdminCode;
+ import com.proaula.aula.document.AdminCode;

- import com.proaula.aula.Repository.AdminCodeRepository;
+ import com.proaula.aula.Repository.mongodb.AdminCodeRepository;
```

#### 5. **CustomUserDetailsService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.Usuario;
+ import com.proaula.aula.document.Usuario;

// Ajustar busquedas de usuario a Optional:
- Usuario user = usuarioRepository.findByUsername(username);
- if (user == null) { ... }
+ Optional<Usuario> user = usuarioRepository.findByUsername(username);
+ if (!user.isPresent()) { ... }
```

#### 6. **DataSeederService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.*;
+ import com.proaula.aula.document.*;

- import com.proaula.aula.Repository.*;
+ import com.proaula.aula.Repository.mongodb.*;

// Cambiar IDs en asignaciones de relaciones
- bus.setRuta(ruta); // NO FUNCIONA EN MONGODB
+ bus.setRutaId(ruta.getId());
+ bus.setRutaNombre(ruta.getNombre());
```

#### 7. **SincronizacionService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.*;
+ import com.proaula.aula.document.*;

- import com.proaula.aula.Repository.*;
+ import com.proaula.aula.Repository.mongodb.*;
```

#### 8. **AuditoriaService.java**
Probablemente ya usa MongoDB. Verifica los imports.

#### 9. **ReportesAnalyticsService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.*;
+ import com.proaula.aula.document.*;

// Ajustar queries de análisis
```

#### 10. **BusquedaAvanzadaService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.*;
+ import com.proaula.aula.document.*;

- import com.proaula.aula.Repository.*;
+ import com.proaula.aula.Repository.mongodb.*;
```

#### 11. **BusUnificadoService.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.*;
+ import com.proaula.aula.document.*;

- import com.proaula.aula.Repository.*;
+ import com.proaula.aula.Repository.mongodb.*;
```

---

### **LISTA DE CONTROLLERS A ACTUALIZAR** (13 controllers)

#### 1. **RutaRestController.java**
**Cambios requeridos:**
```java
// LÍNEA 1: Cambiar import de Entity
- import com.proaula.aula.Entity.Ruta;
+ import com.proaula.aula.document.Ruta;

// TODO Lo demás debe funcionar igual
```

#### 2. **BusRestController.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.Bus;
+ import com.proaula.aula.document.Bus;

// Cambiar tipos de ID
- public Bus getBusById(@PathVariable Long id)
+ public Bus getBusById(@PathVariable String id)

- public void deleteBus(@PathVariable Long id)
+ public void deleteBus(@PathVariable String id)
```

#### 3. **BarrioController.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.Barrio;
+ import com.proaula.aula.document.Barrio;
```

#### 4. **ContactoController.java**
**Cambios requeridos:**
```java
- import com.proaula.aula.Entity.ContactoMensaje;
+ import com.proaula.aula.document.ContactoMensaje;
```

#### 5. **DashboardController.java**
**Cambios requeridos:**
```java
// Buscar y reemplazar ALL de Entity a document
```

#### 6-13. **Otros Controllers**
Aplica el mismo patrón: cambiar todos los imports de `Entity` a `document`.

---

## 🔄 Procedimiento Automático (Recomendado)

### Opción 1: Reemplazar en todo el proyecto

```bash
# Navegar al directorio del proyecto
cd "c:\Users\HP\Downloads\Pro.A(Actual SQL) (1)\Pro.A(Actual)\Pro.Aula (Spring boot )\aula"

# WINDOWS - Reemplazar Entity por document en todos los archivos
for /r . %%A in (*.java) do (
    PowerShell -Command "(Get-Content '%%A') -replace 'import com.proaula.aula.Entity', 'import com.proaula.aula.document' | Set-Content '%%A'"
)

# WINDOWS - Reemplazar repositorio normal por MongoDB
for /r . %%A in (*.java) do (
    PowerShell -Command "(Get-Content '%%A') -replace 'import com.proaula.aula.Repository;', 'import com.proaula.aula.Repository.mongodb;' | Set-Content '%%A'"
)
```

### Opción 2: Script PowerShell (Más seguro)

Crea un archivo `migrate.ps1`:
```powershell
# migrate.ps1
$projectPath = "c:\Users\HP\Downloads\Pro.A(Actual SQL) (1)\Pro.A(Actual)\Pro.Aula (Spring boot )\aula"

$files = Get-ChildItem -Path $projectPath -Filter "*.java" -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    
    # Reemplazo 1: Entity -> document
    $content = $content -replace 'import com\.proaula\.aula\.Entity\.', 'import com.proaula.aula.document.'
    
    # Reemplazo 2: Repositorios normales -> mongodb
    $content = $content -replace 'import com\.proaula\.aula\.Repository\.([A-Z])', 'import com.proaula.aula.Repository.mongodb.$1'
    
    Set-Content $file.FullName $content
    Write-Host "✅ Actualizado: $($file.Name)"
}

Write-Host "✅ Migración automática completada"
```

Ejecutar:
```bash
powershell -ExecutionPolicy Bypass -File migrate.ps1
```

---

## 📝 Checklist Manual (Si prefieres hacer cambios uno a uno)

### Paso 1: Actualizar Imports en Services
- [ ] BusService.java
- [ ] BarrioService.java
- [ ] ContactoMensajeService.java
- [ ] AdminCodeService.java
- [ ] CustomUserDetailsService.java
- [ ] DataSeederService.java
- [ ] SincronizacionService.java
- [ ] AuditoriaService.java
- [ ] ReportesAnalyticsService.java
- [ ] BusquedaAvanzadaService.java
- [ ] BusUnificadoService.java

### Paso 2: Actualizar Imports en Controllers
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

### Paso 3: Validar Cambios
```bash
# Buscar imports antiguos que quedan
grep -r "import com.proaula.aula.Entity" src/main/java --include="*.java"

# Deben mostrar 0 resultados si está todo actualizado
```

### Paso 4: Compilar y Probar
```bash
# Limpiar y compilar
mvn clean compile

# Si hay errores de compilación, revisar:
# - Tipos de retorno (Long -> String para IDs)
# - Cambios en Optional (usar .isPresent() y .get())
# - Eliminación de @Transactional
```

---

## 🧪 Validaciones Post-Actualización

### 1. Verificar compilación
```bash
mvn clean compile
```
**Resultado esperado:** `BUILD SUCCESS`

### 2. Verificar que MongoDB está corriendo
```bash
# En otra terminal
mongo
> db.version()
```
**Resultado esperado:** Versión de MongoDB (ej: 5.0.0)

### 3. Ejecutar aplicación
```bash
mvn spring-boot:run
```
**Resultado esperado:** Aplicación inicia en puerto 8080

### 4. Probar endpoints de prueba
```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/usuarios/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"password\":\"test123\",\"email\":\"test@test.com\",\"nombres\":\"Test\",\"apellidos\":\"User\"}"

# Resultado esperado: Usuario creado en MongoDB
```

---

## 📊 Orden Recomendado de Actualización

1. **Services** (11) - Primero actualiza los Services
2. **Controllers** (13) - Luego actualiza los Controllers
3. **Compilar** - Compila el proyecto
4. **Probar** - Prueba endpoints
5. **Migrar Datos** - Ejecuta el script Python
6. **Validar** - Verifica datos en MongoDB

---

## 🚨 Errores Comunes y Soluciones

### Error: "Long cannot be cast to String"
**Causa:** IDs aún son Long
**Solución:** Cambiar tipos de parámetro de Long a String

### Error: "Cannot resolve symbol 'Entity'"
**Causa:** Imports antiguos
**Solución:** Reemplazar con imports de `document`

### Error: "Optional object cannot be cast to Usuario"
**Causa:** No verificas .isPresent() antes de .get()
**Solución:** Usar `if (optional.isPresent()) { Usuario u = optional.get(); }`

### Error: "Cannot find mongodb repository"
**Causa:** Imports apuntando a Repository normal
**Solución:** Cambiar a `Repository.mongodb.NombreRepository`

---

## ✅ Validación Final

Cuando completes todas las actualizaciones:
```bash
# 1. Compilar
mvn clean compile  ✅

# 2. Ejecutar aplicación
mvn spring-boot:run  ✅

# 3. Verificar MongoDB tiene datos
mongo
use proyectobd
db.usuarios.countDocuments()  # Debe mostrar número > 0

# 4. Probar CRUD endpoints
curl http://localhost:8080/api/usuarios  ✅
curl http://localhost:8080/api/rutas    ✅
curl http://localhost:8080/api/buses    ✅
```

Si todos los pasos son exitosos ✅, ¡la migración está completa!

---

**Última actualización:** 18 de abril de 2026
