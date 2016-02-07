package de.sgtrumbi.opengl_implementation.texturing;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.opengl_implementation.texturing
 */
public interface FrameBufferTexture extends Texture {

    void resize(int width, int height);

    void update();

    int getWidth();

    int getHeight();
}
