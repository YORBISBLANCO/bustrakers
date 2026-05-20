# Bustraker - Sistema de Gestión de Rutas de Transporte Universitario

## Descripción

Bustraker es una aplicación web desarrollada con Spring Boot que permite gestionar y rastrear rutas de autobuses universitarios en Cartagena. La aplicación proporciona funcionalidades tanto para administradores como para usuarios regulares.

## Características

- **Gestión de usuarios**: Registro e inicio de sesión para administradores y usuarios
- **Gestión de rutas**: Creación y administración de rutas de transporte
- **Gestión de buses**: Control de flota de buses con placa, modelo, conductor
- **Gestión de paradas**: Definición de puntos de parada para cada ruta
- **Visualización de rutas**: Interfaz para consultar rutas disponibles
- **Panel de administración**: Interfaz dedicada para la gestión del sistema
- **Reportes**: Visualización de estadísticas y datos del sistema

## Tecnologías utilizadas

- **Backend**: Java 21, Spring Boot 3.2.12
- **Persistencia**: Spring Data JPA, Hibernate
- **Base de datos**: MySQL
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Dependencias**: Maven, Lombok, Java Faker
- **Servidor**: Embedded Tomcat

## Arquitectura

La aplicación sigue una arquitectura MVC (Modelo-Vista-Controlador) con las siguientes capas:

- **Entity**: Clases JPA que representan las entidades del dominio
- **Repository**: Interfaces Spring Data JPA para acceso a datos
- **Service**: Lógica de negocio
- **Controller**: Controladores Spring MVC para manejar las peticiones
- **Templates**: Plantillas Thymeleaf para la vista

## Entidades principales

- **Usuario**: Representa a los usuarios del sistema (administradores y usuarios regulares)
- **Ruta**: Define las rutas de transporte con barrios y paradas
- **Bus**: Representa los vehículos de transporte
- **Parada**: Puntos de parada dentro de una ruta
- **ContactoMensaje**: Mensajes de contacto/envío

## Configuración y ejecución

### Requisitos previos

- Java 21
- Maven 3.6 o superior
- MySQL Server
- Git (opcional)

### Configuración de la base de datos

1. Asegúrese de que MySQL esté instalado y en ejecución
2. El archivo `application.properties` está configurado para conectarse a una base de datos llamada `bustraker` con credenciales por defecto:
   - Usuario: `root`
   - Contraseña: vacía (en blanco)

### Pasos para ejecutar la aplicación

1. Clone o descargue el proyecto
2. Asegúrese de tener Maven instalado
3. Abra una terminal en el directorio raíz del proyecto
4. Ejecute el siguiente comando:

```bash
mvn spring-boot:run
```

5. La aplicación se ejecutará en `http://localhost:3460`

### Configuración alternativa

También puede construir el archivo JAR y ejecutarlo:

```bash
mvn clean package
java -jar target/aula-0.0.1-SNAPSHOT.jar
```

## Características especiales

- **Data Seeder**: La aplicación incluye un servicio de carga inicial de datos que genera datos de ejemplo para desarrollo y pruebas
- **Validación**: Validación de datos en el lado del servidor
- **Responsive Design**: Interfaz adaptable a diferentes tamaños de pantalla
- **Seguridad básica**: Autenticación de usuarios

## Seguridad

La aplicación usa Spring Security para proteger rutas y validar usuarios.

### Autenticación con JWT

- Al iniciar sesión con nombre de usuario y contraseña, el sistema genera un token JWT.
- El token se firma con una clave secreta configurada en `application.properties`.
- El token JWT se guarda en una cookie `jwt` con `HttpOnly`.
- En cada petición, el filtro `JwtAuthenticationFilter` revisa la cookie `jwt` y el encabezado `Authorization: Bearer ...`.
- Si el token es válido, carga el usuario desde MongoDB con `CustomUserDetailsService` y marca la petición como autenticada.

### Login local y API

- El login tradicional usa el formulario de `/login` configurado en `SecurityConfig`.
- La API de autenticación está disponible en `/api/auth/login` y `/api/auth/register`.
- El controlador `AuthRestController` crea el JWT y retorna respuesta 200 si el login es correcto.

### OAuth2 y usuarios externos

- La aplicación soporta login a través de OAuth2 con proveedores externos.
- `CustomOAuth2UserService` mapea el usuario externo a un usuario local en MongoDB.
- Si el email del proveedor no existe, crea un nuevo `Usuario` con `ROLE_USER` y marca `passwordSetupRequired = true`.
- Luego `CustomAuthenticationSuccessHandler` genera el JWT y redirige al usuario.

### Manejo de sesión y logout

- La aplicación está configurada como `STATELESS`, es decir, no mantiene sesión en el servidor.
- El control de acceso se hace con roles: `ROLE_ADMIN` y `ROLE_USER`.
- Al cerrar sesión, la ruta `/logout` borra la cookie `jwt` y redirige a `/`.

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/proaula/aula/
│   │   ├── Entity/          # Clases de entidad JPA
│   │   ├── Repository/      # Interfaces de repositorio
│   │   ├── Service/         # Lógica de negocio
│   │   ├── Controller/      # Controladores Spring MVC
│   │   ├── Barrios/         # Datos de localidades y barrios
│   │   └── AulaApplication.java  # Clase principal
│   └── resources/
│       ├── templates/       # Plantillas Thymeleaf
│       ├── static/          # Recursos estáticos (CSS, JS, imágenes)
│       └── application.properties  # Configuración
```

## Uso

### Para usuarios

1. Acceda a la página principal
2. Regístrese como nuevo usuario o inicie sesión si ya tiene cuenta
3. Consulte las rutas disponibles
4. Puede enviar mensajes de contacto

### Para administradores

1. Haga clic en el botón de administrador (icono rojo en la esquina inferior derecha)
2. Ingrese el código de administrador (por defecto: `ADMIN2026`)
3. Acceda al panel de administración
4. Gestione usuarios, buses, rutas y visualice reportes

## Contribuciones

Las contribuciones son bienvenidas. Por favor, siga las siguientes pautas:

1. Cree un fork del proyecto
2. Cree una rama para su característica (`git checkout -b feature/NuevaCaracteristica`)
3. Haga commit de sus cambios (`git commit -m 'Agrega nueva característica'`)
4. Suba sus cambios (`git push origin feature/NuevaCaracteristica`)
5. Abra un Pull Request

## Licencia

Este proyecto es de uso académico y forma parte de un trabajo universitario.

## Contacto

Proyecto universitario desarrollado por Samuel en el programa de ProAula.