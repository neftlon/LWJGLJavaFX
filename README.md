# LWJGLJavaFX

## Description
LWJGLJavaFX shows how to implement JavaFX in a GLFW/LWJGL-window.
This repository is currently in progress!
Everything could change!
It is not a library (yet), it's just a small demo (yet)!

## How does this work?
JavaFX has a class called <code>EmbeddedWindow</code>, from which you can get the pixel data, 
using an <code>java.nio.IntBuffer</code>. In LWJGLs (LWJGL 3) OpenGL everything works with Buffers.
You can take the <code>IntBuffer</code> and draw it on a texture. Then you can draw this texture on
the GLFW window.

### Issues
- I see a black screen:
  - Error: You may not see anything, but a black texture
  - Fix: Enable the first texture unit using <code>GL13.glActiveTexture(GL13.GL_TEXTURE0);</code>. Also don't forget to load up
  a <code>1</code> to the shaders <code>sampler2D</code> uniform (in this fragment shader: <code>u_Texture</code>). Therefore, use
  <code>shader.setUniform1i("u_Texture", 0);</code> to upload the <code>Integer</code>
- I only see the JavaFX gui
  - Error: You just see the JavaFX gui, and not the LWJGL code
  - Fix: You have to set the JavaFX <code>Scene</code> to have a transparent background.
