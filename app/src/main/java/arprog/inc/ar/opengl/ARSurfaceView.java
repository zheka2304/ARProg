package arprog.inc.ar.opengl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import arprog.inc.ar.arprog.IMarkerHandler;

/**
 * Created by zheka on 12.06.2017.
 */

public class ARSurfaceView extends GLSurfaceView {

    private ARRenderer renderer;

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     */
    public ARSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        renderer = new ARRenderer();
        setRenderer(renderer);
    }

    public ARRenderer getRenderer() {
        return this.renderer;
    }
}
