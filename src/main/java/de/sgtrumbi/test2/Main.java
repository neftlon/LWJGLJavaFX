package de.sgtrumbi.test2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.lwjgl.opengl.GL11;

/**
 * @author Johannes (on 12.02.2016)
 * @see de.sgtrumbi.test2
 */
public class Main extends Application {

    private final int initialWidth = 1280, initialHeight = 720;

    static {
        GLView.setDebug(true);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        View view = new GLView(() -> {
            GL11.glClearColor((float) Math.random(), (float) Math.random(), (float) Math.random(),
                    (float) Math.random());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }, initialWidth, initialHeight);

        Button button = new Button("Image update");
        button.setOnAction(event -> view.onUpdate());

        StackPane root = new StackPane();
        root.getChildren().addAll(view.get(), button);

        Scene scene = new Scene(root, initialWidth, initialHeight);
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> view.get().setFitWidth(newValue.intValue()));
        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> view.get().setFitHeight(newValue.intValue()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hello");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> view.onClose());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
