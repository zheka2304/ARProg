package es.ava.aruco;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by zheka on 12.06.2017.
 */

public class MarkerRegistry {
    public static double[] MatToList(Mat input) {
        return new double[] {input.get(0, 0)[0], input.get(1, 0)[0], input.get(2, 0)[0]};
    }

    public static Mat ListToMat(double[] input) {
        Mat mat = new Mat(3, 1, CvType.CV_64FC1);
        mat.put(0, 0, input);
        return mat;
    }


    public static class MarkerData {
        public Mat Rvec, Tvec;
        //public boolean hadData = false;

        public MarkerData (Marker marker) {
            Rvec = marker.Rvec;
            Tvec = marker.Tvec;
        }

        public void setTo (Marker marker) {
            Mat RvecCpy = new Mat();
            Mat TvecCpy = new Mat();
            Rvec.copyTo(RvecCpy);
            Tvec.copyTo(TvecCpy);
            marker.Rvec = RvecCpy;
            marker.Tvec = TvecCpy;
        }

        public Mat getStabilizedRVec(Mat newRvec) {
            double[] newVec = MatToList(newRvec);
            double[] oldVec = MatToList(Rvec);

            double t = 0.25;
            double d = Math.max(Math.max(Math.abs(oldVec[0] - newVec[0]), Math.abs(oldVec[1] - newVec[1])), Math.abs(oldVec[2] - newVec[2]));
            double k = d > t ? 0.9 : 0.3;
            return ListToMat(new double[] {
                    newVec[0] * k + oldVec[0] * (1 - k),
                    newVec[1] * k + oldVec[1] * (1 - k),
                    newVec[2] * k + oldVec[2] * (1 - k),
            });
        }
    }

    private static HashMap<Integer, MarkerData> markerData = new HashMap<Integer, MarkerData>();
    private static HashMap<Integer, Boolean> markerLock = new HashMap<Integer, Boolean>();

    /**
     * cleans marker data lock
     */
    public static void startDetection() {
        markerLock.clear();
    }

    /**
     * cleans marker data for marker ids, that was not detected, or was detected twice
    */
    public static void endDetection() {
        Set<Integer> keys = markerData.keySet();
        try {
            for (int id : keys) {
                if (!markerLock.containsKey(id))
                    markerData.remove(id);
                else if (markerLock.get(id))
                    markerData.remove(id);
            }
        } catch (ConcurrentModificationException e) {
            markerData.clear();
        }
    }

    public static void addMarker (Marker marker) {
        if (markerLock.containsKey(marker.id)) {
            markerLock.put(marker.id, true);
            return;
        }
        else {
            markerLock.put(marker.id, false);

            MarkerData data = new MarkerData(marker);
            markerData.put(marker.id, data);
        }
    }

    public static MarkerData getMarkerData (Marker marker) {
        if (markerLock.containsKey(marker.id) && markerLock.get(marker.id)) {
            return null;
        }
        else {
            if (markerData.containsKey(marker.id))
                return markerData.get(marker.id);
            else
                return null;
        }
    }
}
