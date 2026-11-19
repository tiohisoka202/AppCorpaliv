# AppCorpaliv - Alba Lab

Aplicación móvil desarrollada en Android para apoyar la gestión de **CORPALIV (Alba Lab)**, permitiendo registrar beneficiarios, recibir solicitudes de alianza de empresas y consultar los servicios e iniciativas disponibles.

## ¿Qué hace la app?

* **Registro de Beneficiarios:** Formulario con validación de datos (nombre, RUT, correo, teléfono) y opción para adjuntar una foto desde la cámara o galería del teléfono.
* **Vinculación Empresarial:** Formulario para que empresas o instituciones puedan solicitar alianzas de inclusión laboral (RSE) o capacitaciones.
* **Catálogo de Servicios:** Pantalla que muestra las iniciativas, programas y servicios disponibles en tiempo real.
* **Respaldo sin conexión:** Si no hay conexión al servidor al registrar un beneficiario, los datos quedan guardados localmente en el teléfono.

## Tecnologías usadas

* **Lenguaje:** Kotlin
* **Interfaz:** Jetpack Compose
* **Arquitectura:** MVVM (ViewModel + StateFlow) y Navigation Compose
* **Conexión a backend:** Retrofit (conectado a los microservicios `ms-vinculacion` y `ms-contenidos`)
* **Base de datos local:** Room (SQLite)
* **Pruebas:** JUnit 4 y MockK

## Estructura del proyecto

* `navigation/`: Manejo de rutas entre pantallas (`Screen.kt`).
* `network/`: Configuración de Retrofit, modelos de datos y llamadas a los microservicios.
* `data/`: Base de datos local con Room (`AppDatabase`, entidades y DAOs).
* `viewmodel/`: Lógica de negocio y estados de los formularios.
* `ui/screens/`: Pantallas principales (`HomeScreen`, `FormularioScreen`, `ContactoEmpresaScreen`, `CatalogoScreen`).