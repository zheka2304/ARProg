package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.provider.Settings;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arprog.inc.ar.arprog.CameraActivity;
import arprog.inc.ar.arprog.IMarkerHandler;
import arprog.inc.ar.arprog.R;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerRegistry;

/**
 * Created by zheka on 12.06.2017.
 */

public class ARRenderer implements Renderer, IMarkerHandler{
    private Vector<Marker> markers = new Vector<Marker>();
    @Override
    public void onMarkersDetected(Vector<Marker> detectedMarkers, Mat inputFrame, CameraActivity currentActivity) {
        markers.clear();
        for (Marker marker : detectedMarkers)
            markers.add(marker);
    }

    @Override
    public void onNothingDetected(Mat inputFrame, CameraActivity currentActivity) {
        markers.clear();
    }

    ShaderHelper.Shader shader;
    Model model;
    MVPMatrix matrix;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        shader = ShaderHelper.createShader(R.raw.default_vertex, R.raw.default_fragment);
        model = new Model(false, false);
        model.color4f(1, 0, 0, 1);
        model.vertex3f(-.5f, -.5f, 0);
        model.color4f(0, 1, 0, 1);
        model.vertex3f(.5f, -.5f, 0);
        model.color4f(0, 0, 1, 1);
        model.vertex3f(.5f, .5f, 0);
        model.color4f(1, 0, 0, 1);
        model.vertex3f(-.5f, -.5f, 0);
        model.color4f(0, 1, 0, 1);
        model.vertex3f(-.5f, .5f, 0);
        model.color4f(0, 0, 1, 1);
        model.vertex3f(.5f, .5f, 0);

        model.compile();
        matrix = new MVPMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        matrix.frustum(width, height, 1, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // test marker render
        matrix.lookAt(0, 0, 0, 0, 0, 1, 0, 1, 0);
        if (markers.size() > 0) {
            Marker marker = markers.get(0);
            matrix.fromMarker(marker);
        }
        else {
            matrix.identity();
            matrix.translate(0, 0, 1);
        }

        model.draw(shader, matrix);
    }

}
