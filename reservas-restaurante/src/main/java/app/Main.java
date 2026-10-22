package app;

import app.Db.Reserva;
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
    private final TableView<Reserva> tabla = new TableView<>();
    private final Label resumen = new Label();

    private static <T> TableColumn<T, String> col(String titulo, Function<T, String> f) {
        var c = new TableColumn<T, String>(titulo);
        c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue())));
        return c;
    }

    private void refrescar() {
        tabla.getItems().setAll(Db.listar());
        resumen.setText("Comensales confirmados hoy: " + Db.comensalesConfirmados(LocalDate.now().toString()));
    }

    @Override
    public void start(Stage stage) {
        Db.init();
        tabla.getColumns().addAll(col("Fecha", Reserva::fecha), col("Hora", Reserva::hora),
                col("Nombre", Reserva::nombre), col("Personas", r -> String.valueOf(r.personas())),
                col("Estado", Reserva::estado));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var nombre = new TextField(); nombre.setPromptText("Nombre");
        var personas = new Spinner<Integer>(1, 20, 2); personas.setPrefWidth(70);
        var fecha = new DatePicker(LocalDate.now());
        var hora = new TextField(); hora.setPromptText("HH:mm"); hora.setPrefWidth(70);
        var msg = new Label();

        var anadir = new Button("Añadir reserva");
        anadir.setOnAction(e -> {
            if (nombre.getText().isBlank() || fecha.getValue() == null
                    || !hora.getText().matches("([01]\\d|2[0-3]):[0-5]\\d")) {
                msg.setText("Revisa los datos (hora en formato HH:mm)."); return;
            }
            Db.insertar(nombre.getText().trim(), personas.getValue(), fecha.getValue().toString(), hora.getText());
            nombre.clear(); hora.clear(); msg.setText("Reserva creada."); refrescar();
        });

        var estado = new ComboBox<String>();
        estado.getItems().addAll("Pendiente", "Confirmada", "Cancelada"); estado.setValue("Confirmada");
        var aplicar = new Button("Aplicar estado");
        aplicar.setOnAction(e -> {
            var sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) { Db.cambiarEstado(sel.id(), estado.getValue()); refrescar(); }
        });
        var borrar = new Button("Eliminar");
        borrar.setOnAction(e -> {
            var sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) { Db.borrar(sel.id()); refrescar(); }
        });

        var root = new VBox(10, new HBox(8, nombre, personas, fecha, hora, anadir), tabla,
                new HBox(10, estado, aplicar, borrar, msg), resumen);
        root.setPadding(new Insets(12));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        refrescar();
        stage.setTitle("Reservas del restaurante");
        stage.setScene(new Scene(root, 860, 480));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
