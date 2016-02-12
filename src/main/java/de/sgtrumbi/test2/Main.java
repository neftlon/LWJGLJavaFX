package de.sgtrumbi.test2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.lwjgl.opengl.GL11;

/**
 * @author Johannes (on 12.02.2016)
 * @see de.sgtrumbi.test2
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("Image update");

        GLView.setDebug(true);
        View view = new GLView(() -> {
            GL11.glClearColor((float) Math.random(), (float) Math.random(), (float) Math.random(),
                    (float) Math.random());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }, 700, 500);

        button.setOnAction(event -> view.onUpdate());

        GridPane root = new GridPane();
        root.setHgap(20);
        root.setVgap(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.add(view.get(), 0, 1);
        root.add(button, 0, 0);

        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setTitle("Hello");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> view.onClose());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
