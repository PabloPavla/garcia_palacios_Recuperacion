# 🎬 WatchAPI - Plataforma de Gestión de Favoritos y Reseñas de Cine

WatchAPI es una API REST robusta y segura desarrollada con **Java 17** y **Spring Boot**, diseñada para permitir a los usuarios gestionar su lista de películas/series favoritas y escribir reseñas de títulos cinematográficos. La aplicación se conecta de forma dinámica con la API externa de **Watchmode** para buscar títulos e importar su información en una base de datos local **MySQL**.

---

## 🚀 Características Principales

*   **Autenticación y Seguridad (JWT Stateless):**
    *   Registro de usuarios con contraseñas cifradas mediante `BCrypt`.
    *   Flujo completo de tokens mediante **Access Token** (corta duración para peticiones seguras) y **Refresh Token** (larga duración para renovación de sesión).
    *   Filtro de seguridad personalizado (`JwtAuthenticationFilter`) que intercepta y valida las peticiones.
*   **Integración con API Externa (Watchmode):**
    *   Consumo de la API de Watchmode para buscar películas y series en tiempo real.
    *   **Estrategia de caché en base de datos:** Los títulos buscados se registran localmente en MySQL en el momento en que se añaden a favoritos o se reseñan, optimizando el rendimiento y minimizando el consumo de la cuota de la API externa.
*   **Gestión de Favoritos:**
    *   Añadir películas/series a favoritos mediante su identificador único de Watchmode.
    *   Listar los favoritos del usuario autenticado.
    *   Eliminar títulos de la lista de favoritos.
*   **Sistema de Reseñas (CRUD completo):**
    *   Los usuarios pueden publicar reseñas sobre películas o series, asignando un comentario de texto y una nota numérica.
    *   Validación de permisos: los usuarios solo pueden editar o borrar sus propias reseñas.
*   **Perfiles de Usuario:**
    *   Visualización de perfiles públicos de otros usuarios (lista de favoritos y reseñas escritas).
    *   Edición del nombre de usuario propio (`username`).
*   **Documentación Interactiva:**
    *   Integración con Swagger/OpenAPI para explorar y probar todos los endpoints desde el navegador.
*   **Manejo Global de Excepciones:**
    *   Respuestas de error estandarizadas en formato JSON para mejorar la experiencia de integración de clientes.

---

## 🛠️ Tecnologías y Librerías Utilizadas

*   **Java 17** (JDK)
*   **Spring Boot 3.5.7** (Core, Web, Security, Data JPA, Actuator, DevTools)
*   **MySQL Connector J** & **HikariCP** (Conexión a Base de Datos)
*   **Lombok** (Reducción de código boilerplate)
*   **io.jsonwebtoken (JJWT 0.12.3)** (Creación y firma de tokens JWT)
*   **Springdoc OpenAPI UI (2.7.0)** (Documentación interactiva de la API)
*   **JUnit 5, Mockito & AssertJ** (Pruebas unitarias de calidad)
*   **JaCoCo (0.8.12)** (Medición del porcentaje de cobertura de código)

---

## 📂 Estructura del Código

El proyecto sigue una arquitectura clásica orientada al dominio (Controller ➔ Service ➔ Repository):

```text
src/main/java/org/vedruna/watchapi/
├── config/                  # Configuraciones generales (Swagger, RestTemplate, etc.)
├── controller/              # Controladores REST principales y DTOs
│   ├── converter/           # Conversores de Entidad ➔ DTO (y viceversa)
│   └── dto/                 # Data Transfer Objects para peticiones y respuestas
├── exception/               # Manejador global de excepciones y excepciones personalizadas
├── persistance/             # Capa de datos (Modelos de Entidad JPA y Repositorios)
│   ├── model/               # Entidades (User, Rol, Title, Review)
│   └── repository/          # Interfaces JPA (UserRepository, TitleRepository, etc.)
├── security/                # Módulo independiente de Seguridad y Auth JWT
│   ├── config/              # Configuración de Spring Security y Criptografía
│   ├── controller/          # Endpoints de Login, Registro y Refresh Token
│   ├── filter/              # Filtro JwtAuthenticationFilter
│   └── service/             # Lógica de creación de Tokens y AuthService
└── service/                 # Capa de servicios con la lógica de negocio principal
```

---

## 🏁 Cómo Iniciar el Proyecto

### 1. Requisitos Previos
*   Tener instalado **Java JDK 17** o superior.
*   Tener **MySQL** ejecutándose en el puerto `3306`.
*   Crear una base de datos llamada `watchapi` o permitir que Spring Boot la cree automáticamente (configurado en `createDatabaseIfNotExist=true`). El usuario por defecto configurado es `root` con contraseña `root`.

### 2. Ejecutar la Aplicación
Puedes iniciar el proyecto usando el Maven Wrapper que viene incluido en la raíz:

```bash
# En Windows (CMD/PowerShell)
./mvnw spring-boot:run

# En Linux/macOS
./mvnw spring-boot:run
```

El servidor web arrancará en el puerto **`8080`**.

### 3. Scripts de Inicialización
Al iniciar, Spring Boot leerá y ejecutará de forma automática los siguientes scripts ubicados en `src/main/resources/db/mysql/` para poblar la base de datos:
*   `schema.sql`: Estructura física de tablas.
*   `data.sql`: Carga del rol inicial `USER`.

---

## 📖 Documentación y Pruebas de Endpoints

### Documentación de Swagger
Una vez que el servidor esté activo, abre tu navegador y accede a:
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Cliente REST local (`request.http`)
En la raíz del proyecto se incluye un archivo de pruebas rápidas llamado **`request.http`**. Si utilizas VS Code, puedes instalar la extensión **REST Client** de *Huachao Mao* para enviar peticiones directamente al servidor local haciendo clic en "Send Request" encima de cada ruta definida en el archivo.

---

## 🧪 Pruebas y Cobertura (JaCoCo)

Para ejecutar la suite de pruebas unitarias implementadas con Mockito y verificar la cobertura de código, ejecuta:

```bash
./mvnw clean verify
```

JaCoCo está configurado en el archivo `pom.xml` para asegurar los siguientes umbrales mínimos de cobertura en la fase de verificación:
*   Mínimo del **80%** de cobertura general de líneas (`BUNDLE`).
*   Mínimo del **50%** de cobertura en clases específicas (`CLASS`), excluyendo modelos, DTOs y clases de configuración de seguridad autogeneradas.
