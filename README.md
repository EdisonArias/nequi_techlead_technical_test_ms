# nequi-techlead-technical-test-ms

[![JDK](https://img.shields.io/badge/JDK-21-blue?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)[![Gradle](https://img.shields.io/badge/Gradle-8.5-green?logo=gradle&logoColor=white)](https://gradle.org/)[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)[![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-3.26.1-blueviolet)](https://github.com/clean-architecture-java)[![Lombok](https://img.shields.io/badge/Lombok-1.18.38-pink?logo=lombok&logoColor=white)](https://projectlombok.org/)[![Mapstruct](https://img.shields.io/badge/Mapstruct-1.5.2.Final-orange)](https://mapstruct.org/)[![Logstash Logback](https://img.shields.io/badge/Logstash%20Logback-8.0-blue?logo=elasticstack&logoColor=white)](https://github.com/logstash/logstash-logback-encoder)[![Sonar](https://img.shields.io/badge/Sonar-6.3.1.5724-blue?logo=sonarqube&logoColor=white)](https://www.sonarsource.com/)[![Jacoco](https://img.shields.io/badge/Jacoco-0.8.13-yellow?logo=jacoco&logoColor=white)](https://www.jacoco.org/jacoco/)

Microservicio de gestión de **Franquicias (Franchises)**, **Sucursales (Branches)** y **Productos (Products)**, 
construido con **Spring Boot WebFlux** y **Clean Architecture**.  
En nube se despliega en **AWS ECS Fargate** (ALB + ECR + CloudWatch Logs + Secrets Manager) con **MongoDB Atlas**.
En desarrollo usa **Mongo local**.

---

## Índice

- [1. Arquitectura](#1-arquitectura)
    - [1.1 Arquitectura de Infraestructura AWS](#11-arquitectura-de-infraestructura-aws)
    - [1.2 Arquitectura de Aplicación (Clean Architecture)](#12-arquitectura-de-aplicación-clean-architecture)
- [2. Estructura](#2-estructura)
- [3. Requisitos](#3-requisitos)
- [4. Instalación](#4-instalación)
- [5. Ejecución local (Docker Compose)](#5-ejecución-local-docker-compose)
- [6. Ejecución local (sin Docker)](#6-ejecución-local-sin-docker)
- [7. Endpoints](#7-endpoints)
- [8. Pruebas unitarias](#8-pruebas-unitarias)
- [9. Salud y observabilidad](#9-salud-y-observabilidad)
- [10. Tecnologías utilizadas)](#10-tecnologías-utilizadas)
- [11. Autor](#11-autor)

---

## 1. Arquitectura

### 1.1 Arquitectura de Infraestructura AWS

Arquitectura desplegada en AWS ECS Fargate con MongoDB Atlas. Pipeline de CI/CD automatizado mediante 
GitHub Actions y Terraform para infraestructura como código. Incluye Application Load Balancer para distribución de tráfico, 
Amazon ECR para registro de contenedores, AWS Secrets Manager para gestión segura de credenciales y CloudWatch para monitoreo y 
observabilidad. La solución garantiza alta disponibilidad, escalabilidad automática y despliegues seguros.

![Diagrama de Arquitectura AWS](Diagrama_Arquitectura.png)

### 1.2 Arquitectura de Aplicación (Clean Architecture)

Se implementó bajo el enfoque de Clean Architecture, utilizando el plugin [Scaffold Clean Architecture](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro) de Bancolombia (Open Source), el cual permite estructurar el código de forma clara y mantenible, separando responsabilidades en capas bien definidas.

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

---

## 2. Estructura

## 📦 Estructura del proyecto
```
📦nequi_techlead_technical_test_ms
┣ 📂applications
┃ ┗ 📂app-service
┃ ┃ ┣ 📂src
┃ ┃ ┃ ┣ 📂main|
┃ ┃ ┃ ┃ ┣ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜[configs and beans]
┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MainApplication.java
┃ ┃ ┃ ┃ ┗ 📂resources
┃ ┃ ┃ ┃ ┃ ┣ 📜[properties]
┃ ┃ ┃ ┗ 📂test
┃ ┃ ┃ ┃ ┗ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┗ 📜build.gradle
┣ 📂deployment
┃ ┣ 📜[Dockerfile, Pipelines as a code]
┣ 📂domain
┃ ┣ 📂model
┃ ┃ ┣ 📂src
┃ ┃ ┃ ┣ 📂main
┃ ┃ ┃ ┃ ┗ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┃ ┗ 📂test
┃ ┃ ┃ ┃ ┗ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┗ 📜build.gradle
┃ ┗ 📂usecase
┃ ┃ ┣ 📂src
┃ ┃ ┃ ┣ 📂main
┃ ┃ ┃ ┃ ┗ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┃ ┗ 📂test
┃ ┃ ┃ ┃ ┗ 📂java
┃ ┃ ┃ ┃ ┃ ┗ 📂co.com.nequi.teachlead.technical.test
┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
┃ ┃ ┗ 📜build.gradle
┣ 📂infrastructure
┃ ┣ 📂driven-adapters
┃ ┃ ┃ ┣ 📂mongo-reposiotory
┃ ┣ 📂entry-points
┃ ┃ ┃ ┣ 📂reactive-web
┣ 📜.gitignore
┣ 📜build.gradle
┣ 📜gradle.properties
┣ 📜lombok.config
┣ 📜main.gradle
┣ 📜README.md
┗ 📜settings.gradle
```

---

## 3. Requisitos

- **JDK 21**
- **Gradle wrapper** (`./gradlew`)
- **Docker** (opcional para Compose)
- Puertos: **8080** (app) y **27017** (Mongo)

---

## 4. Instalación

### Clonar el repositorio
```bash
git clone https://github.com/EdisonArias/nequi_techlead_technical_test_ms.git
cd nequi_techlead_technical_test_ms
```

La app detecta perfil y URI de Mongo vía env vars:

- `SPRING_DATA_MONGODB_URI`
    - Local (host):  
      `mongodb://admin:SuperSecreta123@localhost:27017/technicalTest?authSource=admin&directConnection=true&serverSelectionTimeoutMS=2000`
    - Compose (host = contenedor):  
      `mongodb://admin:SuperSecreta123@mongo:27017/technicalTest?authSource=admin&directConnection=true&serverSelectionTimeoutMS=2000`
    - En **ECS Fargate**, inyectada desde **AWS Secrets Manager**.

---

## 5. Ejecución local (Docker Compose)

1. **Build JAR**

```bash
./gradlew clean test bootJar
```

El Dockerfile espera: `applications/app-service/build/libs/nequi_techlead_technical_test_ms.jar`

2. **docker-compose.yml** (raíz):

```yaml
services:
  mongo:
    image: mongo:7
    container_name: mongodb
    ports: ["27017:27017"]
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: SuperSecreta123
    volumes: ["mongo_data:/data/db"]

  app:
    build:
      context: .
      dockerfile: deployment/Dockerfile
      args:
        JAR_PATH: applications/app-service/build/libs/nequi_techlead_technical_test_ms.jar
    image: nequi-techlead-local-app:latest
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: local
      SPRING_DATA_MONGODB_URI: "mongodb://admin:SuperSecreta123@mongo:27017/technicalTest?authSource=admin&directConnection=true&serverSelectionTimeoutMS=2000"
    depends_on: [mongo]

volumes:
  mongo_data:
```

3. **Levantar**

```bash
docker compose up -d --build
```

4. **Verificar**

- Health: <http://localhost:8080/actuator/health>
- Logs: `docker compose logs -f app`

Apagar: `docker compose down`

---

## 6. Ejecución local (sin Docker)

Levanta Mongo en contenedor y corre la app local:

```bash
docker run -d --name mongo -p 27017:27017   -e MONGO_INITDB_ROOT_USERNAME=admin   -e MONGO_INITDB_ROOT_PASSWORD=SuperSecreta123   mongo:7

./gradlew clean test bootRun
```

> Alternativa: definir `spring.data.mongodb.uri` en `application-local.yml`.

---

## 7. Endpoints

### Parámetros de Path

Los siguientes identificadores son utilizados en las rutas de la API:

- **`franchiseId`** → Identificador único de la franquicia generado por MongoDB
- **`branchId`** → Identificador único de la sucursal generado por MongoDB
- **`productId`** → Identificador único del producto generado por MongoDB

> **Nota:** Para consultar estos IDs, puede utilizar el método GET de cada funcionalidad donde puede validar el identificador de cada registro.

---

### Franchises (Franquicias)

| Método | Path                               | Parámetros    | Body                | Descripción                    |
|:------:|:-----------------------------------|:--------------|:--------------------|:-------------------------------|
| `GET`  | `/api/v1/franchises`               | —             | —                   | Lista todas las franquicias    |
| `POST` | `/api/v1/franchises`               | —             | `{ "name": "..." }` | Crea una nueva franquicia      |
| `PUT`  | `/api/v1/franchises/{franchiseId}` | `franchiseId` | `{ "name": "..." }` | Actualiza nombre de franquicia |

**Ejemplo de uso:**
```bash
# Obtener todas las franquicias
GET /api/v1/franchises

# Crear franquicia
POST /api/v1/franchises
{
  "name": "Franquicia McDonald's"
}

# Actualizar franquicia
PUT /api/v1/franchises/507f1f77bcf86cd799439011
{
  "name": "Franquicia McDonald's Actualizada"
}
```

### Branches (Sucursales)

| Método | Path                                        | Params                | Body                | Descripción                        |
| -----: | ------------------------------------------- | --------------------- |---------------------|------------------------------------|
|    GET | `/api/franchises/{franchiseId}/branches`            | `franchiseId`             |                     | Lista sucursales de una franquicia |
|   POST | `/api/franchises/{franchiseId}/branches`            | `franchiseId`             | `{ "name": "..." }` | Crea sucursal                      |
|    PUT | `/api/franchises/{franchiseId}/branches/{branchId}` | `franchiseId`, `branchId` | `{ "name": "..." }` | Actualiza nombre de sucursal       |

```bash
# Obtener sucursales de una franquicia
GET /api/franchises/507f1f77bcf86cd799439011/branches

# Crear sucursal
POST /api/franchises/507f1f77bcf86cd799439011/branches
{
  "name": "Sucursal Centro Comercial"
}

# Actualizar sucursal
PUT /api/franchises/507f1f77bcf86cd799439011/branches/507f1f77bcf86cd799439012
{
  "name": "Sucursal Centro Comercial Norte"
}
```

### Products (Productos)

| Método | Path                                                             | Params      | Body                             | Descripción                             |
| -----: |------------------------------------------------------------------|-------------|----------------------------------|-----------------------------------------|
|    GET | `/api/v1/franchises/branches/products`                           | _           | —                                | Lista de productos                      |
|   POST | `/api/v1/franchises/branches/{branchId}/products`                | `branchId`  | `{ "name": "...", "stock": 10 }` | Crea producto en sucursal               |
|    PUT | `/api/v1/franchises/branches/products/{productId}/name`          | `productId` | `{ "name": "..." }`              | Actualiza nombre de producto            |
|    PUT | `/api/v1/franchises/branches/products/{productId}/stock`         | `productId` | `{ "stock": "..." }`             | Actualiza stock de producto             |
| DELETE | `/api/v1/franchises/branches/products/{productId}`               | `productId` | —                                | Elimina producto                        |
|    GET | `/api/v1/franchises/{franchiseId}/branches/top-products-by-site` | `franchiseId` | —                                | Top producto (mayor stock) por sucursal |

```bash
# Obtener productos
GET /api/v1/franchises/branches/products

# Crear producto
POST /api/v1/franchises/branches/707f1f77bcf86cd799439012/products
{
  "name": "Big Mac",
  "stock": 50
}

# Actualizar nombre de producto
PUT /api/v1/franchises/branches/products/507f1f77bcf86cd799439012/name
{
  "name": "Big Mac Deluxe",
}

# Actualizar stock de producto
PUT /api/v1/franchises/branches/products/507f1f77bcf86cd799439012/stock
{
  "stock": 77,
}

# Eliminar producto
DELETE /api/v1/franchises/branches/products/507f1f77bcf86cd799439012

# Obtener top productos por sucursal de un franquicia
GET /api/v1/franchises/507f1f77bcf86cd799439012/branches/topProducts
```

---

## 8. Pruebas Unitarias

El proyecto incluye una suite completa de pruebas unitarias que cubren todas las capas de la arquitectura, garantizando la calidad y confiabilidad del código.

### Cobertura de Pruebas

#### Casos de Uso (Use Cases)

**Branch**
- `CreateBranchUseCase` - Validación de creación de sucursales
- `GetBranchesUseCase` - Obtención y filtrado de sucursales
- `UpdateBranchUseCase` - Actualización de datos de sucursales

**Franchise**
- `CreateFranchiseUseCase` - Creación y validación de franquicias
- `GetFranchisesUseCase` - Consulta de franquicias existentes
- `UpdateFranchiseUseCase` - Modificación de información de franquicias

**Product**
- `CreateProductUseCase` - Registro de nuevos productos
- `DeleteProductUseCase` - Eliminación segura de productos
- `GetProductsUseCase` - Consulta de catálogo de productos
- `GetTopProductsByBranchUseCase` - Productos más vendidos por sucursal
- `UpdateProductNameUseCase` - Modificación de nombres de productos
- `UpdateProductStockUseCase` - Gestión de inventario

#### Repositorios MongoDB

**Branch Repository**
- `SyncBranchManager` - Sincronización de datos de sucursales
- `BranchMongoAdapter` - Adaptador de persistencia para sucursales
- `AddProductToBranchService` - Servicio de asignación de productos
- `MongoConfig` - Configuración de conexión MongoDB

**Franchise Repository**
- `BranchToProductMapper` - Mapeo de relaciones sucursal-producto
- `FranchiseMongoAdapter` - Persistencia de datos de franquicias
- `AddBranchesToFranchiseService` - Gestión de sucursales en franquicias
- `TopProductByBranchMongoService` - Análisis de productos top por sucursal

**Product Repository**
- `SyncDeleteManager` - Gestión de eliminaciones sincronizadas
- `SyncProductStockManager` - Sincronización de inventario
- `ProductMongoAdapter` - Adaptador de persistencia para productos

#### 🎮 Controladores (Controllers)

**Branch Controllers**
- `CreateBranchController` - Endpoint de creación de sucursales
- `GetBranchesController` - Endpoint de consulta de sucursales
- `UpdateBranchController` - Endpoint de actualización de sucursales

**Franchise Controllers**
- `CreateFranchiseController` - Endpoint de creación de franquicias
- `GetFranchisesController` - Endpoint de consulta de franquicias
- `UpdateFranchisesController` - Endpoint de actualización de franquicias

**Product Controllers**
- `ProductMapper` - Mapeo de DTOs de productos
- `CreateProductController` - Endpoint de registro de productos
- `DeleteProductController` - Endpoint de eliminación de productos
- `GetProductsController` - Endpoint de consulta de productos
- `GetTopProductsInBranchController` - Endpoint de productos destacados

#### 🔧 Handlers y Enrutadores

**Request Handlers**
- `BranchHandler` - Manejo de peticiones de sucursales
- `FranchiseHandler` - Manejo de peticiones de franquicias
- `ProductHandler` - Manejo de peticiones de productos

**API Routers**
- `BranchRouter` - Enrutamiento para endpoints de sucursales
- `FranchiseRouter` - Enrutamiento para endpoints de franquicias
- `ProductRouter` - Enrutamiento para endpoints de productos

#### 🛠️ Utilidades y Validaciones

**Core Utils**
- `Response` - Formateo estandarizado de respuestas
- `ValidateRequest` - Validación de entrada de requests
- `GlobalExceptionHandler` - Manejo centralizado de excepciones

### Ejecución de Pruebas

Para ejecutar toda la suite de pruebas:
```bash
./gradlew test
```

### Reporte de Cobertura
Generar reporte de cobertura de código:

```bash
./gradlew jacocoTestReport
```

El reporte estará disponible en: build/reports/jacoco/test/html/index.html

## 9. Salud y observabilidad

- Health: `GET /actuator/health`
- CORS: configurable vía `cors.allowed-origins` en `application.yml`

---

## 10. Tecnologías utilizadas

* **Java 21** - Plataforma de desarrollo principal
* **Spring Boot 3.3.11** - Framework de aplicación con WebFlux para programación reactiva
* **Spring WebFlux** - Stack reactivo no bloqueante
* **MongoDB** - Base de datos NoSQL para persistencia
* **Reactor** - Biblioteca de programación reactiva
* **JUnit 5 + Mockito** - Framework de testing y mocking
* **Hibernate Validator** - Validación de datos
* **Docker** - Containerización de la aplicación
* **AWS ECS Fargate** - Plataforma de despliegue en contenedores
* **AWS ECR** - Registro de contenedores Docker
* **AWS Secrets Manager** - Gestión segura de credenciales
* **MongoDB Atlas** - Base de datos MongoDB en la nube
* **Terraform** - Infraestructura como código (IaC)
* **GitHub Actions** - Pipeline de CI/CD
* **Clean Architecture** - Patrón arquitectónico
* **Gradle** - Herramienta de construcción y gestión de dependencias

### Datos importantes

* La arquitectura sigue principios de **Clean Architecture** con separación clara de responsabilidades en capas
* El sistema es **completamente reactivo** utilizando Spring WebFlux
* Implementa **validación centralizada** que mejora el manejo de errores y respuestas consistentes al cliente
* El microservicio se despliega automáticamente en **AWS ECS Fargate** mediante pipeline de **GitHub Actions** con **Terraform** para infraestructura como código
* Utiliza **MongoDB** tanto local (desarrollo) como **MongoDB Atlas** (producción) para persistencia de datos
* Incluye **observabilidad completa** con health checks, métricas y logs estructurados

---

## 11. Autor
Edison Ferney Arias Plazas <br>
Prueba técnica Nequi Tech Lead
