package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import org.opencv.core.Mat;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arprog.inc.ar.arprog.CameraActivity;
import arprog.inc.ar.arprog.IMarkerHandler;
import arprog.inc.ar.arprog.MainActivity;
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

    ModelProjectorLight projectorModel;

    private void prepareTestData() {
        testShader = ShaderHelper.createShader(R.raw.model_vertex, R.raw.model_fragment);

        testModel = ModelLoader.asObj(MainActivity.current.getResources().openRawResource(R.raw.cow), .1f);

        testModel.setTexture(TextureHelper.loadTexture(R.drawable.ar_icon_contrast));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        projectorModel = new ModelProjectorLight();
        projectorModel.setColor(.5f, .5f, 1f);

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
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // switch to markers CS
        mvpMatrix.fromMarker(marker);

        projectorModel.draw(mvpMatrix);

        mvpMatrix.translate(0, 0, .8f);
        testModel.draw(testShader, mvpMatrix);
    }

}
