package de.sgtrumbi.opengl_implementation.texturing;

import java.nio.IntBuffer;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.opengl_implementation.texturing
 */
public interface PixelGetter {

    boolean getPixels(IntBuffer buffer, int width, int height);
}
