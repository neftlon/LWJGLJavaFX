package de.sgtrumbi.test;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.test
 */
public interface GLStage {

    /**
     * Called, when a key was pressed.
     *
     * @param keyCode The key, which was pressed.
     */
    void onKeyPress(int keyCode);

    /**
     * Called, when a key was released.
     *
     * @param keyCode The key, which was released.
     */
    void onKeyRelease(int keyCode);

    /**
     * Called when a key was typed.
     *
     * @param keyCode The key typed.
     */
    void onKeyType(char keyCode);

    /**
     * Called, when a mouse button was pressed.
     *
     * @param button The button, which was pressed.
     */
    void onMouseButtonPress(int button);

    /**
     * Called, when a mouse button was released.
     *
     * @param button The button, which was released.
     */
    void onMouseButtonRelease(int button);

    /**
     * Called, when the mouse was moved.
     *
     * @param x The new x position of the mouse.
     * @param y The new y position of the mouse.
     */
    void onMouseMove(int x, int y);
}
