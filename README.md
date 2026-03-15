# Metrics Servers API

Backend REST para la monitorización de servidores y servicios.  
La aplicación permite gestionar servidores, consultar métricas del sistema y de servicios, y administrar usuarios, permisos y tokens de acceso.

Este proyecto forma parte de un sistema de monitorización desarrollado como backend para la recopilación y consulta de métricas de infraestructura.

---

# Características

- API REST desarrollada en **Java**
- Documentación automática mediante **OpenAPI / Swagger**
- Monitorización de métricas de sistema
- Monitorización de servicios
- Gestión de servidores
- Gestión de usuarios y permisos
- Autenticación mediante **API Tokens**
- Integración con **MariaDB**
- Integración con **MongoDB**
- Integración con **MinIO**
- Consulta de métricas mediante **SSH**

---

# Arquitectura

El proyecto sigue una arquitectura por capas:

```
api
├ resources → endpoints REST
└ services → lógica de negocio

dao → acceso a datos

models → entidades del sistema

exceptions → gestión de errores

utils → utilidades y helpers
```

Flujo típico de una petición:
```
Cliente
↓
REST API (resources)
↓
Services
↓
DAO
↓
Base de datos / Servicios externos
```


---

# Funcionalidades principales

## Gestión de servidores

Permite registrar servidores y consultar su estado.

## Monitorización

Obtención de métricas como:

- CPU
- Memoria
- Disco
- Red

También se obtienen métricas de servicios:

- Apache
- MariaDB
- SSH

## Gestión de usuarios

El sistema permite:

- Crear usuarios
- Asignar permisos
- Gestionar secciones y ámbitos

## Autenticación

La API utiliza **tokens de acceso** para autenticar las peticiones.

# Manejo de errores

La API implementa un sistema de excepciones personalizado que devuelve errores estructurados.

Ejemplo de respuesta:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Datos inválidos",
  "details": null
}
```
---

# Documentación de la API

La documentación interactiva de la API está disponible mediante Swagger UI.

Una vez desplegada la aplicación:

```
http://<IP_HOST>:8080/metrics-servers/docs
```

Desde Swagger UI es posible:

- Consultar endpoints
- Ver esquemas de datos
- Probar peticiones directamente

---

# Documentación del código

La documentación Javadoc del proyecto se encuentra en:

```
docs/javadoc/index.html
```
