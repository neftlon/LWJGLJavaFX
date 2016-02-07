package de.sgtrumbi.opengl_implementation.texturing;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi.opengl_implementation
 */
public class MeshFactory {

    private static List<Mesh> meshs = new ArrayList<>();

    private MeshFactory() {
    }

    public static Mesh create(float[] vertices, float[] textureCoordinates) {
        // Create the vertex array object
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // Create the float buffer for the vertices
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        // Bind the buffer data to the buffer
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        // Create the float buffer for the texture coordinates
        FloatBuffer textureCoordinateBuffer = BufferUtils.createFloatBuffer(textureCoordinates.length);
        textureCoordinateBuffer.put(textureCoordinates);
        textureCoordinateBuffer.flip();

        // Bind the buffer data to the buffer
        int tcb = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tcb);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureCoordinateBuffer, GL15.GL_STATIC_DRAW);

        // Unbind the buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Unbind the vertex array
        GL30.glBindVertexArray(0);

        Mesh mesh = new IMesh(vao, vbo, tcb, vertices.length / 2);
        meshs.add(mesh);
        return mesh;
    }

    public static void cleanUp() {
        for (Mesh mesh : meshs)
            mesh.cleanUp();
    }

    private static class IMesh implements Mesh {

        private final int vao;
        private final int vbo, tcb;
        private final int vertexCount;

        public IMesh(int vao, int vbo, int tcb, int vertexCount) {
            this.vao = vao;
            this.vbo = vbo;
            this.tcb = tcb;
            this.vertexCount = vertexCount;
        }

        @Override
        public void bind() {
            // Bind the vertex array
            GL30.glBindVertexArray(vao);

            // Enable the vertex attribute array for the vertices
            GL20.glEnableVertexAttribArray(0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

            // Enable the vertex attribute array for the texture coordinates
            GL20.glEnableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tcb);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        }

        @Override
        public void draw() {
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vertexCount);
        }

        @Override
        public void unbind() {
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);

            GL30.glBindVertexArray(0);
        }

        @Override
        public void cleanUp() {
            GL15.glDeleteBuffers(vbo);
            GL15.glDeleteBuffers(tcb);

            GL30.glDeleteVertexArrays(vao);
        }
    }
}
