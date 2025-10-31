# K-Dramas Manager

Aplicación Android que permite registrar, visualizar, editar y eliminar K-Dramas.  
Cada entrada incluye información como título, género, año de emisión, número de capítulos, calificación, estado de visionado y URL de imagen.  
Los datos se almacenan localmente mediante **SQLite** y se gestionan con **arquitectura MVVM**.


## Funcionalidades

- Formulario para agregar nuevos K-Dramas  
- Listado interactivo con RecyclerView  
- Edición y eliminación de K-Dramas existentes  
- Visualización de estado de visionado mediante Spinner  
- Calificación con RatingBar  
- Carga de imágenes desde URL  
- Persistencia local con SQLite  
- Navegación entre pantallas con Intents  
- Manejo de estados vacíos cuando no hay registros  


## Requisitos

- **Android Studio Narwhal 3** (versión 2025.1.3.7)  
- **Gradle:** 8.13.0  
- **SDK mínimo:** 26  
- **SDK objetivo:** 36  
- **Java:** 11  
- **Permiso de acceso a internet** habilitado en el dispositivo  

## Estructura del proyecto

### Actividades
- `MainActivity.java`: Formulario para agregar K-Dramas  
- `ListarKdramas.java`: Lista de K-Dramas registrados  
- `EditarKdrama.java`: Edición y eliminación de K-Dramas  
- `SplashActivity.java`: Pantalla de inicio  

### Componentes adicionales
- `KdramaViewModel.java`: Lógica de presentación  
- `KdramaRepository.java`: Acceso a datos  
- `DBHelper.java`: Gestión de base de datos SQLite  
- `KdramaAdapter.java`: Adaptador para RecyclerView  
- `Kdrama.java`: Modelo de datos  

### Layouts
- `activity_main.xml`: Formulario de registro  
- `activity_listar_kdramas.xml`: Lista de K-Dramas  
- `activity_editar_kdrama.xml`: Formulario de edición  
- `activity_splash.xml`: Pantalla de bienvenida  
- `item_kdrama.xml`: Tarjeta individual en la lista  

### Recursos
- `strings.xml`: Textos de la interfaz  
- `colors.xml`: Paleta de colores  
- `styles.xml`: Estilos visuales  
- `arrays.xml`: Estados de visionado  
- `drawable/`: Fondos, bordes e íconos  
- `AndroidManifest.xml`: Declaración de actividades y permisos  

---

## Instrucciones de ejecución

1. Clona el repositorio:
   git clone https://github.com/catherinepinilla13g/KDramasManager.git
2. Abre el proyecto en Android Studio Narwhal.
3. Compila y ejecuta la aplicación en un dispositivo Android con acceso a internet.
