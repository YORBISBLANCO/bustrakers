# Instrucciones para configurar la base de datos

## 1. Crear la base de datos MySQL

Ejecuta estos comandos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS proyectobd;
USE proyectobd;

-- Crear tabla admin_code
CREATE TABLE IF NOT EXISTS admin_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar códigos de administrador
INSERT IGNORE INTO admin_code (codigo) VALUES
('ADMIN2026'),
('ADMIN2025'),
('ADMIN2024'),
('MASTERADMIN'),
('SUPERADMIN');

-- Crear tabla usuario (si no existe)
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(255),
    apellidos VARCHAR(255),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 2. Ejecutar el script de corrección de roles

Ejecuta el contenido del archivo `FIX_ADMIN_ROLES.sql` en tu base de datos.

## 3. Ejecutar la aplicación

```bash
.\mvnw.cmd spring-boot:run
```

## 4. Probar el registro

### Para usuario regular:
- Ve a `/registro-mejorado`
- Selecciona "Usuario Regular"
- Completa el formulario (asegúrate de que las contraseñas coincidan)

### Para administrador:
- Ve a `/registro-mejorado`
- Selecciona "Administrador"
- Ingresa el código: `ADMIN2026`
- Completa el formulario (asegúrate de que las contraseñas coincidan)

Después del registro exitoso, serás redirigido al formulario de login con un mensaje de confirmación.

## 5. Probar el login

- Los usuarios regulares irán automáticamente a `/dashboard`
- Los administradores irán automáticamente a `/index_2`

El sistema detecta automáticamente el rol del usuario y lo redirige al dashboard correspondiente.</content>
<parameter name="filePath">c:\Users\HP\Downloads\Pro.A(Actual SQL) (1)\Pro.A(Actual)\Pro.Aula (Spring boot )\aula\CONFIGURACION_BD.md