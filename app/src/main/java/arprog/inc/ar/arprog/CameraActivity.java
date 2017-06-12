package arprog.inc.ar.arprog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.Vector;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerDetector;
import es.ava.aruco.exceptions.CPException;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView cameraView;

    private MarkerDetector markerDetector = new MarkerDetector();
    private Vector<Marker> detectedMarkers = new Vector<Marker>();

    private static IMarkerHandler markerHandler;
    private static CameraParameters cameraParams = new CameraParameters();
    private static float markerSize = 1f;
    private static int maxResolution = 480;

    public static void setMarkerSize(float size) {
        markerSize = size;
    }

    public static CameraParameters getCameraParams() {
        return cameraParams;
    }

    public static void setMarkerHandler(IMarkerHandler handler) {
        markerHandler = handler;
    }

    public static void setMaxResolutionDimension(int maxRes) {
        maxResolution = maxRes;
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    private void setMaxResolution() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int winHeight = metrics.heightPixels;
        int winWidth = metrics.widthPixels;

        int width, height;
        if (winWidth > winHeight) {
            width = maxResolution;
            height = maxResolution * winHeight / winWidth;
        }
        else {
            height = maxResolution;
            width = maxResolution * winWidth / winHeight;
        }

        cameraView.setMaxFrameSize(width, height);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        requestCameraPermission();

        cameraView = (JavaCameraView) findViewById(R.id.camera_view);
        cameraView.setCvCameraViewListener(this);
        cameraView.setVisibility(SurfaceView.VISIBLE);

        setMaxResolution();

        cameraView.enableView();
    }


    private Mat mFrame;

    private int sWidth, sHeight;

    /**
     * This method is invoked when camera preview has started. After this method is invoked
     * the frames will start to be delivered to client via the onCameraFrame() callback.
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {

        sWidth = width;
        sHeight = height;

        System.out.println("ARuco: Cam size: " + width + "x" + height);
    }

    /**
     * This method is invoked when camera preview has been stopped for some reason.
     * No frames will be delivered via onCameraFrame() callback after this method is called.
     */
    @Override
    public void onCameraViewStopped() {

    }

    /**
     * This method is invoked when delivery of the frame needs to be done.
     * The returned values - is a modified frame which needs to be displayed on the screen.
     * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
     *
     * @param inputFrame
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mFrame = inputFrame.rgba();

        float focal_length = sWidth;
        cameraParams.set(focal_length, focal_length, sWidth / 2f, sHeight / 2f);

        detectedMarkers.clear();
        markerDetector.detect(mFrame, detectedMarkers, cameraParams, markerSize);

        if (markerHandler != null)
            if (detectedMarkers.size() > 0)
                markerHandler.onMarkersDetected(detectedMarkers, mFrame, this);
            else
                markerHandler.onNothingDetected(mFrame, this);

        return mFrame;
    }
}
