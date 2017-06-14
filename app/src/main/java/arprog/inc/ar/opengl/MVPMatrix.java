package arprog.inc.ar.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerRegistry;

/**
 * Created by zheka on 13.06.2017.
 */

public class MVPMatrix {
    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] markerMatrix = new float[16];
    private final float[] localMatrix = new float[16];


    public void frustum(int width, int height, float near, float far) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        float fov = CameraParameters.DEFAULT_FOV;
        float top = (float) Math.tan(fov * Math.PI / 360.0f) * near;
        float bottom = -top;
        float left = ratio * bottom;
        float right = ratio * top;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void lookAt(float camX, float camY, float camZ, float targetX, float targetY, float targetZ, float upX, float upY, float upZ) {
        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, targetX, targetY, targetZ, upX, upY, upZ);
    }

    public void identity() {
        Matrix.setIdentityM(markerMatrix, 0);
        Matrix.setIdentityM(localMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(markerMatrix, 0, x, y, z);
    }

    public void translate2(float x, float y, float z) {
        Matrix.translateM(viewMatrix, 0, x, y, z);
    }

    public void fromMarker (Marker marker) {
        // load identity
        identity();
        // look towards z+
        lookAt(0, 0, 0, 0, 0, 1, 0, 1, 0);

        // get and fix rotation vector
        double[] r = MarkerRegistry.MatToList(marker.Rvec);
        r[0] = -r[0];
        r[1] = -r[1];
        Mat Rvec = MarkerRegistry.ListToMat(r);

        // calculate and apply rotation matrix
        Mat rotationMatrix = new Mat(3, 3, CvType.CV_64FC1);
        Calib3d.Rodrigues(Rvec, rotationMatrix);
        applyRotationMatrix(rotationMatrix);

        // translate
        translate2((float) -marker.Tvec.get(0, 0)[0],
                    (float) -marker.Tvec.get(1, 0)[0],
                    (float) marker.Tvec.get(2, 0)[0]);
    }

    public void applyRotationMatrix(Mat matrix) {
        markerMatrix[0] = (float) matrix.get(0, 0)[0];
        markerMatrix[1] = (float) matrix.get(1, 0)[0];
        markerMatrix[2] = (float) matrix.get(2, 0)[0];

        markerMatrix[4] = (float) matrix.get(0, 1)[0];
        markerMatrix[5] = (float) matrix.get(1, 1)[0];
        markerMatrix[6] = (float) matrix.get(2, 1)[0];

        markerMatrix[8] = (float) matrix.get(0, 2)[0];
        markerMatrix[9] = (float) matrix.get(1, 2)[0];
        markerMatrix[10] = (float) matrix.get(2, 2)[0];

    }

    public void rotate(float x, float y, float z) {
//        Matrix.rotateM(localMatrix, 0, x, 1, 0, 0);
//        Matrix.rotateM(localMatrix, 0, y, 0, 1, 0);
//        Matrix.rotateM(localMatrix, 0, z, 0, 0, 1);
        Matrix.setRotateEulerM(localMatrix, 0, x, y, z);
    }

    public float[] getMVP() {
        float[] result = new float[16];

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(result, 0, mvpMatrix, 0, markerMatrix, 0);

        return result;
    }

    public float[] getLocal() {
        return localMatrix;
    }
}
