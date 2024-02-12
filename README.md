# Wayra: Venta de Pasajes Interprovinciales sin Conexión a Internet 🚌
## 📝 Descripción 
Wayra es una aplicación móvil desarrollada en `Android` con `Java` que se enfoca en la venta de pasajes para transportes interprovinciales en rutas donde no hay cobertura de internet. Aquí están los aspectos clave de la aplicación:

- 📵 **Venta Offline**: Wayra permite a los vendedores de pasajes operar en áreas remotas sin acceso a internet. Los vendedores pueden registrar ventas localmente en sus dispositivos incluso cuando no hay conexión. 
- 🌐 **Sincronización Remota**: Cuando el vendedor verifica que tiene acceso a internet (durante la ruta o en la terminal), puede sincronizar las ventas registradas localmente con una base de datos remota. Esto garantiza que los datos estén actualizados y disponibles para su procesamiento centralizado. 
## 💻 Tecnologías Utilizadas 
- **API en PHP**: La aplicación se comunica con una API en PHP que maneja la sincronización de datos entre el dispositivo y la base de datos remota.
- **Retrofit**: Se utiliza Retrofit para consumir la API de manera eficiente.
- **SQLite**: Para el registro de pasajes, Wayra utiliza SQLite, una base de datos local ligera y rápida.
- **Material Design**: El diseño de la interfaz de usuario sigue los principios de Material Design para una experiencia visual coherente y agradable.
## 🛠️ Instalación 
1. Clona este repositorio en tu máquina local.
```bash
git clone https://github.com/Minkaspr/Wayra.git
```
2. Abre el proyecto en Android Studio.
3. Configura las dependencias y el archivo gradle según sea necesario.
