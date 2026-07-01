# Sistema de Convocatorias — USCO

Aplicación web para la gestión de convocatorias universitarias. Permite crear y publicar convocatorias, inscribir estudiantes, aprobar o rechazar solicitudes y consultar reportes estadísticos.

## Stack tecnológico

| Capa        | Tecnología                                      |
|-------------|-------------------------------------------------|
| Frontend    | Angular 20, TypeScript, SweetAlert2             |
| Backend     | Spring Boot 3.5, Java 17, Spring Security, JWT  |
| Base de datos | PostgreSQL 17, Flyway                         |
| Infraestructura | Docker Compose, Adminer                     |

## Arquitectura general

```
┌─────────────┐     HTTP/JWT      ┌─────────────┐     JDBC      ┌────────────┐
│   Angular   │ ◄──────────────► │ Spring Boot │ ◄───────────► │ PostgreSQL │
│  :4200      │                   │   :8080     │               │   :5432    │
└─────────────┘                   └─────────────┘               └────────────┘
                                         ▲
                                         │ Adminer :8081
```

## Requisitos previos

- [Docker](https://www.docker.com/) y Docker Compose
- Opcional (desarrollo local sin Docker): Java 17+, Maven 3.9+, Node.js 22+, PostgreSQL 17

## Inicio rápido con Docker

1. Clona el repositorio y entra al directorio del proyecto.

2. Crea el archivo de variables de entorno:

```bash
cp .env.example .env
```

Edita `.env` y define un `JWT_SECRET` seguro (mínimo 32 caracteres).

3. Levanta todos los servicios:

```bash
docker compose up --build
```

4. Accede a la aplicación:

| Servicio   | URL                          |
|------------|------------------------------|
| Frontend   | http://localhost:4200        |
| Backend API | http://localhost:8080       |
| Adminer (DB) | http://localhost:8081      |

### Credenciales por defecto (seed)

Tras la primera migración Flyway se crea un usuario administrador:

| Campo    | Valor              |
|----------|--------------------|
| Email    | `admin@example.com` |
| Password | `123456789`         |
| Rol      | `ADMIN`             |

> Cambia estas credenciales en entornos reales.

### Adminer — conexión a PostgreSQL

| Campo    | Valor          |
|----------|----------------|
| Sistema  | PostgreSQL     |
| Servidor | `postgres`     |
| Usuario  | `postgres`     |
| Contraseña | valor de `DB_PASSWORD` en `.env` |
| Base de datos | valor de `DB_NAME` en `.env` |

## Desarrollo local (sin Docker)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Variables requeridas: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.

### Frontend

```bash
cd frontend
npm install
npm start
```

La URL del API se configura en `frontend/src/environments/environment.ts`:

```typescript
apiUrl: 'http://localhost:8080/api/v1'
```

## Estructura del proyecto

```
usco/
├── backend/                 # API REST (Spring Boot)
│   └── src/main/java/com/usco/convocatoria/
│       ├── app/             # Módulos de negocio
│       │   ├── auth/
│       │   ├── categories/
│       │   ├── convocations/
│       │   ├── petitions/
│       │   └── reports/
│       ├── common/          # DTOs, mappers, respuestas comunes
│       ├── security/        # JWT, filtros, configuración
│       └── exception/       # Manejo global de errores
├── frontend/                # SPA Angular
│   └── src/app/
│       ├── core/            # Servicios, guards, modelos
│       ├── pages/           # Pantallas
│       ├── layout/          # Layout principal
│       └── shared/          # Componentes reutilizables
├── database/init/           # Scripts SQL de inicialización (Docker)
├── docs/                    # Documentación técnica
├── docker-compose.yml
└── .env.example
```

## Roles y permisos

| Rol      | Descripción                                      |
|----------|--------------------------------------------------|
| `ADMIN`  | Gestión completa de categorías y convocatorias  |
| `TEACHER`| Crear/editar convocatorias, ver reportes         |
| `STUDENT`| Inscribirse en convocatorias publicadas          |

## Módulos principales

- **Autenticación**: registro, login, perfil (`/me`), logout con blacklist de JWT.
- **Categorías**: CRUD (solo ADMIN).
- **Convocatorias**: ciclo de vida `BORRADOR → PUBLICADA → CERRADA`.
- **Peticiones**: inscripción de estudiantes; estados `PENDIENTE`, `APROBADA`, `RECHAZADA`.
- **Reportes**: estadísticas por categoría, convocatoria y estado de peticiones.

## Documentación técnica

Consulta la documentación detallada en:

- [Documentación técnica completa](docs/DOCUMENTACION_TECNICA.md)

Incluye: modelo de datos, endpoints REST, autenticación JWT, reglas de negocio, convenciones de código y guía de despliegue.

## Comandos útiles

```bash
# Levantar en segundo plano
docker compose up -d --build

# Ver logs del backend
docker compose logs -f backend

# Detener servicios
docker compose down

# Detener y eliminar volúmenes (resetea la BD)
docker compose down -v

# Compilar backend
cd backend && ./mvnw compile -DskipTests

# Compilar frontend
cd frontend && npm run build
```

## Licencia

Proyecto académico — Universidad del Surcolombiana (USCO).
