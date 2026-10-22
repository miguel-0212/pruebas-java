package app;

import app.Db.Cita;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.function.Function;

public class Main extends Application {
    private final TableView<Cita> tabla = new TableView<>();

    private static <T> TableColumn<T, String> col(String titulo, Function<T, String> f) {
        var c = new TableColumn<T, String>(titulo);
        c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue())));
        return c;
    }

    private void refrescar() { tabla.getItems().setAll(Db.listar()); }

    @Override
    public void start(Stage stage) {
        Db.init();
        tabla.getColumns().addAll(col("Fecha", Cita::fecha), col("Hora", Cita::hora),
                col("Cliente", Cita::cliente), col("Servicio", Cita::servicio));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var cliente = new TextField(); cliente.setPromptText("Cliente");
        var servicio = new ComboBox<String>(); servicio.getItems().addAll("Corte", "Tinte", "Barba", "Peinado");
        servicio.setPromptText("Servicio");
        var fecha = new DatePicker(LocalDate.now());
        var hora = new TextField(); hora.setPromptText("HH:mm"); hora.setPrefWidth(70);
        var msg = new Label();

        var anadir = new Button("Añadir cita");
        anadir.setOnAction(e -> {
            if (cliente.getText().isBlank() || servicio.getValue() == null || fecha.getValue() == null
                    || !hora.getText().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                msg.setText("Revisa los datos (hora en formato HH:mm)."); return;
            }
            boolean ok = Db.insertar(cliente.getText().trim(), servicio.getValue(),
                    fecha.getValue().toString(), hora.getText());
            msg.setText(ok ? "Cita creada." : "Ese hueco ya está ocupado.");
            if (ok) { cliente.clear(); hora.clear(); refrescar(); }
        });

        var borrar = new Button("Eliminar seleccionada");
        borrar.setOnAction(e -> {
            var sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) { Db.borrar(sel.id()); refrescar(); msg.setText("Cita eliminada."); }
        });

        var form = new HBox(8, cliente, servicio, fecha, hora, anadir);
        var root = new VBox(10, form, tabla, new HBox(10, borrar, msg));
        root.setPadding(new Insets(12));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        refrescar();
        stage.setTitle("Gestor de citas");
        stage.setScene(new Scene(root, 820, 480));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
