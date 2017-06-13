package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import org.opencv.core.Mat;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arprog.inc.ar.arprog.CameraActivity;
import arprog.inc.ar.arprog.IMarkerHandler;
import arprog.inc.ar.arprog.R;
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

    private MVPMatrix mvpMatrix = new MVPMatrix();

    ShaderHelper.Shader testShader;
    Model testModel;

    private void prepareTestData() {
        testShader = ShaderHelper.createShader(R.raw.default_vertex, R.raw.default_fragment);

        testModel = new Model(false, true);
        testModel.color4f(1, 0, 0, 1);
        testModel.uv2f(0, 0);
        testModel.vertex3f(-.5f, -.5f, 0);
        testModel.color4f(0, 1, 0, 1);
        testModel.uv2f(1, 0);
        testModel.vertex3f(.5f, -.5f, 0);
        testModel.color4f(0, 0, 1, 1);
        testModel.uv2f(1, 1);
        testModel.vertex3f(.5f, .5f, 0);
        testModel.color4f(1, 0, 0, 1);
        testModel.uv2f(0, 0);
        testModel.vertex3f(-.5f, -.5f, 0);
        testModel.color4f(0, 1, 0, 1);
        testModel.uv2f(0, 1);
        testModel.vertex3f(-.5f, .5f, 0);
        testModel.color4f(0, 0, 1, 1);
        testModel.uv2f(1, 1);
        testModel.vertex3f(.5f, .5f, 0);
        testModel.compile();

        testModel.setTexture(TextureHelper.loadTexture(R.drawable.ar_icon_contrast));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        prepareTestData();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mvpMatrix.frustum(width, height, .01f, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // init frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        try {
            for (int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(i);
                onMarkerRendered(marker);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
    }

    private void onMarkerRendered(Marker marker) {
        // switch to markers CS
        mvpMatrix.fromMarker(marker);

        // draw test testModel
        testModel.draw(testShader, mvpMatrix);
    }

}
