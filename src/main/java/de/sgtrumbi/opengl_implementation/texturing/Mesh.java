package de.sgtrumbi.opengl_implementation.texturing;

import de.sgtrumbi.opengl_implementation.Bindable;
import de.sgtrumbi.opengl_implementation.Cleanable;

/**
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi.opengl_implementation
 */
public interface Mesh extends Bindable, Cleanable {

    /**
     * Used for drawing the currently bound vao/mesh
     *
     * (Mostly used:
     *   - GL11.glDrawArrays(...)
     *   - GL11.glDrawElements(...)
     * )
     */
    void draw();
}
