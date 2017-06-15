package arprog.inc.ar.arprog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLES20;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.Vector;

import arprog.inc.ar.opengl.RenderData;
import arprog.inc.ar.opengl.ShaderHelper;
import es.ava.aruco.Marker;

public class MainActivity extends AppCompatActivity{

    public static void prepareOpenCV() {
        System.out.println("OpenCV Loaded: " + OpenCVLoader.initDebug());
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

    public static Activity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        current = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_layout);

//        prepareOpenCV();
//        RenderData.prepareAllModels();

        setContentView(R.layout.activity_main);

        requestCameraPermission();

        CameraActivity.setMarkerSize(1);
        CameraActivity.addMarkerHandler(new IMarkerHandler() {
            @Override
            public void onMarkersDetected(Vector<Marker> detectedMarkers, Mat inputFrame, CameraActivity currentActivity) {
                for (Marker m:
                        detectedMarkers) {
                    m.draw(inputFrame, new Scalar(255, 1, 1, 1), 1, false);
                }
            }

            @Override
            public void onNothingDetected(Mat inputFrame, CameraActivity currentActivity) {

            }
        });

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(current, CameraActivity.class);
            startActivity(intent);
            }
        });

    }


}
