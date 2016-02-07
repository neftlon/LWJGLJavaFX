package de.sgtrumbi.opengl_implementation.texturing;

import de.sgtrumbi.opengl_implementation.Bindable;
import de.sgtrumbi.opengl_implementation.Cleanable;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.opengl_implementation
 */
public interface Texture extends Bindable, Cleanable {

    int getHandle();
}
