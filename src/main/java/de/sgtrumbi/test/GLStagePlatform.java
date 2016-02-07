package de.sgtrumbi.test;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.EmbeddedStageInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.stage.EmbeddedWindow;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.Image;
import de.sgtrumbi.opengl_implementation.texturing.FrameBufferTexture;
import de.sgtrumbi.opengl_implementation.texturing.PixelGetter;
import de.sgtrumbi.opengl_implementation.texturing.TextureFactory;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.awt.event.KeyEvent;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi
 */
public class GLStagePlatform implements PixelGetter, GLStage {

    private volatile EmbeddedWindow stage;
    private EmbeddedStageInterface stagePeer;
    private EmbeddedSceneInterface scenePeer;

    /**
     * The current JavaFX scene.
     */
    private volatile Scene scene;

    /**
     * The container for the JavaFX embedded stuff.
     */
    private HostContainer hostContainer;

    /**
     * The frame buffer texture.
     */
    private FrameBufferTexture texture;

    /**
     * The width and height of the window.
     * (The window dimensions)
     */
    private int width, height;

    private boolean mouseButtonLeft, mouseButtonMiddle, mouseButtonRight;
    private boolean shift, ctrl, alt, meta;

    /**
     * Says, if the screen should be redrawn
     * (because it's 'dirty')
     * or not.
     */
    private boolean dirty = true;

    private int mousex, mousey;

    private boolean drawcursor;
    private ImageCursorFrame currentframe;

    // private Hashtable<CursorType,ImageCursorFrame> cursorimages = new Hashtable<>();

    private ReentrantLock cursorlock = new ReentrantLock();

    public GLStagePlatform(int w, int h) {
        width = w;
        height = h;
        initFx();
        hostContainer = new HostContainer();
    }

    @Override
    public boolean getPixels(IntBuffer dest, int width, int height) {
        if (scenePeer != null) {
            boolean success = scenePeer.getPixels(dest, width, height);

            if (success) {
                if (drawcursor && currentframe != null) {
                    cursorlock.lock();
                    //NOTE composite the cursor

                    Image img = (Image) currentframe.getPlatformImage();

                    for (int x = 0; x < currentframe.getWidth(); x++) {
                        for (int y = 0; y < currentframe.getHeight(); y++) {
                            int frameX = (int) (mousex - currentframe.getHotspotX() + x);
                            int frameY = (int) (mousey - currentframe.getHotspotY() + y);

                            if (((frameX >= 0) && (frameX <= width) &&
                                    ((frameY >= 0) && (frameY <= height)))) {
                                int index = frameX + (width * frameY);

                                int destColor = dest.get(index);

                                int srcColor = img.getArgb(x, y);

                                int endColor = getAlpha(destColor, srcColor);

                                dest.put(index, endColor);
                            }
                        }
                    }
                    cursorlock.unlock();
                }
                return true;
            }
        }
        return false;
    }

    private synchronized static void initFx() {
        // Note that calling PlatformImpl.startup more than once is OK
        PlatformImpl.startup(() -> {
            // No need to do anything here
        });
    }

