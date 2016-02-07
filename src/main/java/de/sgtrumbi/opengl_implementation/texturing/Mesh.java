package de.sgtrumbi.opengl_implementation.texturing;

import de.sgtrumbi.opengl_implementation.Bindable;
import de.sgtrumbi.opengl_implementation.Cleanable;

/**
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi.opengl_implementation
 */
public interface Mesh extends Bindable, Cleanable {

    void draw();
}
