package app;

import java.sql.*;
import java.util.*;

public class Db {
    public record Producto(int id, String nombre, double precio, int stock) {}

    private static Connection conn() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:tienda.db");
    }

    public static void init() {
        try (var c = conn(); var s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS productos(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL, precio REAL NOT NULL, stock INTEGER NOT NULL)");
            s.execute("CREATE TABLE IF NOT EXISTS ventas(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "producto TEXT NOT NULL, importe REAL NOT NULL, fecha TEXT NOT NULL)");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<Producto> listar() {
        var lista = new ArrayList<Producto>();
        try (var c = conn(); var rs = c.createStatement().executeQuery("SELECT * FROM productos ORDER BY nombre")) {
            while (rs.next())
                lista.add(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock")));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public static void insertar(String nombre, double precio, int stock) {
        try (var c = conn(); var p = c.prepareStatement("INSERT INTO productos(nombre,precio,stock) VALUES(?,?,?)")) {
            p.setString(1, nombre); p.setDouble(2, precio); p.setInt(3, stock); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void borrar(int id) {
        try (var c = conn(); var p = c.prepareStatement("DELETE FROM productos WHERE id=?")) {
            p.setInt(1, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Vende una unidad: descuenta stock y registra la venta en una sola transacción. */
    public static boolean vender(Producto prod) {
        try (var c = conn()) {
            c.setAutoCommit(false);
            try (var u = c.prepareStatement("UPDATE productos SET stock=stock-1 WHERE id=? AND stock>0")) {
                u.setInt(1, prod.id());
                if (u.executeUpdate() == 0) { c.rollback(); return false; }
            }
            try (var i = c.prepareStatement("INSERT INTO ventas(producto,importe,fecha) VALUES(?,?,date('now','localtime'))")) {
                i.setString(1, prod.nombre()); i.setDouble(2, prod.precio()); i.executeUpdate();
            }
            c.commit();
            return true;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static double totalHoy() {
        try (var c = conn(); var rs = c.createStatement().executeQuery(
                "SELECT COALESCE(SUM(importe),0) FROM ventas WHERE fecha=date('now','localtime')")) {
            return rs.getDouble(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
