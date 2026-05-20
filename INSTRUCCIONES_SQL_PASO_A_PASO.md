# Guía Paso a Paso: Ejecutar Script SQL para Normalizar Roles

## ⚠️ CRÍTICO: Este paso debe ejecutarse ANTES de probar el login de admin

---

## Opción 1: Usando MySQL desde Terminal (Windows)

### 1. Abre PowerShell (como Administrador)
```powershell
# Navega a tu carpeta de MySQL
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin" # Ajusta la versión según tu instalación
```

### 2. Conectate a MySQL
```powershell
mysql -u root -p
# Ingresa tu contraseña
```

### 3. Selecciona la base de datos
```sql
USE nombre_de_tu_base_datos;
```

### 4. Copia y pega el contenido completo del archivo `FIX_ADMIN_ROLES.sql`

O ejecuta el script directamente:
```powershell
mysql -u root -p nombre_de_tu_base_datos < "C:\Users\HP\OneDrive\Desktop\Programas\Samuel\Sprint boot\Universidad\Pro.A(Actual)\Pro.Aula (Spring boot )\aula\FIX_ADMIN_ROLES.sql"
```

---

## Opción 2: Usando PHPMyAdmin (Más Fácil)

### 1. Abre tu navegador y ve a http://localhost/phpmyadmin

### 2. Ingresa con tus credenciales
- Usuario: `root` (u otro usuario)
- Contraseña: Tu contraseña

### 3. Selecciona tu base de datos en el panel izquierdo
Ej: `pro_aula`, `bustraker`, etc.

### 4. Haz clic en la pestaña "SQL"

### 5. Copia el contenido de `FIX_ADMIN_ROLES.sql`

Aquí está el script:
```sql
-- Script para corregir y normalizar roles de administrador en la base de datos
-- Este script asegura que todos los roles estén normalizados con el prefijo ROLE_

-- 1. Normalizador de roles ADMIN
-- Convierte "ADMIN" a "ROLE_ADMIN"
UPDATE usuario 
SET role = 'ROLE_ADMIN' 
WHERE role = 'ADMIN' OR role = 'admin';

-- 2. Normalizador de roles USER
-- Convierte "USER" a "ROLE_USER"
UPDATE usuario 
SET role = 'ROLE_USER' 
WHERE role = 'USER' OR role = 'user';

-- 3. Convertir cualquier rol sin prefijo ROLE_
-- Para usuarios con roles personalizados
UPDATE usuario 
SET role = CONCAT('ROLE_', role) 
WHERE role IS NOT NULL 
AND role != '' 
AND role NOT LIKE 'ROLE_%';

-- 4. Asignar rol por defecto si es NULL
UPDATE usuario 
SET role = 'ROLE_USER' 
WHERE role IS NULL OR role = '';

-- Verificar cambios realizados
SELECT id, username, nombres, role, email FROM usuario ORDER BY id;

-- Resumen de usuarios por rol
SELECT role, COUNT(*) as cantidad FROM usuario GROUP BY role;
```

### 6. Haz clic en "Continuar" o el botón de Ejecutar

### 7. Verifica los resultados
Deberías ver una tabla con tus usuarios y sus roles actualizados.

---

## Opción 3: Usando DBeaver (Interfaz Visual)

### 1. Abre DBeaver

### 2. Clic derecho en tu base de datos → "SQL Editor" → "Abrir script SQL"

### 3. Copia y pega el contenido de `FIX_ADMIN_ROLES.sql`

### 4. Presiona `Ctrl+Enter` para ejecutar

### 5. Verifica los resultados en la pestaña "Resultados"

---

## Verificación: Consulta para Verificar Cambios

Ejecuta esta consulta para verificar que todo esté bien:

```sql
-- Ver todos los usuarios y sus roles
SELECT 
    id,
    username,
    nombres,
    apellidos,
    email,
    role,
    CASE 
        WHEN role = 'ROLE_ADMIN' THEN '✅ ADMIN'
        WHEN role = 'ROLE_USER' THEN '👤 USER'
        ELSE '⚠️ OTRO: ' CONCAT(role)
    END as Estado
FROM usuario
ORDER BY id;

-- Resumen por rol
SELECT 
    role,
    COUNT(*) as cantidad,
    GROUP_CONCAT(username SEPARATOR ', ') as usuarios
FROM usuario
GROUP BY role;
```

---

## Posibles Errores y Soluciones

### Error: "Access denied for user 'root'@'localhost'"
**Causa**: Contraseña incorrecta
**Solución**: Verifica tu contraseña de MySQL

### Error: "Table 'xxxx.usuario' doesn't exist"
**Causa**: Nombre de tabla incorrecto
**Solución**: 
1. En PHPMyAdmin, ve a tu BD
2. Busca el nombre correcto de la tabla
3. Reemplaza `usuario` en el script por el nombre correcto

### Error: "No database selected"
**Causa**: Olvidaste seleccionar la BD
**Solución**: Ejecuta `USE nombre_base_datos;` primero

### No se ven cambios
**Soluciones**:
1. Recarga la página en PHPMyAdmin (`F5`)
2. Verifica que ejecutaste la consulta correctamente
3. Comprueba que hay datos en la tabla `usuario`

---

## ✅ Checklist Final

Después de ejecutar el script:

- [ ] El script se ejecutó sin errores
- [ ] Puedo ver usuarios en la tabla `usuario`
- [ ] Todos los roles tienen el prefijo `ROLE_`
- [ ] No hay roles vacíos o NULL
- [ ] Hay al menos un usuario con `ROLE_ADMIN`

---

## ¿Listo?

Una vez completado este paso:
1. Compila el proyecto: `mvn clean install`
2. Reinicia el servidor Spring Boot
3. Intenta registrarte como administrador en `/registro`
4. Intenta hacer login de admin en `/admin-login`

¡Debería funcionar ahora! 🎉
