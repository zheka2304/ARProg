package arprog.inc.ar.opengl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import arprog.inc.ar.arprog.MainActivity;
import arprog.inc.ar.arprog.R;

/**
 * Created by zheka on 13.06.2017.
 */

public class TextureHelper {
    private static final int MAX_TEXTURE_COUNT = 256;

    private static boolean isLoaded = false;
    private static int[] textureIds = new int[MAX_TEXTURE_COUNT];
    private static int currentId = 0;

    public static class Texture {
        private int id;

        public Texture(int id) {
            this.id = id;
        }

        public void use(int handle) {
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
            GLES20.glUniform1i(handle, 0);
        }
    }

    public static Texture loadTexture(int res) {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        if (!isLoaded) {
            isLoaded = true;
            GLES20.glGenTextures(MAX_TEXTURE_COUNT, textureIds, 0);
            currentId = 0;
        }

        int id = textureIds[currentId++];
        if (id != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.current.getResources(), res, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            return new Texture(id);
        }
        else {
            System.out.println("ARuco: texture failed to load!");
            return null;
        }
    }
}
