package app;

import app.Db.Producto;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.function.Function;

public class Main extends Application {
    private final TableView<Producto> tabla = new TableView<>();
    private final Label total = new Label();

    private static <T> TableColumn<T, String> col(String titulo, Function<T, String> f) {
        var c = new TableColumn<T, String>(titulo);
        c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue())));
        return c;
    }

    private void refrescar() {
        tabla.getItems().setAll(Db.listar());
        total.setText(String.format("Ventas de hoy: %.2f €", Db.totalHoy()));
    }

    @Override
    public void start(Stage stage) {
        Db.init();
        tabla.getColumns().addAll(col("Producto", Producto::nombre),
                col("Precio (€)", p -> String.format("%.2f", p.precio())),
                col("Stock", p -> String.valueOf(p.stock())));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setStyle(!empty && p != null && p.stock() <= 3 ? "-fx-background-color: #ffd6d6;" : "");
            }
        });

        var nombre = new TextField(); nombre.setPromptText("Producto");
        var precio = new TextField(); precio.setPromptText("Precio");
        var stock = new TextField(); stock.setPromptText("Stock");
        var msg = new Label();

        var anadir = new Button("Añadir producto");
        anadir.setOnAction(e -> {
            try {
                double pr = Double.parseDouble(precio.getText().replace(',', '.'));
                int st = Integer.parseInt(stock.getText().trim());
                if (nombre.getText().isBlank() || pr < 0 || st < 0) throw new NumberFormatException();
                Db.insertar(nombre.getText().trim(), pr, st);
                nombre.clear(); precio.clear(); stock.clear(); msg.setText("Producto añadido.");
                refrescar();
            } catch (NumberFormatException ex) { msg.setText("Revisa nombre, precio y stock."); }
        });

        var vender = new Button("Vender 1 unidad");
        vender.setOnAction(e -> {
            var sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) { msg.setText("Selecciona un producto."); return; }
            msg.setText(Db.vender(sel) ? "Venta registrada." : "Sin stock.");
            refrescar();
        });

        var borrar = new Button("Eliminar");
        borrar.setOnAction(e -> {
            var sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) { Db.borrar(sel.id()); refrescar(); }
        });

        var root = new VBox(10, new HBox(8, nombre, precio, stock, anadir), tabla,
                new HBox(10, vender, borrar, msg), total);
        root.setPadding(new Insets(12));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        refrescar();
        stage.setTitle("Inventario y TPV");
        stage.setScene(new Scene(root, 780, 480));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
