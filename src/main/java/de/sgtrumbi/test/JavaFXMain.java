package de.sgtrumbi.test;

import de.sgtrumbi.opengl_implementation.Shader;
import de.sgtrumbi.opengl_implementation.texturing.FrameBufferTexture;
import de.sgtrumbi.opengl_implementation.texturing.Mesh;
import de.sgtrumbi.opengl_implementation.texturing.MeshFactory;
import de.sgtrumbi.opengl_implementation.texturing.TextureFactory;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;

/**
 * The JavaFXMain class.
 *
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi
 */
public class JavaFXMain {

    /**
     * The GLFW window handle.
     *
     * Here you find information about creating the window:
     * https://www.lwjgl.org/guide
     */
    private long window;

    /** The stage, on which the JavaFX scene should be drawn. */
    private static GLStagePlatform plat;

    // All the GLFW callbacks:
    // Used to call the window events in the GLStagePlatform.
    // They have to be static to prevent exceptions which could be
    // thrown if a callback event happens and the callback should be
    // invoked by a native void (or GLFW.glfwPollEvents(), I don't know
    // how this works...), but the garbage collector, not knowing that
    // it probably could be invoked, finalized it.
    // -> a closure exception happens.
    // If it is static the callback can not be finalized.
    /** The error callback (Prints errors to {@link System#err}, when an error in GLFW happens. */
    private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
    /** The key callback checks, if a key event happens. */
    private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            // If escape was pressed...
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                // ... close the window.
                GLFW.glfwSetWindowShouldClose(window, GL11.GL_TRUE);
            }

            // Switch the action:
            switch (action) {
                // Case, the key was pressed:
                case GLFW.GLFW_PRESS:
                    // Send it to the stage
                    plat.onKeyPress(key);
                    // and break.
                    break;
                // Case, the key was released:
                case GLFW.GLFW_RELEASE:
                    // Send it to the stage
                    plat.onKeyRelease(key);
                    // and break.
                    break;
                // When nothing matched:
                default:
                    // break the switch statement.
                    // (Do nothing)
                    break;
            }
        }
    };
    /** The cursor position callback listens for cursor movement. */
    private static GLFWCursorPosCallback cursorPositionCallback = new GLFWCursorPosCallback() {
        public void invoke(long window, double x, double y) {
            if(plat != null) {
                // Say the stage, that the mouse was moved.
                plat.onMouseMove((int) x, (int) y);
            }
        }
    };
    /** The mouse button callback listens for mouse button press. */
    private static GLFWMouseButtonCallback butt_call = new GLFWMouseButtonCallback() {
        public void invoke(long window, int button, int action, int mods) {
            if(plat != null) {
                if(action == GLFW.GLFW_PRESS) {
                    plat.onMouseButtonPress(button + 1);
                }

                if(action == GLFW.GLFW_RELEASE) {
                    plat.onMouseButtonRelease(button + 1);
                }
            }
        }
    };
    /** The char listens for typed keys. */
    private static GLFWCharCallback charcall = new GLFWCharCallback() {
        public void invoke(long window, int codepoint) {
            System.out.println(codepoint);

            plat.onKeyType((char) codepoint);
        }
    };
    /** The size callback is called, when the window was resized. */
    private static GLFWWindowSizeCallback windowSizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            // Resize the JavaFX scene and the frame texture.
            plat.onResize(width, height);
        }
    };

    /** The texture containing the JavaFX scene. */
    private FrameBufferTexture texture;
    /** The shader object for the 'res/shader.vert' and 'res/shader.frag' path. */
    private Shader shader;
    /**
     * The vertex attribute array representation for the frame buffer texture.
     *
     * (Stored the <code>vertices</code> and the <code>textureCoordinates</code> arrays)
     */
    private Mesh mesh;

    /**
     * The constructor.
     * Initializes the GLFW window and performs the main game loop.
     */
    public JavaFXMain() {
        // Setup the error callback
        GLFW.glfwSetErrorCallback(errorCallback);

        if (GLFW.glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Set the default window hints to the GLFW window
        GLFW.glfwDefaultWindowHints();

        // Set up some OpenGL compatibility stuff...
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        // Set the window resizable
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);

        // Create the window
        window = GLFW.glfwCreateWindow(512, 512, "My Title", MemoryUtil.NULL, MemoryUtil.NULL);

        // If the window is null...
        // (means, that it couldn't be created)
        if (window == MemoryUtil.NULL) {
            // ... terminate GLFW and
            GLFW.glfwTerminate();
            // Exit the program with a RuntimeException
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup the callbacks
        // (Important for the input handling on the stage)
        // Setup the key callback,
        GLFW.glfwSetKeyCallback(window, keyCallback);
        // the cursor callback,
        // (Called when the cursor was moved)
        GLFW.glfwSetCursorPosCallback(window, cursorPositionCallback);
        // the window size callback,
        // (Called when the window was resized)
        GLFW.glfwSetWindowSizeCallback(window, windowSizeCallback);
        // the mouse button callback
        // (Called when a mouse button was pressed)
        GLFW.glfwSetMouseButtonCallback(window, butt_call);
        // and the char callback.
        // (Called when a key was pressed)
        GLFW.glfwSetCharCallback(window, charcall);

        // Mark the current thread as OpenGL context compatible.
        GLFW.glfwMakeContextCurrent(window);

        // Get the OpenGL context for the current thread
        GL.createCapabilities();

        // Setup the stage for the JavaFX stuff
        plat = new GLStagePlatform(512, 512);

        // Setup the JavaFX application
        Platform.runLater(() -> {
            // Set the scene to the JavaFX application
            plat.setScene(createScene());
        });

        // Create the frame buffer texture for the JavaFX application.
        // On this texture, all of the JavaFX will be displayed.
        texture = plat.setupTexture();

        // Create the base for the JavaFX application texture
        mesh = MeshFactory.create(
                // The texture goes over the whole screen
                // -> the vertices are from -1 to 1.
                new float[] {
                    1f, -1f,
                    -1f, -1f,
                    1f, 1f,
                    -1f, 1f,
                },
                // Same to the texture coordinates
                new float[] {
                    1f,1f,
                    0f,1f,
                    1f,0f,
                    0f,0f,
                }
        );

        // Create a new shader.
        // (They are located in the res folder)
        shader = new Shader("res/shader.vert", "res/shader.frag");

        // Set the screen clear color
        GL11.glClearColor(1f, 1f, 1f, 1.0f);

        // Enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        // Set the alpha blending function
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // While not the GLFW window should close...
        while (GLFW.glfwWindowShouldClose(window) != GL11.GL_TRUE) {
            // ... do the main game loop.
            update();
        }

        // After the game loop:
        //   - Destroy the GLFW window
        GLFW.glfwDestroyWindow(window);
        //   - Release the key callback
        keyCallback.release();
        //   - Terminate GLFW
        GLFW.glfwTerminate();
        //   - Release the error callback
        errorCallback.release();
        //   - Exit the JavaFX platform
        Platform.exit();

        // Clean up the factories
        // (Means: giving memory back)
        MeshFactory.cleanUp();
        TextureFactory.cleanUp();
    }

    /**
     * Updates the display.
     *
     * Gets the JavaFX scene and renders it on the screen.
     *
     * TODO: 07.02.2016 Only draw when dirty
     */
    public void update() {
        // Poll the events.
        GLFW.glfwPollEvents();

        // Update the stage
        plat.update();

        // Clear the screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Enable the shader
        shader.bind();
        // Enable the first OpenGL texture unit.
        // https://www.opengl.org/wiki/Texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        // Enable the sampler2D int the fragment shader
        // (Enabling the sampler2D just means to pass an 0-int
        // to it's uniform location, the rest is done by the
        // GL11.glBindTexture stuff in the ITexture (The texture
        // implementation in the TextureFactory))
        shader.setUniform1i("u_Texture", 0);
        // Bind the texture
        // (The JavaFX scene texture)
        texture.bind();

        // Bind the vertex array object
        // (Represented by the mesh class)
        mesh.bind();
        // Draw the mesh
        // (Using GL11.glDrawArrays(...))
        mesh.draw();
        // Unbind the vertex array
        // (Means binding a 0 value)
        mesh.unbind();

        // Unbind the texture
        texture.unbind();
        // Unbind the shader
        shader.unbind();

        // Swap the display buffers
        // (The framebuffer, which was currently written
        // to and the display buffer)
        GLFW.glfwSwapBuffers(window);
    }

    /**
     * Creates a simple JavaFX scene with a {@link Button},
     * a {@link TextField} an a {@link Label}.
     * When you click the button, his text changes from 'Accept'
     * to 'Accepted'.
     *
     * (I won't explain the JavaFX code)
     *
     * @return The scene.
     */
    private Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root,
                // Set the scene background to transparent!
                new Color(0, 0, 0, 0));

        Button button2 = new Button("Accept");
        // button2.setStyle("-fx-font: 22 arial; -fx-base: #0000ff;");

        Label label1 = new Label("Name:");
        TextField textField = new TextField ();
        // label1.setTextFill(Color.RED);
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button2, new Label("Hello"));
        hb.setSpacing(10);

        root.getChildren().add(hb);

        textField.requestFocus();

        button2.setOnAction(e -> label1.setText("Accepted"));

        return scene;
    }

    /**
     * The main method.
     *
     * @apiNote Define '-Djava.library.path' in the command line arguments!
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        // Just call the constructor
        new JavaFXMain();
    }
}
