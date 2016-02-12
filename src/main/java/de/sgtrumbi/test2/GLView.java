package de.sgtrumbi.test2;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * @author Johannes (on 12.02.2016)
 * @see de.sgtrumbi.test2
 */
public class GLView implements View {

    private DrawCallback drawCallback;
    private WritableImage bufferImage;
    private long glfwWindowHandle;
    private ByteBuffer buffer;
    private int width, height;

    public GLView(DrawCallback readyToDrawNotifier, int initialWidth, int initialHeight) {
        this.drawCallback = readyToDrawNotifier;

        glfwWindowHandle = setupContext();

        onResize(initialWidth, initialHeight);
    }

    @Override
    public void onUpdate() {
        drawCallback.onDraw();

        GL11.glViewport(0, 0, width, height);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;

                bufferImage.getPixelWriter().setArgb(x, y, (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
    }

    @Override
    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;

        bufferImage = new WritableImage(width, height);

        GLFW.glfwSetWindowSize(glfwWindowHandle, width, height);

        buffer = BufferUtils.createByteBuffer(width * height * 4);

        if (GLView.debug)
            System.out.println("(GLView): Resizing. New width: " + width + ", new height: " + height + ".");
    }

    @Override
    public void onClose() {
        if (GLView.debug)
            System.out.println("(GLView): Shutting GLFW and OpenGL-Context down.");

        GLFW.glfwDestroyWindow(glfwWindowHandle);
        GLFW.glfwTerminate();
    }

    @Override
    public ImageView get() {
        return new ImageView(bufferImage);
    }

    private long setupContext() {
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
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);

        // Create the window
        long window = GLFW.glfwCreateWindow(7, 5, "GL_Context_Holder", MemoryUtil.NULL, MemoryUtil.NULL);
        // GLFW.glfwDestroyWindow(window);
        GLFW.glfwHideWindow(window);

        // If the window is null...
        // (means, that it couldn't be created)
        if (window == MemoryUtil.NULL) {
            // ... terminate GLFW and
            GLFW.glfwTerminate();
            // Exit the program with a RuntimeException
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Mark the current thread as OpenGL context compatible.
        GLFW.glfwMakeContextCurrent(window);

        // Get the OpenGL context for the current thread
        GL.createCapabilities();

        return window;
    }

    public interface DrawCallback {

        void onDraw();
    }

    //
    // /!\ STOP! HERE COMES STATIC /!\
    //
    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        GLView.debug = debug;
    }
}
