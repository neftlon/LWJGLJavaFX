package de.sgtrumbi.test;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.test
 */
public interface GLStage {

    void onKeyPress(int keyCode);

    void onKeyRelease(int keyCode);

    void onKeyType(char keyCode);

    void onMouseButtonPress(int button);

    void onMouseButtonRelease(int button);

    void onMouseMove(int x, int y);
}
