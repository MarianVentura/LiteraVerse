# 📚 LiteraVerse

<div align="center">

![LiteraVerse Logo](https://img.shields.io/badge/LiteraVerse-Reading%20%26%20Writing%20Platform-blueviolet?style=for-the-badge)

**Plataforma móvil integral para descubrir, leer y crear historias digitales**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=flat-square&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-4285F4?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-UI-blue?style=flat-square)](https://m3.material.io/)
[![.NET](https://img.shields.io/badge/.NET-8.0-512BD4?style=flat-square&logo=dotnet)](https://dotnet.microsoft.com/)
[![Azure](https://img.shields.io/badge/Azure-Deployed-0089D6?style=flat-square&logo=microsoftazure)](https://azure.microsoft.com/)

[Características](#-características-principales) • [Arquitectura](#-arquitectura) • [Tecnologías](#-stack-tecnológico) • [API](#-api-backend) • [Instalación](#-instalación) • [Capturas](#-capturas-de-pantalla)

</div>

---

## 📖 Descripción General

**LiteraVerse** es una aplicación Android nativa que fusiona dos experiencias fundamentales del ecosistema literario digital: **leer y escribir**. Inspirada en plataformas como Wattpad, LiteraVerse ofrece un entorno moderno, limpio y centrado en la comodidad del usuario, donde lectores pueden descubrir historias nuevas escritas por autores independientes, y escritores pueden publicar, organizar y gestionar sus propias novelas de forma estructurada y profesional.

Este proyecto fue desarrollado como proyecto final para la asignatura **Programación Aplicada II**, demostrando dominio avanzado en desarrollo Android moderno, arquitectura limpia, integración de APIs RESTful y gestión de estado con patrones contemporáneos.

---

## ✨ Características Principales

### 📱 Para Lectores

#### 🏠 **Exploración y Descubrimiento**
- **Feed curado** con historias destacadas, populares y novedades recientes
- **Carruseles organizados** por categorías (Destacadas, Populares, Nuevas)
- **Navegación por géneros**: Romance, Fantasía, Ciencia Ficción, Misterio, Aventura, Drama, Horror, Comedia y más
- **Sistema de etiquetas** para descubrimiento personalizado

#### 🔍 **Búsqueda Avanzada**
- **Búsqueda en tiempo real** por título, autor o contenido
- **Filtros múltiples**:
  - Por género literario
  - Por estado (Publicado/Borrador)
  - Por criterios de ordenamiento (Relevancia, Popularidad, Recientes, Más leídos)
- **Resultados expandibles** con vista previa de sinopsis

#### 📖 **Experiencia de Lectura**
- **Lector optimizado** con diseño limpio y sin distracciones
- **Guardado automático de progreso** para continuar donde lo dejaste
- **Navegación fluida** entre capítulos con indicadores visuales
- **Detalle completo de historias** con portada, sinopsis, géneros, capítulos y estadísticas

#### 📚 **Biblioteca Personal**
Organización inteligente en tres categorías:
- **⭐ Favoritos**: Historias marcadas para lectura futura
- **📖 En progreso**: Lecturas activas con seguimiento de progreso
- **✅ Completadas**: Historias terminadas

Funcionalidades de la biblioteca:
- Acceso rápido a la última lectura
- Navegación directa al lector de capítulos
- Gestión visual de colecciones

### ✍️ Para Escritores

#### 🖊️ **Panel del Escritor**
Centro de control para autores con:
- **Mis Historias**: Vista completa de todas las obras
- **Crear Nueva Historia**: Asistente guiado de creación
- **Gestión de borradores** y publicaciones
- Acceso rápido a edición de capítulos

#### 📝 **Creación y Gestión de Historias**
- **Formulario completo** con campos estructurados:
  - Título de la historia
  - Portada (imagen o default)
  - Sinopsis detallada
  - Selección de géneros y etiquetas
  - Estado inicial (Borrador/Publicado)
- **Validación en tiempo real** de campos requeridos

#### 📑 **Estructura de Capítulos**
- **Vista de tabla de contenidos** para organización clara
- **Creación de nuevos capítulos** con título personalizado
- **Edición de capítulos existentes**
- **Gestión de estados**:
  - 📝 Borrador
  - 💾 Guardado
  - ✅ Publicado
- **Indicadores visuales** de estado y fecha de edición

#### 📄 **Editor de Capítulos**
- **Campo de texto amplio** optimizado para escritura
- **Guardado manual** con confirmación visual
- **Indicador de estado del borrador**
- **Navegación contextual** hacia otros capítulos

#### 🚀 **Publicación**
- **Control total** sobre visibilidad de contenido
- Historias en borrador permanecen privadas
- **Proceso de publicación simple** con un botón
- Posibilidad de actualizar capítulos publicados

### 🎨 **Experiencia de Usuario**

- **Interfaz moderna** siguiendo lineamientos de Material Design 3
- **Navegación intuitiva** con Bottom Navigation Bar
- **Tema claro/oscuro** para confort visual en cualquier ambiente
- **Animaciones fluidas** y transiciones naturales
- **Feedback visual** para todas las acciones del usuario
- **Estados de carga** claros con indicadores apropiados
- **Mensajes de error informativos** y orientados a soluciones

---

## 🏗 Arquitectura

LiteraVerse implementa **Clean Architecture** con patrón **MVI (Model-View-Intent)**, garantizando:

- ✅ Separación clara de responsabilidades
- ✅ Código testeable y mantenible
- ✅ Independencia de frameworks externos
- ✅ Escalabilidad y extensibilidad

### 📐 Capas de la Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  ┌───────────┐  ┌──────────┐  ┌─────────┐  ┌─────────┐ │
│  │  Screen   │→ │ViewModel │→ │ UiState │  │  Event  │ │
│  └───────────┘  └──────────┘  └─────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                         │
│  ┌──────────┐  ┌────────────┐  ┌──────────────────────┐│
│  │  Model   │  │  Use Cases │  │ Repository Interface ││
│  └──────────┘  └────────────┘  └──────────────────────┘│
└─────────────────────────────────────────────────────────┘
                          ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                      DATA LAYER                          │
│  ┌──────────────────┐  ┌──────────────────────────────┐ │
│  │  Repository Impl │  │  Data Sources                │ │
│  └──────────────────┘  └──────────────────────────────┘ │
│  ┌──────────────────┐  ┌──────────────────────────────┐ │
│  │  Local (Room)    │  │  Remote (Retrofit)           │ │
│  │  - DAO           │  │  - API Service               │ │
│  │  - Entities      │  │  - DTOs                      │ │
│  └──────────────────┘  └──────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 🔄 Patrón MVI (Model-View-Intent)

Cada pantalla sigue el patrón consistente:

```kotlin
// 1️⃣ Event: Acciones del usuario
sealed class HomeEvent {
    data object LoadFeaturedStories : HomeEvent()
    data class SelectGenre(val genre: String) : HomeEvent()
}

// 2️⃣ UiState: Estado de la UI
data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredStories: List<Story> = emptyList(),
    val userMessage: String? = null
)

// 3️⃣ ViewModel: Gestión de estado
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetFeaturedStoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    
    fun onEvent(event: HomeEvent) { /* ... */ }
}

// 4️⃣ Screen: Composición UI
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    // UI based on state
}
```

### 📁 Estructura del Proyecto

```
app/src/main/java/edu/ucne/literaverse/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAO interfaces
│   │   ├── entities/         # Entidades de base de datos
│   │   └── database/         # Configuración de Room
│   ├── remote/
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── api/              # Retrofit API interfaces
│   │   ├── Resource.kt       # Wrapper para estados de red
│   │   └── RemoteDataSource.kt
│   ├── mappers/              # Conversión entre capas
│   └── repository/           # Implementaciones de repositorios
│
├── domain/
│   ├── model/                # Modelos de negocio
│   ├── repository/           # Interfaces de repositorios
│   └── usecase/              # Casos de uso
│
├── presentation/
│   ├── login/                # Autenticación
│   ├── register/             # Registro de usuarios
│   ├── home/                 # Pantalla principal
│   ├── search/               # Búsqueda y filtros
│   ├── library/              # Biblioteca personal
│   ├── storydetailreader/    # Detalle de historia (lector)
│   ├── chapterreader/        # Lector de capítulos
│   ├── write/                # Panel del escritor
│   ├── mystories/            # Mis historias
│   ├── createstory/          # Crear historia
│   ├── storychapters/        # Gestión de capítulos
│   ├── chaptereditor/        # Editor de capítulos
│   ├── navigation/           # Navegación de la app
│   └── ui/theme/             # Tema Material 3
│
└── di/                       # Inyección de dependencias (Hilt)
```

---

## 🛠 Stack Tecnológico

### 📱 Android (Cliente)

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Kotlin** | 1.9.0 | Lenguaje principal |
| **Jetpack Compose** | 1.5.4 | UI declarativa moderna |
| **Material 3** | Latest | Sistema de diseño |
| **Hilt** | 2.48 | Inyección de dependencias |
| **Room** | 2.6.0 | Base de datos local |
| **Retrofit** | 2.9.0 | Cliente HTTP |
| **StateFlow** | - | Gestión de estado reactivo |
| **Navigation Compose** | 2.7.5 | Navegación declarativa |
| **Coil** | 2.5.0 | Carga de imágenes |

### 🌐 Backend (.NET)

| Tecnología | Propósito |
|-----------|-----------|
| **.NET 8.0** | Framework backend |
| **ASP.NET Core** | Web API |
| **Entity Framework Core** | ORM |
| **SQLite** | Base de datos |
| **JWT Authentication** | Autenticación segura |
| **Azure App Service** | Hosting en la nube |
| **Swagger/OpenAPI** | Documentación de API |

### 🔐 Seguridad

- **JWT (JSON Web Tokens)** para autenticación
- **Password hashing** con algoritmos seguros
- **Session management** con tokens
- **Validación de tokens** en tiempo real
- **HTTPS** para comunicación segura

---

## 🌐 API Backend

### 🔗 Base URL
```
https://literaverseapi-emg2d4e8eybve7fd.centralus-01.azurewebsites.net/
```

### 📋 Endpoints Principales

#### 🔐 **Autenticación**

```http
POST /api/Auth/Login
Content-Type: application/json

{
  "userName": "usuario",
  "password": "contraseña"
}

Response: {
  "usuarioId": 1,
  "userName": "usuario",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "loginDate": "2024-11-24T10:30:00Z"
}
```

```http
POST /api/Auth/Register
Content-Type: application/json

{
  "userName": "nuevoUsuario",
  "password": "contraseña123"
}
```

```http
POST /api/Auth/ValidateToken
Content-Type: application/json

"token_aqui"

Response: {
  "isValid": true,
  "userId": 1,
  "userName": "usuario"
}
```

```http
POST /api/Auth/Logout
Content-Type: application/json

"token_aqui"
```

#### 🏠 **Exploración**

```http
GET /api/Explore/featured
Authorization: Bearer {token}
```

```http
GET /api/Explore/popular
Authorization: Bearer {token}
```

```http
GET /api/Explore/new
Authorization: Bearer {token}
```

```http
GET /api/Explore/genre/{genreName}
Authorization: Bearer {token}
```

#### 🔍 **Búsqueda**

```http
GET /api/Search?query={text}&genre={genre}&status={status}
Authorization: Bearer {token}
```

#### 📚 **Historias**

```http
GET /api/Stories
GET /api/Stories/{id}
POST /api/Stories
PUT /api/Stories/{id}
DELETE /api/Stories/{id}
```

#### 📑 **Capítulos**

```http
GET /api/Chapters/story/{storyId}
GET /api/Chapters/{id}
POST /api/Chapters
PUT /api/Chapters/{id}
DELETE /api/Chapters/{id}
```

#### 📖 **Biblioteca**

```http
GET /api/Library/favorites
POST /api/Library/favorites/{storyId}
DELETE /api/Library/favorites/{storyId}

GET /api/Library/reading
POST /api/Library/reading/{storyId}

GET /api/Library/completed
```

#### 📊 **Progreso de Lectura**

```http
GET /api/ReadingProgress/story/{storyId}
POST /api/ReadingProgress
PUT /api/ReadingProgress
```

#### 🏷️ **Géneros**

```http
GET /api/Genres
```

### 🔒 Autenticación de Endpoints

Todos los endpoints (excepto Login y Register) requieren autenticación mediante JWT Bearer Token:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📦 Instalación

### Prerrequisitos

- Android Studio Otter (2025.2.1) o superior
- JDK 17 o superior
- Android SDK 34
- Dispositivo/Emulador con Android 8.0 (API 26) o superior

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/MarianVentura/LiteraVerse.git
cd literaverse
```

2. **Abrir el proyecto en Android Studio**
```bash
# Abrir Android Studio y seleccionar "Open" → Navegar a la carpeta del proyecto
```

3. **Sincronizar dependencias de Gradle**
```bash
# Android Studio sincronizará automáticamente
# O manualmente: File → Sync Project with Gradle Files
```

4. **Configurar la API**

El proyecto ya está configurado con la API en producción. Si deseas usar una instancia local:

```kotlin
// En: app/src/main/java/edu/ucne/literaverse/di/AppModule.kt
private const val BASE_URL = "https://tu-api-local.com/"
```

5. **Ejecutar la aplicación**
```bash
# Seleccionar dispositivo/emulador y presionar Run (▶️)
# O usar: ./gradlew installDebug
```

### 🔧 Configuración del Backend (Opcional)

Si deseas ejecutar el backend localmente:

1. **Instalar .NET 8 SDK**
```bash
# Descargar desde: https://dotnet.microsoft.com/download
```

2. **Configurar la base de datos**
```bash
cd LiteraVerseAPI
dotnet ef database update
```

3. **Ejecutar el servidor**
```bash
dotnet run
```

4. **Acceder a Swagger UI**
```
http://localhost:5036/swagger
```

---

## 📸 Capturas de Pantalla

### 🏠 Pantallas de Lectura

<div align="center">

| Exploración | Detalle de Historia | Lector de Capítulos |
|-------------|---------------------|---------------------|
| ![Home](screenshots/home.png) | ![Detail](screenshots/story_detail.png) | ![Reader](screenshots/chapter_reader.png) |

| Búsqueda | Biblioteca | Filtros |
|----------|-----------|---------|
| ![Search](screenshots/search.png) | ![Library](screenshots/library.png) | ![Filters](screenshots/filters.png) |

</div>

### ✍️ Pantallas de Escritura

<div align="center">

| Panel Escritor | Crear Historia | Editor de Capítulos |
|----------------|----------------|---------------------|
| ![Writer Panel](screenshots/writer_panel.png) | ![Create Story](screenshots/create_story.png) | ![Chapter Editor](screenshots/chapter_editor.png) |

| Mis Historias | Gestión de Capítulos |
|---------------|---------------------|
| ![My Stories](screenshots/my_stories.png) | ![Manage Chapters](screenshots/story_chapters.png) |

</div>

---

## 🎯 Funcionalidades Implementadas

### ✅ Completadas

#### Autenticación y Usuarios
- [x] Sistema completo de Login con JWT
- [x] Registro de usuarios con validación de duplicados
- [x] Validación de tokens en tiempo real
- [x] Logout con invalidación de sesión
- [x] Gestión de sesiones múltiples

#### Exploración y Descubrimiento
- [x] Pantalla Home con carruseles
- [x] Historias destacadas
- [x] Historias populares
- [x] Novedades recientes
- [x] Navegación por géneros
- [x] Sistema de etiquetas

#### Búsqueda
- [x] Búsqueda en tiempo real
- [x] Filtros por género
- [x] Filtros por estado
- [x] Ordenamiento múltiple
- [x] Resultados expandibles

#### Lectura
- [x] Detalle completo de historias
- [x] Lector de capítulos optimizado
- [x] Guardado automático de progreso
- [x] Navegación entre capítulos
- [x] Indicadores visuales de progreso

#### Biblioteca Personal
- [x] Colección de Favoritos
- [x] Lecturas en progreso
- [x] Historias completadas
- [x] Gestión de colecciones
- [x] Acceso rápido a última lectura

#### Modo Escritor
- [x] Panel del escritor
- [x] Crear nuevas historias
- [x] Gestión de borradores
- [x] Lista de mis historias
- [x] Crear capítulos
- [x] Editor de capítulos
- [x] Gestión de estados (Borrador/Guardado/Publicado)
- [x] Publicación de historias
- [x] Actualización de contenido

#### UI/UX
- [x] Material Design 3
- [x] Navegación con Bottom Nav
- [x] Estados de carga
- [x] Manejo de errores
- [x] Animaciones y transiciones
- [x] Diseño responsive

### 🚧 Futuras Mejoras

- [ ] Modo oscuro completo
- [ ] Comentarios en capítulos
- [ ] Sistema de valoraciones
- [ ] Notificaciones push
- [ ] Lectura offline
- [ ] Sincronización en segundo plano con WorkManager
- [ ] Compartir historias
- [ ] Perfil de usuario personalizable
- [ ] Estadísticas para autores
- [ ] Recomendaciones personalizadas con ML

---

## 🧪 Testing

El proyecto incluye tests unitarios de la capa de datos:

### Unit Tests
```bash
./gradlew test
```

### Cobertura de Tests
- Repositories: ✅ Implementados
- ViewModels: ❌ No implementados
- UI Components: ❌ No implementados

---

## 🤝 Contribución

Este es un proyecto académico, pero las sugerencias son bienvenidas:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto fue desarrollado como proyecto final universitario para **Programación Aplicada II**.

© 2024 LiteraVerse - Universidad Católica Nordestana (UCNE)

---

## 👨‍💻 Autores

**Marianela Ventura**
- GitHub: [@MarianVentura](https://github.com/MarianVentura)
- Universidad: Universidad Católica Nordestana (UCNE)
- Curso: Programación Aplicada II

**Francis Castillo**
- GitHub: [@Francis-C1pher](https://github.com/Francis-C1pher)
- Universidad: Universidad Católica Nordestana (UCNE)
- Curso: Programación Aplicada II

**Proyecto**: LiteraVerse - Plataforma de Lectura y Escritura

---

## 📊 Estadísticas del Proyecto

- **Líneas de código**: ~15,000+
- **Pantallas implementadas**: 15+
- **Endpoints API**: 40+
- **Tiempo de desarrollo**: 2 semanas
- **Arquitectura**: Clean Architecture + MVI
- **Cobertura de tests**: Repositorios (Unit Tests)

---

<div align="center">

**Hecho con ❤️ y ☕ para Programación Aplicada II**

[![GitHub App](https://img.shields.io/badge/GitHub-App%20Repository-181717?style=flat-square&logo=github)](https://github.com/MarianVentura/LiteraVerse)
[![GitHub API](https://img.shields.io/badge/GitHub-API%20Repository-181717?style=flat-square&logo=github)](https://github.com/MarianVentura/LiteraVerseAPI)
[![API](https://img.shields.io/badge/API-Azure-0089D6?style=flat-square&logo=microsoftazure)](https://literaverseapi-emg2d4e8eybve7fd.centralus-01.azurewebsites.net/swagger)

</div>
