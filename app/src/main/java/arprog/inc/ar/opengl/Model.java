package arprog.inc.ar.opengl;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by zheka on 13.06.2017.
 */

public class Model {
    public static final int BYTES_PER_FLOAT = 4;

    public static final int COORDS_PER_VERTEX = 3;
    public static final int COLORS_PER_VERTEX = 4;
    public static final int NORMAL_COORDS_PER_VERTEX = 3;
    public static final int UV_COORDS_PER_VERTEX = 2;

    boolean useNormals = false, useUVs = false;

    private ArrayList<Float> vertices, colors, normals, uvs;
    private FloatBuffer bVertices, bColors, bNormals, bUVs;
    private int vertexCount;

    public Model (boolean useNormals, boolean useUVs) {
        this.useNormals = useNormals;
        this.useUVs = useUVs;

        vertices = new ArrayList<Float>();
        colors = new ArrayList<Float>();

        if (useNormals)
            normals = new ArrayList<Float>();
        if (useUVs)
            uvs = new ArrayList<Float>();
    }

    public Model() {
        this(false, false);
    }

    /**
     * clears all array lists
     * it will not clear render buffers, so to clear testModel fully you need to call compile() after clear()
     */
    public void clear() {
        vertices.clear();
        colors.clear();

        if (useNormals)
            normals.clear();
        if (useUVs)
            uvs.clear();
    }

    /**
     * clones all data in array lists to buffers
    */
    public void compile() {
        // clear old data
        if (bVertices != null)
            bVertices.clear();
        if (bColors != null)
            bColors.clear();
        if (bNormals != null)
            bNormals.clear();
        if (bUVs != null)
            bUVs.clear();

        bVertices = toBuffer(vertices);
        bColors = toBuffer(colors);
        if (useNormals)
            bNormals = toBuffer(normals);
        if (useUVs)
            bUVs = toBuffer(uvs);

        vertexCount = vertices.size() / COORDS_PER_VERTEX;
    }

    /**
     * creates float buffer and fills it from array values
     */
    private FloatBuffer toBuffer(ArrayList<Float> data) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.size() * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.position(0);
        for (int i = 0; i < data.size(); i++) {
            buffer.put(data.get(i));
        }
        buffer.position(0);
        return buffer;
    }

    private TextureHelper.Texture currentTexture;
    public void setTexture(TextureHelper.Texture texture) {
        currentTexture = texture;
    }

    /**
     * renders testModel
     * @param shader testShader to use
     */
    public void draw(ShaderHelper.Shader shader, MVPMatrix matrix) {
        shader.use();

        int positionHandle = shader.getAttribLocation(shader.attributeNames.getPositionName());
        int colorHandle = shader.getAttribLocation(shader.attributeNames.getColorName());

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, COORDS_PER_VERTEX * BYTES_PER_FLOAT, bVertices);
        GLES20.glVertexAttribPointer(colorHandle, COLORS_PER_VERTEX, GLES20.GL_FLOAT, false, COLORS_PER_VERTEX * BYTES_PER_FLOAT, bColors);
        if (useNormals) {
            int normalHandle = shader.getAttribLocation(shader.attributeNames.getNormalName());
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glVertexAttribPointer(normalHandle, NORMAL_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, NORMAL_COORDS_PER_VERTEX * BYTES_PER_FLOAT, bNormals);
        }
        if (useUVs) {
            int uvHandle = shader.getAttribLocation(shader.attributeNames.getUVName());
            GLES20.glEnableVertexAttribArray(uvHandle);
            GLES20.glVertexAttribPointer(uvHandle, UV_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, UV_COORDS_PER_VERTEX * BYTES_PER_FLOAT, bUVs);
        }

        int textureHandle = shader.getUniformLocation(shader.attributeNames.getTextureName());
        if (currentTexture != null) {
            currentTexture.use(textureHandle);
        }
        else {
            RenderData.FLAT_WHITE.use(textureHandle);
        }

        int matrixHandle1 = shader.getUniformLocation(shader.attributeNames.getMatrixName());
        GLES20.glUniformMatrix4fv(matrixHandle1, 1, false, matrix.getMVP(), 0);
        int matrixHandle2 = shader.getUniformLocation(shader.attributeNames.getLocalMatrixName());
        GLES20.glUniformMatrix4fv(matrixHandle2, 1, false, matrix.getLocal(), 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }


    /* functions for fast hardcoded creation */
    public void vertex3f(float x, float y, float z) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
    }

    public void color4f(float r, float g, float b, float a) {
        colors.add(r);
        colors.add(g);
        colors.add(b);
        colors.add(a);
    }

    public void normal3f(float x, float y, float z) {
        if (useNormals) {
            normals.add(x);
            normals.add(y);
            normals.add(z);
        }
    }

    public void uv2f(float x, float y) {
        if (useUVs) {
            uvs.add(x);
            uvs.add(y);
        }
    }
}
