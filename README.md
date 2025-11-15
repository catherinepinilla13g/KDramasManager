# K-Dramas Manager (rama KDramasManagerNew)

Aplicación Android que permite registrar, visualizar, editar y eliminar K-Dramas.  
Cada entrada incluye información como título, género, año de emisión, número de capítulos, calificación, estado de visionado y URL de imagen.  
Los datos se almacenan localmente mediante **SQLite** y se gestionan con **arquitectura MVVM**.  
En esta rama (`KDramasManagerNew`), además, los datos se sincronizan con **Firebase Firestore** cuando hay conexión a internet.

## Funcionalidades

- Formulario para agregar nuevos K-Dramas  
- Listado interactivo con RecyclerView  
- Edición y eliminación de K-Dramas existentes  
- Visualización de estado de visionado mediante Spinner  
- Calificación con RatingBar  
- Carga de imágenes desde URL  
- Persistencia local con SQLite  
- **Sincronización en la nube con Firebase Firestore**  
- **Verificación de conectividad con `NetworkUtils`**  
- Navegación entre pantallas con Intents  
- Manejo de estados vacíos cuando no hay registros  

## Requisitos

- **Android Studio Narwhal 3** (versión 2025.1.3.7)  
- **Gradle:** 8.13.0  
- **SDK mínimo:** 26  
- **SDK objetivo:** 36  
- **Java:** 11  
- **Permiso de acceso a internet** habilitado en el dispositivo  
- Proyecto configurado en Firebase con **Firestore Database habilitado**  
- Archivo `google-services.json` en la carpeta `app/`  

## Estructura del proyecto

### Actividades
- `MainActivity.java`: Formulario para agregar K-Dramas  
- `ListarKdramas.java`: Lista de K-Dramas registrados  
- `EditarKdrama.java`: Edición y eliminación de K-Dramas  
- `SplashActivity.java`: Pantalla de inicio  

### Componentes adicionales
- `KdramaViewModel.java`: Lógica de presentación  
- `KdramaRepository.java`: Acceso a datos (SQLite + Firebase Firestore)  
- `DBHelper.java`: Gestión de base de datos SQLite  
- `KdramaAdapter.java`: Adaptador para RecyclerView  
- `Kdrama.java`: Modelo de datos  
- `NetworkUtils.java`: Utilidad para verificar conexión a internet  

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

### Dependencias adicionales utilizadas
La aplicación utiliza las siguientes bibliotecas gestionadas mediante Gradle:

- `androidx.recyclerview:recyclerview`  
- `androidx.cardview:cardview`  
- `androidx.lifecycle:lifecycle-viewmodel-ktx`  
- `androidx.lifecycle:lifecycle-livedata-ktx`  
- `androidx.lifecycle:lifecycle-common-java8`  
- `com.github.bumptech.glide:glide`  
- `com.google.firebase:firebase-firestore`  
- `com.google.firebase:firebase-database`  

## Instrucciones de ejecución

```bash
1. Clona el repositorio:
   git clone https://github.com/catherinepinilla13g/KDramasManager.git

2. Cambia a la rama KDramasManagerNew:
   git checkout KDramasManagerNew

3. Abre el proyecto en Android Studio Narwhal.

4. Coloca el archivo google-services.json en la carpeta app/.

5. Compila y ejecuta la aplicación en un dispositivo Android con acceso a internet.

6. Verifica los datos en la consola de Firebase → Firestore Database.

