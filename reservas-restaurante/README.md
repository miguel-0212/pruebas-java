# Reservas para restaurante

Aplicación de escritorio para registrar reservas de mesa y controlar su estado.

## Funcionalidades
- Alta de reservas (nombre, comensales, fecha y hora)
- Estados: Pendiente, Confirmada, Cancelada
- Cambio de estado y borrado de reservas
- Contador de comensales confirmados

## Tecnologías
Java 17 · JavaFX 21 · SQLite (JDBC) · Maven

## Cómo ejecutarlo
Requisitos: JDK 17+ y Maven.

```bash
git clone <url-del-repo>
cd reservas-restaurante
mvn javafx:run
```

La base de datos SQLite se crea sola en el primer arranque.

## Estructura
- `Db.java`: acceso a datos (JDBC, consultas preparadas)
- `Main.java`: interfaz JavaFX

## Posibles mejoras
Exportar a CSV, tests unitarios de la capa de datos, empaquetado con jpackage.
