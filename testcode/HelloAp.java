package testcode;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloAp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox box = new HBox();
        box.setPadding(new Insets(10, 10, 10, 10));

        final Button left = new Button("Left");
        left.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);
        final Button right = new Button("Right");
        right.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        box.getChildren().addAll(left, spacer, right);

        primaryStage.setScene(new Scene(box, 400, 400));
        primaryStage.show();
    }
}
