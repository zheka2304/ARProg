package arprog.inc.ar.arprog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLES20;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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

    private static final int FILE_SELECT_CODE = 0;

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

        findViewById(R.id.button_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(current, HelpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.open_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        String name = getFileName(data.getData());

                        if (!name.endsWith(".obj")) {
                            Toast.makeText(this, "only .obj models are supported", Toast.LENGTH_LONG).show();
                            return;
                        }

                        System.out.println("file uri: " + data.getData());

                        RenderData.prepareModel("current", getContentResolver().openInputStream(data.getData()));
                        Toast.makeText(this, "model loaded successfully: " + name, Toast.LENGTH_LONG).show();

                        ((TextView) findViewById(R.id.current_model_name)).setText("current model:\n" + name);
                    } catch (Exception e) {
                        Toast.makeText(this, "failed to load model: " + e, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
