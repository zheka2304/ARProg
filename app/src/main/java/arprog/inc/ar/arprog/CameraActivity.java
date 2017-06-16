package arprog.inc.ar.arprog;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import arprog.inc.ar.opengl.ARSurfaceView;
import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerDetector;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView cameraView;
    private ARSurfaceView arSurfaceView;
    private FrameLayout frameLayout;

    private MarkerDetector markerDetector = new MarkerDetector();
    private Vector<Marker> detectedMarkers = new Vector<Marker>();

    private static List<IMarkerHandler> markerHandlers = new ArrayList<IMarkerHandler>();
    private static CameraParameters cameraParams = new CameraParameters();
    private static float markerSize = 1f;
    private static int maxResolution = 480;

    public static void setMarkerSize(float size) {
        markerSize = size;
    }

    public static CameraParameters getCameraParams() {
        return cameraParams;
    }

    public static void addMarkerHandler(IMarkerHandler handler) {
        markerHandlers.add(handler);
    }

    public static void removeAllMarkerHandlers() {
        markerHandlers.clear();
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

        // find by ids
        frameLayout = (FrameLayout) findViewById(R.id.camera_layout);
        cameraView = (JavaCameraView) findViewById(R.id.camera_view);

        // setup camera view
        cameraView.setCvCameraViewListener(this);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        setMaxResolution();
        cameraView.enableView();
    }

    private boolean reversedLandscape = true;
    private void setupARSurfaceView() {
        // calculate view size by camera preview size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        double widthRatio = width / (double) sWidth;
        double heightRatio = height / (double) sHeight;

        int wWidth = 0, wHeight = 0;

        if (sWidth * heightRatio > width) {
            wWidth = (int) (sWidth * widthRatio);
            wHeight = (int) (sHeight * widthRatio);
        }
        else {
            wWidth = (int) (sWidth * heightRatio);
            wHeight = (int) (sHeight * heightRatio);
        }

        // setup AR (GL) SurfaceView
        // TODO: arSurfaceView must be sized and positioned exactly at camera preview to correct projection
        arSurfaceView = new ARSurfaceView(this);
        addMarkerHandler(arSurfaceView.getRenderer());

        arSurfaceView.setX((width - wWidth) / 2);
        arSurfaceView.setY((height - wHeight) / 2);
        arSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(wWidth, wHeight));

        LayoutInflater.from(this).inflate(R.layout.fab_rotate_button, frameLayout);
        frameLayout.addView(arSurfaceView);

        findViewById(R.id.rotate_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reversedLandscape = !reversedLandscape;
                setRequestedOrientation(reversedLandscape ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }


    private Mat mFrame;

    public static int sWidth, sHeight;

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

        float fov = CameraParameters.DEFAULT_FOV;
        float focal_length = (sHeight / 2) / (float) Math.tan(fov / 360.0 * Math.PI);

        cameraParams.set(focal_length, focal_length, sWidth / 2f, sHeight / 2f);
        System.out.println("ARuco: Cam size: " + width + "x" + height);

        // setup AR surface view
        setupARSurfaceView();
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

        detectedMarkers.clear();
        markerDetector.detect(mFrame, detectedMarkers, cameraParams, markerSize);

        for(IMarkerHandler handler : markerHandlers)
            if (detectedMarkers.size() > 0)
                handler.onMarkersDetected(detectedMarkers, mFrame, this);
            else
                handler.onNothingDetected(mFrame, this);

        return mFrame;
    }
}
