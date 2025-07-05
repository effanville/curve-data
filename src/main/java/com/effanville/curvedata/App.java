package com.effanville.curvedata;

import java.io.File;
import java.time.LocalTime;
import com.effanville.curvedata.IO.CurveCsvReader;

public class App {
    /**
     * Example routine to read a curve from a csv file and then validate the curve, with retrieval
     * of curve data to demonstrate the curve is loaded.
     * <p>
     * The intention is to provide a sample implementation of reading the file and verifying
     * validity. In a more complex application, handling to deal with and invalid curve would be
     * provided.
     */
    public static void main(String[] args) {
        String filePath = "src/main/resources/Generic_HK.csv";
        File file = new File(filePath);

        CurveCsvReader reader = new CurveCsvReader();

        String absFile = file.getAbsolutePath();
        System.out.println("Reading curve from file: " + file);

        Curve curve = reader.readCurve("Generic HK", absFile);

        Boolean valid = curve.isValid();

        System.out.println("Curve validity: " + valid);

        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:30:00");
        double volume = curve.getVolume(start, end);
        System.out.println("Volume between 09:00 and 09:30 is: " + volume);
    }
}
