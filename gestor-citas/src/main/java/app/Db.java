package app;

import java.sql.*;
import java.util.*;

public class Db {
    public record Cita(int id, String cliente, String servicio, String fecha, String hora) {}

    private static Connection conn() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:citas.db");
    }

    public static void init() {
        try (var c = conn(); var s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS citas(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "cliente TEXT NOT NULL, servicio TEXT NOT NULL, fecha TEXT NOT NULL, hora TEXT NOT NULL)");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<Cita> listar() {
        var lista = new ArrayList<Cita>();
        try (var c = conn(); var rs = c.createStatement().executeQuery("SELECT * FROM citas ORDER BY fecha, hora")) {
            while (rs.next())
                lista.add(new Cita(rs.getInt("id"), rs.getString("cliente"), rs.getString("servicio"),
                        rs.getString("fecha"), rs.getString("hora")));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    /** Devuelve false si el hueco (fecha + hora) ya está ocupado. */
    public static boolean insertar(String cliente, String servicio, String fecha, String hora) {
        try (var c = conn()) {
            try (var q = c.prepareStatement("SELECT 1 FROM citas WHERE fecha=? AND hora=?")) {
                q.setString(1, fecha); q.setString(2, hora);
                if (q.executeQuery().next()) return false;
            }
            try (var p = c.prepareStatement("INSERT INTO citas(cliente,servicio,fecha,hora) VALUES(?,?,?,?)")) {
                p.setString(1, cliente); p.setString(2, servicio); p.setString(3, fecha); p.setString(4, hora);
                p.executeUpdate();
            }
            return true;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void borrar(int id) {
        try (var c = conn(); var p = c.prepareStatement("DELETE FROM citas WHERE id=?")) {
            p.setInt(1, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
