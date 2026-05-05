# 🐾 AppPet - Sistema Integral para el Cuidado de Mascotas

AppPet es una aplicación móvil desarrollada en Android Studio utilizando Java y SQLite, diseñada para gestionar de manera integral la información de mascotas, vacunas, historial médico, citas veterinarias y notificaciones automáticas. La aplicación permite registrar mascotas, almacenar fotografías, controlar vacunas y citas veterinarias, generar reportes PDF y administrar información de los dueños de las mascotas desde un solo lugar.

---

# 📱 Características principales

✅ Registro de usuarios  
✅ Inicio de sesión  
✅ Perfil del dueño de la mascota  
✅ Registro completo de mascotas  
✅ Edición de información de mascotas  
✅ Carga de fotografías desde galería o cámara  
✅ Historial médico  
✅ Gestión de vacunas  
✅ Recordatorios automáticos de vacunas  
✅ Gestión de citas veterinarias  
✅ Notificaciones emergentes  
✅ Generación de PDF con información de la mascota  
✅ Base de datos SQLite integrada  
✅ Carrito de compras y tienda para mascotas  
✅ Historial de pedidos  

---

# 🛠️ Tecnologías utilizadas

- Java
- Android Studio
- SQLite
- RecyclerView
- CardView
- SharedPreferences
- NotificationManager
- FileProvider
- PDFDocument
- ConstraintLayout

---

# 🗄️ Base de datos SQLite

La aplicación utiliza SQLite para almacenar la información localmente.

## Tablas implementadas

- usuarios
- mascotas
- vacunas
- historial
- citas
- notificaciones
- productos
- carrito
- pedidos
- detalle_pedido

---

# 📂 Estructura principal del proyecto

```bash
app/
 ├── manifests/
 ├── java/com/liseth/miprimeraapp/
 │    ├── activities
 │    ├── adapters
 │    ├── database
 │    ├── models
 │    ├── helpers
 │    └── receivers
 ├── res/
 │    ├── layout
 │    ├── drawable
 │    ├── mipmap
 │    ├── values
 │    └── xml
```

---

# 🚀 Instalación y ejecución

## 1️⃣ Clonar repositorio

```bash
git clone https://github.com/Monroybr/MiPrimeraApp.git
```

## 2️⃣ Abrir proyecto

Abrir Android Studio y seleccionar:

```bash
Open > MiPrimeraApp
```

## 3️⃣ Sincronizar Gradle

Esperar que Android Studio descargue las dependencias automáticamente.

## 4️⃣ Ejecutar aplicación

Conectar un dispositivo físico o iniciar un emulador y presionar:

```bash
Run ▶
```

---

# 📸 Funcionalidades destacadas

## 🐶 Registro de mascotas

Permite almacenar:

- Nombre
- Raza
- Edad
- Sexo
- Peso
- Color
- Alergias
- Observaciones
- Fotografía

## 💉 Vacunas y recordatorios

La aplicación genera notificaciones automáticas para:

- Vacunas próximas
- Vacunas vencidas
- Citas veterinarias

## 📄 Generación de PDF

La aplicación permite exportar la información completa de la mascota en formato PDF incluyendo fotografía.

---

# 🔔 Permisos utilizados

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
```

---

# 👩‍💻 Autora

**Liseth Dayana Monroy Briñez**  
Ingeniería de Sistemas — UNAD

GitHub:  
https://github.com/Monroybr

---

# 📚 Referencias

Aditya, S. K., Mohanta, P., & Karn, V. K. (2014). *Android SQLite Essentials*. Packt Publishing.

Kotipalli, S. R., & Imran, M. A. (2016). *Hacking Android*. Packt Publishing.

Google Developers. Android Developers Documentation:  
https://developer.android.com/

---

# 📌 Estado del proyecto

🚧 Proyecto en desarrollo y mejora continua.
