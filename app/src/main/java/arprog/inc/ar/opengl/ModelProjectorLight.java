package arprog.inc.ar.opengl;

import android.opengl.GLES20;

import arprog.inc.ar.arprog.R;
import java.util.Random;

/**
 * Created by user on 6/14/17.
 */

public class ModelProjectorLight extends Model {
    static ShaderHelper.Shader shader;

    public ModelProjectorLight() {
        super(false, false);

        if (shader == null) {
            shader = RenderData.getShader("projector");
        }
    }

    float colorR, colorG, colorB;

    public void setColor(float r, float g, float b) {
        colorR = r;
        colorG = g;
        colorB = b;
    }

    public void update() {
        clear();

        Random random = new Random(67671);

        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 10 - 5;
            float z = random.nextFloat() * 10 - 5;

            float h = 0.6f + random.nextFloat() * 0.6f;
            float d = 0.2f + random.nextFloat() * 0.8f;
            float angle = random.nextFloat() * 100f + (float) Math.sin((System.currentTimeMillis() % (62820)) * 0.0001f) * (.5f + random.nextFloat());

            color4f(colorR, colorG, colorB, 0.2f);
            vertex3f(0, 0, -1f);
            color4f(colorR, colorG, colorB, 0);
            vertex3f((float) Math.sin(angle) * d, (float) Math.cos(angle) * d, h);
            color4f(colorR, colorG, colorB, 0);
            angle += .8f + random.nextFloat();
            vertex3f((float) Math.sin(angle) * d, (float) Math.cos(angle) * d, h);

        }

        compile();
    }

    public void draw(MVPMatrix matrix) {
        update();
        GLES20.glDepthMask(false);
        super.draw(shader, matrix);
        GLES20.glDepthMask(true);
    }

}