    public void setScene(final Scene newScene) {
        if (Toolkit.getToolkit().isFxUserThread()) {
            setSceneImpl(newScene);
        } else {
            final CountDownLatch initLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                setSceneImpl(newScene);
                initLatch.countDown();
            });
            try {
                initLatch.await();
            } catch (InterruptedException z) {
                z.printStackTrace(System.err);
            }
        }
    }

    private void setSceneImpl(Scene newScene) {
        if ((stage != null) && (newScene == null)) {
            stage.hide();
            stage = null;
        }

        scene = newScene;

        if ((stage == null) && (newScene != null)) {
            stage = new EmbeddedWindow(hostContainer);
        }

        if (stage != null) {
            stage.setScene(newScene);
            if (!stage.isShowing()) {
                stage.show();
            }
        }
    }

    /**
     * The HostContainer class
     */
    private class HostContainer implements HostInterface {

        @Override
        public boolean grabFocus() {
            System.out.println("Grab focus.");

            return false;
        }

        @Override
        public void repaint() {
            System.out.println("Repaint screen.");

            dirty = true;
        }

        @Override
        public boolean requestFocus() {
            System.out.println("Requesting focus.");

            return false;
        }

        @Override
        public void setCursor(CursorFrame cursor) {
            System.out.println("Setting the cursor frame.");

            if(drawcursor) {
                // Lock the cursor
                cursorlock.lock();

                if(cursor instanceof ImageCursorFrame) {
                    currentframe = (ImageCursorFrame) cursor;
                }
                // TODO: 07.02.2016 Create else statement.
                // Currently not used else statement
                /*else {
                    // currentframe = cursorimages.get(cursor.getCursorType());
                }*/

                // Unlock the cursor
                cursorlock.unlock();

                // Set dirty to true: The display content should
                // be redrawn.
                dirty = true;
            }
        }

        @Override
        public void setEmbeddedScene(EmbeddedSceneInterface embeddedScene) {
            System.out.println("Setting the embedded scene.");

            if (scenePeer == embeddedScene) {
                return;
            }
            scenePeer = embeddedScene;

            if (width > 0 && height > 0) {
                scenePeer.setSize(width, height);
            }

            scenePeer.setPixelScaleFactor(1);
        }

        @Override
        public void setEmbeddedStage(EmbeddedStageInterface embeddedStage) {
            System.out.println("Setting the embedded stage.");

            stagePeer = embeddedStage;
            if (stagePeer == null) {
                return;
            }

            if (width > 0 && height > 0) {
                stagePeer.setSize(width, height);
            }

            stagePeer.setLocation(0, 0);
        }

        @Override
        public void setEnabled(boolean arg0) {
            System.out.println("Set enabled.");
        }

        @Override
        public void setPreferredSize(int arg0, int arg1) {
            System.out.println("Set preferred size to width: " + arg0 + ", height: " + arg1 + ".");
        }

        @Override
        public boolean traverseFocusOut(boolean arg0) {
            System.out.println("Traverse focus out: " + arg0 + ".");

            return false;
        }

        @Override
        public void ungrabFocus() {
            System.out.println("Ungrab focus.");
        }
    }

    /**
     * Creates the frame buffer texture using self as
     * the {@link PixelGetter} parameter and the current
     * width and height.
     *
     * @return The frame buffer texture.
     */
    public FrameBufferTexture setupTexture() {
        // Create the frame buffer texture.
        FrameBufferTexture texture = TextureFactory.createFrameBufferTexture(this, width, height);

        // Set the current texture in the class
        // to the texture
        this.texture = texture;

        // Return the texture
        return texture;
    }

    /**
     * Called once every frame to update the
     * frame buffer texture.
     */
    public void update() {
        // if the texture is 'dirty'
        if(dirty) {
            // Update the texture
            // (Repaint the content to it)
            texture.update();

            // Set dirty to false
            // (The content was redrawn,
            // wait for next change)
            dirty = false;
        }
    }

    /**
     * Called, when a mouse button was pressed.
     *
     * @param button The mouse button, which was pressed.
     */
    @Override
    public void onMouseButtonPress(final int button) {
        // If the scene peer is not null...
        if(scenePeer != null) {
            // ... focus it.
            stagePeer.setFocused(true, AbstractEvents.FOCUSEVENT_ACTIVATED);

            // The button, the event happened to.
            int eventButton = 0;
            // Switch the button
            switch (button) {
                case 1:
                    mouseButtonLeft = true;
                    eventButton = AbstractEvents.MOUSEEVENT_PRIMARY_BUTTON;
                    break;
                case 2:
                    mouseButtonRight = true;
                    eventButton = AbstractEvents.MOUSEEVENT_SECONDARY_BUTTON;
                    break;
                case 3:
                    mouseButtonMiddle = true;
                    eventButton = AbstractEvents.MOUSEEVENT_MIDDLE_BUTTON;
                    break;
            }

            scenePeer.mouseEvent(AbstractEvents.MOUSEEVENT_PRESSED,
                    eventButton, mouseButtonLeft, mouseButtonMiddle, mouseButtonRight, mousex, mousey, mousex, mousey,
                    shift, ctrl, alt, meta, 0, false);
        }
    }

    /**
     * Called, when a mouse button was released.
     *
     * @param button The mouse button, which was released
     */
    @Override
    public void onMouseButtonRelease(int button) {
        if(scenePeer != null) {
            int eventButton = 0;

            switch (button) {
                case 1:
                    mouseButtonLeft = false;
                    eventButton = AbstractEvents.MOUSEEVENT_PRIMARY_BUTTON;
                    break;
                case 2:
                    mouseButtonRight = false;
                    eventButton = AbstractEvents.MOUSEEVENT_SECONDARY_BUTTON;
                    break;
                case 3:
                    mouseButtonMiddle = false;
                    eventButton = AbstractEvents.MOUSEEVENT_MIDDLE_BUTTON;
                    break;
            }

            scenePeer.mouseEvent(AbstractEvents.MOUSEEVENT_RELEASED,
                    eventButton, mouseButtonLeft, mouseButtonMiddle, mouseButtonRight, mousex, mousey, mousex, mousey,
                    shift, ctrl, alt, meta, 0, false);
        }
    }

    /**
     * Called, when the mouse was moved.
     *
     * @param x The new x position of the mouse on the window.
     * @param y The new y position of the mouse on the window.
     */
    @Override
    public void onMouseMove(int x, int y) {
        if(drawcursor) {
            dirty = true;
        }

        if(scenePeer != null) {
            mousex = x;
            mousey = y;

            if(mouseButtonLeft || mouseButtonMiddle || mouseButtonRight) {
                scenePeer.mouseEvent(AbstractEvents.MOUSEEVENT_DRAGGED,
                        AbstractEvents.MOUSEEVENT_NONE_BUTTON, mouseButtonLeft, mouseButtonMiddle, mouseButtonRight,
                        mousex, mousey, mousex, mousey,
                        shift, ctrl, alt, meta, 0, false);
            } else {
                scenePeer.mouseEvent(AbstractEvents.MOUSEEVENT_MOVED,
                        AbstractEvents.MOUSEEVENT_NONE_BUTTON, mouseButtonLeft, mouseButtonMiddle, mouseButtonRight,
                        mousex, mousey, mousex, mousey,
                        shift, ctrl, alt, meta, 0, false);
            }
        }
    }

    /**
     * Called, when a key was pressed.
     *
     * @param keyCode The key, which was pressed.
     */
    @Override
    public void onKeyPress(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_SHIFT:
                shift = true;

                break;
            case KeyEvent.VK_CONTROL:
                ctrl = true;

                break;
            case KeyEvent.VK_ALT:
                alt = true;

                break;
            case KeyEvent.META_DOWN_MASK:
                meta = true;
                break;
        }

        int modifiers = 0;
        if(shift)
            modifiers |= AbstractEvents.MODIFIER_SHIFT;

        if(ctrl)
            modifiers |= AbstractEvents.MODIFIER_CONTROL;

        if(alt)
            modifiers |= AbstractEvents.MODIFIER_ALT;

        if(meta)
            modifiers |= AbstractEvents.MODIFIER_META;

        char[] c = {(char) keyCode};

        scenePeer.keyEvent(AbstractEvents.KEYEVENT_PRESSED, keyCode, c, modifiers);
    }

    /**
     * Called, when a key was released.
     *
     * @param keyCode The key, which was released.
     */
    @Override
    public void onKeyRelease(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_SHIFT:
                shift = false;
                break;
            case KeyEvent.VK_CONTROL:
                ctrl = false;
                break;
            case KeyEvent.VK_ALT:
                alt = false;
                break;
            case KeyEvent.META_DOWN_MASK:
                meta = false;
                break;
        }

        int modifiers = 0;

        if(shift)
            modifiers |= AbstractEvents.MODIFIER_SHIFT;

        if(ctrl)
            modifiers |= AbstractEvents.MODIFIER_CONTROL;

        if(alt)
            modifiers |= AbstractEvents.MODIFIER_ALT;

        if(meta)
            modifiers |= AbstractEvents.MODIFIER_META;

        char[] c = {};

        scenePeer.keyEvent(AbstractEvents.KEYEVENT_RELEASED, keyCode, c, modifiers);
    }

    /**
     * Called, when a key was typed.
     *
     * @param letter The char, which was typed.
     */
    @Override
    public void onKeyType(char letter) {
        int modifiers=0;

        if(shift)
            modifiers |= AbstractEvents.MODIFIER_SHIFT;

        if(ctrl)
            modifiers |= AbstractEvents.MODIFIER_CONTROL;

        if(alt)
            modifiers |= AbstractEvents.MODIFIER_ALT;

        if(meta)
            modifiers |= AbstractEvents.MODIFIER_META;

        char[] c = {letter};

        scenePeer.keyEvent(AbstractEvents.KEYEVENT_TYPED, com.sun.glass.events.KeyEvent.VK_UNDEFINED, c, modifiers);
    }

    /**
     * Called when the window was resized.
     *
     * @param w The new width of the window.
     * @param h The new width of the window.
     */
    public void onResize(int w, int h) {
        width = w;
        height = h;

        if(scenePeer != null) {
            scenePeer.setSize(w, h);
        }

        if(stagePeer != null) {
            stagePeer.setSize(w , h );
        }

        if(texture != null) {
            texture.resize(w, h);
        }
    }

    /**
     * Gets the alpha value of a pixel.
     *
     * @param bg The background.
     * @param src The source pixel value.
     * @return The alpha value.
     */
    private int getAlpha(int bg, int src) {
        int bA = src & 0xff;
        int gA = (src >> 8) & 0xff;
        int rA = (src >> 16) & 0xff;
        int aA = (src >> 24) & 0xff;

        int bB = bg & 0xff;
        int gB = (bg >> 8) & 0xff;
        int rB = (bg >> 16) & 0xff;
        int aB = (bg >> 24) & 0xff;

        int rOut = (rA * aA / 255) + (rB * aB * (255 - aA) / (255 * 255));
        int gOut = (gA * aA / 255) + (gB * aB * (255 - aA) / (255 * 255));
        int bOut = (bA * aA / 255) + (bB * aB * (255 - aA) / (255 * 255));
        int aOut = aA + (aB * (255 - aA) / 255);

        return (aOut << 24) | (rOut << 16) | (gOut << 8) | (bOut);
    }
}