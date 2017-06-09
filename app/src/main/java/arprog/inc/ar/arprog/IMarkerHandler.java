package arprog.inc.ar.arprog;

import android.graphics.Camera;

import org.opencv.core.Mat;

import java.util.Vector;

import es.ava.aruco.Marker;

/**
 * Created by zheka on 09.06.2017.
 */

public interface IMarkerHandler {

    void onMarkersDetected (Vector<Marker> detectedMarkers, Mat inputFrame, CameraActivity currentActivity);

    void onNothingDetected (Mat inputFrame, CameraActivity currentActivity);
}
