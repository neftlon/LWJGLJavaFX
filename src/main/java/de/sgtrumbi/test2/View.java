package de.sgtrumbi.test2;

import javafx.scene.image.ImageView;

/**
 * @author Johannes (on 12.02.2016)
 * @see de.sgtrumbi.test2
 */
public interface View {

    void onUpdate();
    void onResize(int width, int height);
    void onClose();
    ImageView get();
}
