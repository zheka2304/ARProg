package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Set;
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
    private HashMap<Integer, Marker> markersToRender = new HashMap<Integer, Marker>();

    @Override
    public void onMarkersDetected(Vector<Marker> detectedMarkers, Mat inputFrame, CameraActivity currentActivity) {
        for (Marker marker : detectedMarkers) {
            markersToRender.put(marker.getMarkerId(), marker);
        }
    }

    @Override
    public void onNothingDetected(Mat inputFrame, CameraActivity currentActivity) {

    }

    private MVPMatrix mvpMatrix = new MVPMatrix();

    ShaderHelper.Shader testShader;
    Model testModel;

    ModelProjectorLight projectorModel;

    private void prepareTestData() {
<<<<<<< HEAD
        testShader = ShaderHelper.createShader(R.raw.default_vertex, R.raw.default_fragment);

//        testModel = new Model(false, true);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(0, 0);
//        testModel.vertex3f(-.5f, -.5f, 0);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(1, 0);
//        testModel.vertex3f(.5f, -.5f, 0);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(1, 1);
//        testModel.vertex3f(.5f, .5f, 0);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(0, 0);
//        testModel.vertex3f(-.5f, -.5f, 0);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(0, 1);
//        testModel.vertex3f(-.5f, .5f, 0);
//        testModel.color4f(1, 1, 1, 1);
//        testModel.uv2f(1, 1);
//        testModel.vertex3f(.5f, .5f, 0);
//        testModel.compile();

        testModel = ModelLoader.asObj(MainActivity.current.getResources().openRawResource(R.raw.cow));

        testModel.setTexture(TextureHelper.loadTexture(R.drawable.ar_icon_contrast));
=======
        testShader = RenderData.getShader("model");
        testModel = RenderData.getModel("cow.obj");
        //testModel.setTexture(RenderData.getTexture("model_texture_owl"));
>>>>>>> c747b9ce4c9e8d2b38997a6d0394c68dd1af01b8
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        RenderData.prepareAllAssets();

        projectorModel = new ModelProjectorLight();
        projectorModel.setColor(.5f, .5f, 1f);

        prepareTestData();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mvpMatrix.frustum(width, height, .01f, 100);
    }

    public static final long ERROR_TIME_MILLIS = 100;

    @Override
    public void onDrawFrame(GL10 gl) {
        // init frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        try {
            long currentTime = System.currentTimeMillis();
            Set<Integer> markerIds = markersToRender.keySet();

            for (int id : markerIds) {
                Marker marker = markersToRender.get(id);
                if (marker.updatedTime + ERROR_TIME_MILLIS < currentTime) {
                    markersToRender.remove(id);
                }
                else
                    onMarkerRendered(marker);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMarkerRendered(Marker marker) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // switch to markers CS
        mvpMatrix.fromMarker(marker);

<<<<<<< HEAD
        // draw test testModel
        testModel.draw(testShader, mvpMatrix);

=======
        projectorModel.draw(mvpMatrix);

        mvpMatrix.translate(0, 0, .8f);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ZERO);
        testModel.draw(testShader, mvpMatrix);
>>>>>>> c747b9ce4c9e8d2b38997a6d0394c68dd1af01b8
    }

}
