# Inventario y TPV para tienda

Aplicación de escritorio para controlar el stock de una tienda y registrar ventas rápidas con total diario.

## Funcionalidades
- Alta y baja de productos con precio y stock
- Venta de una unidad con control de stock (transacción)
- Aviso visual de stock bajo (3 unidades o menos)
- Total de ventas del día

## Tecnologías
Java 17 · JavaFX 21 · SQLite (JDBC) · Maven

## Cómo ejecutarlo
Requisitos: JDK 17+ y Maven.

```bash
git clone <url-del-repo>
cd inventario-tpv
mvn javafx:run
```

La base de datos SQLite se crea sola en el primer arranque.

## Estructura
- `Db.java`: acceso a datos (JDBC, consultas preparadas)
- `Main.java`: interfaz JavaFX

## Posibles mejoras
Exportar a CSV, tests unitarios de la capa de datos, empaquetado con jpackage.
