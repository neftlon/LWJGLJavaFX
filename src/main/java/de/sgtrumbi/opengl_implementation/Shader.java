package de.sgtrumbi.opengl_implementation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Johannes (on 06.02.2016)
 * @see de.sgtrumbi.opengl_implementation
 */
public class Shader implements Bindable {

    private Map<String, Integer> locationsCache = new HashMap<>();
    private int programHandle;

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        programHandle = Shader.createProgram(vertexShaderPath, fragmentShaderPath);
    }

    private static int createProgram(String vertexShaderPath, String fragmentShaderPath) {
        int vertexShaderHandle = createShader(GL20.GL_VERTEX_SHADER, vertexShaderPath);
        int fragmentShaderHandle = createShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderPath);

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShaderHandle);
        GL20.glAttachShader(program, fragmentShaderHandle);

        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.out.println("Failed to link shader program! Log: " + GL20.glGetProgramInfoLog(program));

            throw new IllegalStateException("Failed to link shader program.");
        }

        return program;
    }

    private static int createShader(int shaderType, String path) {
        int handle = GL20.glCreateShader(shaderType);

        try {
            String source = "";

            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNext()) {
                source += scanner.nextLine() + "\n";
            }
            scanner.close();

            GL20.glShaderSource(handle, source);
            GL20.glCompileShader(handle);

            if (GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.out.println("Failed to compile shader. Log: " + GL20.glGetShaderInfoLog(handle));

                throw new IllegalStateException("Failed to compile shader.");
            }

            return handle;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void bind() {
        GL20.glUseProgram(programHandle);
    }

    @Override
    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void setUniform1i(String uniformName, int i) {
        GL20.glUniform1i(getUniform(uniformName), i);
    }

    private int getUniform(String name) {
        if (locationsCache.containsKey(name))
            return locationsCache.get(name);

        int uniform = GL20.glGetUniformLocation(programHandle, name);
        if (uniform == -1)
            throw new IllegalArgumentException("Couldn't find a uniform named '" + name + "'!");

        locationsCache.put(name, uniform);

        return uniform;
    }
}
