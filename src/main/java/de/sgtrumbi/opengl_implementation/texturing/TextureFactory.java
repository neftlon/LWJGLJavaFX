package de.sgtrumbi.opengl_implementation.texturing;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes (on 07.02.2016)
 * @see de.sgtrumbi.opengl_implementation.texturing
 */
public class TextureFactory {

    private static List<Texture> textures = new ArrayList<>();

    private TextureFactory() {
    }

    public static FrameBufferTexture createFrameBufferTexture(PixelGetter pixelGetter, int width, int height) {
        int handle = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                (ByteBuffer) null);

        // Set up the filters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        final int[] swizzleMask = {GL11.GL_RED, GL11.GL_GREEN, GL11.GL_BLUE, GL11.GL_ALPHA};
        IntBuffer swizzleMaskBuffer = BufferUtils.createIntBuffer(swizzleMask.length);
        swizzleMaskBuffer.put(swizzleMask);
        swizzleMaskBuffer.rewind();

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);

        return new IFrameBufferTexture(handle, width, height, pixelBuffer, pixelGetter);
    }

    public static void cleanUp() {
        for (Texture texture : textures)
            texture.cleanUp();
    }

    private static class IFrameBufferTexture implements FrameBufferTexture {

        private final int handle;
        private int width, height;
        private ByteBuffer byteBuffer;
        private PixelGetter pixelGetter;

        public IFrameBufferTexture(int handle, int width, int height, ByteBuffer byteBuffer, PixelGetter pixelGetter) {
            this.handle = handle;
            this.width = width;
            this.height = height;
            this.byteBuffer = byteBuffer;
            this.pixelGetter = pixelGetter;
        }

        @Override
        public int getHandle() {
            return handle;
        }

        @Override
        public void update() {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);
            byteBuffer.rewind();
            pixelGetter.getPixels(byteBuffer.asIntBuffer(), width, height);
            byteBuffer.rewind();

            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        }

        @Override
        public void resize(int width, int height) {
            this.width = width;
            this.height = height;

            // If the window was resized, the current byte buffer
            // (The pixel buffer) would be to small for the pixels.
            // -> Create a new buffer from the new size
            byteBuffer = ByteBuffer.allocateDirect(width * height * 4);

            // Make the texture the same size as the new window.
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void bind() {
            // Bind the texture to the texture unit
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);
        }

        @Override
        public void unbind() {
            // Unbind the texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }

        @Override
        public void cleanUp() {
            // Delete the texture
            GL11.glDeleteTextures(handle);
        }
    }
}
