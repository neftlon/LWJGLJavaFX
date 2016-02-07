package de.sgtrumbi.opengl_implementation.texturing;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.opengl_implementation.texturing
 */
public interface FrameBufferTexture extends Texture {

    /**
     * Called, when the texture was resized.
     *
     * @param width The new width of the texture.
     * @param height The new height of the texture.
     */
    void resize(int width, int height);

    /**
     * Called to update the texture.
     */
    void update();

    int getWidth();

    int getHeight();
}
