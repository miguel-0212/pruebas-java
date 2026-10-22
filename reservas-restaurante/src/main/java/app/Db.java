package app;

import java.sql.*;
import java.util.*;

public class Db {
    public record Reserva(int id, String nombre, int personas, String fecha, String hora, String estado) {}

    private static Connection conn() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:reservas.db");
    }

    public static void init() {
        try (var c = conn(); var s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS reservas(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL, personas INTEGER NOT NULL, fecha TEXT NOT NULL,"
                    + "hora TEXT NOT NULL, estado TEXT NOT NULL DEFAULT 'Pendiente')");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<Reserva> listar() {
        var lista = new ArrayList<Reserva>();
        try (var c = conn(); var rs = c.createStatement().executeQuery("SELECT * FROM reservas ORDER BY fecha, hora")) {
            while (rs.next())
                lista.add(new Reserva(rs.getInt("id"), rs.getString("nombre"), rs.getInt("personas"),
                        rs.getString("fecha"), rs.getString("hora"), rs.getString("estado")));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }

    public static void insertar(String nombre, int personas, String fecha, String hora) {
        try (var c = conn(); var p = c.prepareStatement(
                "INSERT INTO reservas(nombre,personas,fecha,hora) VALUES(?,?,?,?)")) {
            p.setString(1, nombre); p.setInt(2, personas); p.setString(3, fecha); p.setString(4, hora);
            p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void cambiarEstado(int id, String estado) {
        try (var c = conn(); var p = c.prepareStatement("UPDATE reservas SET estado=? WHERE id=?")) {
            p.setString(1, estado); p.setInt(2, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void borrar(int id) {
        try (var c = conn(); var p = c.prepareStatement("DELETE FROM reservas WHERE id=?")) {
            p.setInt(1, id); p.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static int comensalesConfirmados(String fecha) {
        try (var c = conn(); var p = c.prepareStatement(
                "SELECT COALESCE(SUM(personas),0) FROM reservas WHERE fecha=? AND estado='Confirmada'")) {
            p.setString(1, fecha);
            return p.executeQuery().getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
