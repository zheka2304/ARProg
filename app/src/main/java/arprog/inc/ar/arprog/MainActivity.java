package arprog.inc.ar.arprog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.Vector;

import es.ava.aruco.Marker;

public class MainActivity extends AppCompatActivity{

    static {
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

    private Activity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestCameraPermission();

        CameraActivity.setMarkerHandler(new IMarkerHandler() {
            @Override
            public void onMarkersDetected(Vector<Marker> detectedMarkers, Mat inputFrame, CameraActivity currentActivity) {
                for (Marker m:
                        detectedMarkers) {
                    m.draw(inputFrame, new Scalar(1, 255, 1, 1), 1, true);
                }
            }

            @Override
            public void onNothingDetected(Mat inputFrame, CameraActivity currentActivity) {

            }
        });

        self = this;
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, CameraActivity.class);
                startActivity(intent);
            }
        });
    }


}
