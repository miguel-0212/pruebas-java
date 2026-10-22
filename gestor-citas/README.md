# Gestor de citas para peluquería

Aplicación de escritorio para que un pequeño negocio gestione su agenda de citas sin solapamientos.

## Funcionalidades
- Alta y baja de citas
- Validación de datos y de hora (HH:mm)
- Evita dos citas en el mismo hueco
- Persistencia en SQLite

## Tecnologías
Java 17 · JavaFX 21 · SQLite (JDBC) · Maven

## Cómo ejecutarlo
Requisitos: JDK 17+ y Maven.

```bash
git clone <url-del-repo>
cd gestor-citas
mvn javafx:run
```

La base de datos SQLite se crea sola en el primer arranque.

## Estructura
- `Db.java`: acceso a datos (JDBC, consultas preparadas)
- `Main.java`: interfaz JavaFX

## Posibles mejoras
Exportar a CSV, tests unitarios de la capa de datos, empaquetado con jpackage.
