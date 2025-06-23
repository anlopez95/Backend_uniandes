# 🧠 Backend_uniandes

Backend desarrollado para pruebas técnicas de Uniandes. Incluye servicios expuestos vía HTTP, conexión con base de datos Oracle, y ejecución de procedimientos almacenados.

---

## 📦 Requisitos previos

Asegúrate de tener instalado:

- ✅ **Java 17 o superior**
- ✅ **Apache Maven** (v3.8+)
- ✅ **Oracle Database** (local o remota)
- ✅ **Eclipse IDE** (preferiblemente la edición para desarrolladores Java EE)
- ✅ **Postman** (o cualquier cliente de pruebas HTTP)

---

## 🔧 Configuración inicial

### 1️⃣ Clonar el repositorio
```bash
git clone https://github.com/anlopez95/Backend_uniandes.git
```

### 2️⃣ Importar el proyecto en Eclipse
```bash
Abre Eclipse.

Ve a File > Import.

Selecciona Maven > Existing Maven Projects.

Navega hasta la carpeta del proyecto clonado.

Haz clic en Finish.

> ☑ Eclipse reconocerá automáticamente el archivo pom.xml.
```

### 3️⃣ Configurar conexión a Oracle Database
Edita el archivo: src/main/resources/application.properties

Modifica estas propiedades según tu conexión:
```bash
properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/xe
spring.datasource.username=USUARIO_AQUI
spring.datasource.password=CLAVE_AQUI
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```
Nota: Asegúrate de que el usuario tenga acceso a las tablas necesarias: SARADAP, SARCHKL, STVADMR, etc.

### 4️⃣ Ejecutar scripts de base de datos
Antes de iniciar la aplicación, asegúrate de que se haya ejecutado el siguiente script:
```bash
scripts/UniAndes_CQ0001_22062025.sql
```
Nota: Este script crea el procedimiento almacenado PU_REGISTRAR_ELEMENTO_VERIFICACION. Si no se ejecuta, la API no podrá registrar datos correctamente.

### 5️⃣ Descargar dependencias y compilar el proyecto

Desde Eclipse:

Clic derecho sobre el proyecto → Maven → Update Project
Luego: Run As > Maven clean install

Desde terminal:
mvn clean install

### 6️⃣ Ejecutar la aplicación
Selecciona el proyecto y posteriormente presiona clic derecho sobre el mismo > Run As > SpringBoot APP

### 🚀 Pruebas con Postman
Endpoints disponibles:
```bash
GET /operaciones-verificacion/consulta-pagos?program=XXXXXXX&term_code=XXXXXXX

POST /operaciones-verificacion/registro-verificacion
```
Nota: Para el servicio get reemplaza XXXXXX por los datos del programa y periodo a consultar











