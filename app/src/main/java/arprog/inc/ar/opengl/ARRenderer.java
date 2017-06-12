package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import org.opencv.core.Mat;

import java.util.Collections;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arprog.inc.ar.arprog.CameraActivity;
import arprog.inc.ar.arprog.IMarkerHandler;
import es.ava.aruco.Marker;

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


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

}
