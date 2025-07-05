package com.effanville.curvedata.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import com.effanville.curvedata.BucketType;
import com.effanville.curvedata.Curve;
import com.effanville.curvedata.CurveBucket;
import com.effanville.curvedata.CurveData;

/**
 * Class that enables reading curve data from a csv file. The csv format should be
 * <p>
 * <code>StartTime,EndTime,Volume,BucketType</code>
 * <p>
 * where times are in the form HH:mm or HH:mm:ss
 * <p>
 * and where the bucketType is parsable by the BucketType enum specified in code
 */
public class CurveCsvReader {
    /**
     * Loads a curve from a file specified. Returns null if the file doesnt exist 
     * or there is an error in loading the file.
     * @param symbol The symbol for the curve to be loaded
     * @param filePath The path to find the csv file to load.
     * @return
     */
    public Curve readCurve(String symbol, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Curve curve = CurveData.Of(symbol);
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("StartTime")) {
                    // header line should be ignored
                    continue;
                }
                CurveBucket bucket = ReadBucket(line);
                if (bucket == null)
                    continue;
                curve.addBucket(bucket);
            }
            return curve;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private CurveBucket ReadBucket(String line)
            throws DateTimeParseException, NumberFormatException, IllegalArgumentException {
        String[] parts = line.split(",");

        if (parts.length < 4)
            return null;
        LocalTime startTime = LocalTime.parse(parts[0]);
        LocalTime endTime = LocalTime.parse(parts[1]);
        double volume = Double.valueOf(parts[2]);
        BucketType bucketType = BucketType.valueOf(parts[3]);

        CurveBucket bucket = CurveBucket.Of(startTime, endTime, volume, bucketType);
        return bucket;
    }
}
