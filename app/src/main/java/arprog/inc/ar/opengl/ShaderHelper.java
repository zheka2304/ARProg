package arprog.inc.ar.opengl;

import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import arprog.inc.ar.arprog.MainActivity;

/**
 * Created by zheka on 12.06.2017.
 */

public class ShaderHelper {
    /**
     * pair of fragment and vertex shaders, loaded from res/raw/ and compiled
    */
    public static class Shader {
        private int vertexShader, fragmentShader;
        private int vertexResource, fragmentResource;
        private int program;

        public IAttributeNames attributeNames;

        public Shader(int vertexR, int fragmentR) {
            vertexResource = vertexR;
            fragmentResource = fragmentR;

            vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        }

        public boolean isLoaded = false;

        private String readResource(int id) {
            InputStream inputStream = MainActivity.current.getResources().openRawResource(id);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    result += line + '\n';
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         * loads and compiles testShader source
         */
        public void compile() {
            String vertexSource = readResource(vertexResource);
            String fragmentSource = readResource(fragmentResource);

            GLES20.glShaderSource(vertexShader, vertexSource);
            GLES20.glShaderSource(fragmentShader, fragmentSource);

            GLES20.glCompileShader(vertexShader);
            GLES20.glCompileShader(fragmentShader);

            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);

            isLoaded = true;
        }

        /**
         * uses current testShader
        */
        public void use() {
            if (isLoaded) {
                GLES20.glUseProgram(program);
            }
            else {
                System.out.println("SHADER ERROR: SHADER IS NOT COMPILED.");
            }
        }

        public int getProgram() {
            return program;
        }

        public int getAttribLocation(String name) {
            return GLES20.glGetAttribLocation(program, name);
        }

        public int getUniformLocation(String name) {
            return GLES20.glGetUniformLocation(program, name);
        }
    }

    public interface IAttributeNames {
        public String getPositionName();
        public String getColorName();
        public String getNormalName();
        public String getUVName();

        public String getMatrixName();
        public String getLocalMatrixName();
        public String getTextureName();
    }

    public static Shader createShader(int vertex, int fragment, IAttributeNames names) {
        Shader shader = new Shader(vertex, fragment);
        shader.compile();
        shader.attributeNames = names;
        return shader;
    }

    public static Shader createShader(int vertex, int fragment) {
        return createShader(vertex, fragment, new IAttributeNames() {
            @Override
            public String getPositionName() {
                return "vPosition";
            }

            @Override
            public String getColorName() {
                return "vColor";
            }

            @Override
            public String getNormalName() {
                return "vNormal";
            }

            @Override
            public String getUVName() {
                return "vUV";
            }

            @Override
            public String getMatrixName() {
                return "mvpMatrix";
            }

            @Override
            public String getLocalMatrixName() {
                return "localMatrix";
            }

            @Override
            public String getTextureName() {
                return "TEXTURE";
            }
        });
    }
}
