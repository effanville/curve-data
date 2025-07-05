package com.effanville.curvedata.IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import com.effanville.curvedata.BucketType;

/**
 * Class to generate the sample generic_HK curve data. This uses the HK exchange sessions to
 * generate sample data, where we provide approximate volumes for the auction sessions, and evenly
 * distribute volume in the morning and afternoon sessions.
 * <p>
 * This is given to provide a well defined volume curve, if a little idealistic. In reality the
 * volume would vary much more from bucket to bucket.
 */
public class SampleCurveGenerator {
    public void generateCurve(String filePath) {
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter myWriter;
        try {
            myWriter = new FileWriter(filePath);
            writeHeader(myWriter);
            writeOpenAuction(myWriter, 8);
            writeContTrading(myWriter, LocalTime.parse("09:30"), LocalTime.parse("12:00"), 35);
            writeIntradayClose(myWriter, LocalTime.parse("12:00"), LocalTime.parse("13:00"));

            writeContTrading(myWriter, LocalTime.parse("13:00"), LocalTime.parse("16:00"), 47);
            writeCloseAuction(myWriter, LocalTime.parse("16:00"), LocalTime.parse("16:10"), 10);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(FileWriter writer) throws IOException {
        writer.write("StartTime,EndTime,VolPercent,BucketType");
        writer.write(System.lineSeparator());
    }

    private void writeOpenAuction(FileWriter writer, double openAuctionVol) throws IOException {
        writer.write("00:00,09:00,0," + BucketType.MARKET_CLOSED);
        writer.write(System.lineSeparator());
        writer.write("09:00,09:30," + openAuctionVol + "," + BucketType.OPEN_AUCTION);
        writer.write(System.lineSeparator());
    }

    private void writeContTrading(FileWriter writer, LocalTime start, LocalTime end,
            double totalVolume) throws IOException {
        Duration time = Duration.between(start, end);
        // number of 5 min buckets
        long numberBuckets = time.getSeconds() / 300;
        for (int bucketIndex = 0; bucketIndex < numberBuckets; bucketIndex++) {
            double volume = totalVolume / numberBuckets;
            LocalTime bucketStart = start.plusSeconds(bucketIndex * 300);
            LocalTime bucketEnd = start.plusSeconds(300 + bucketIndex * 300);
            writer.write(
                    bucketStart + "," + bucketEnd + "," + volume + "," + BucketType.CONT_TRADING);
            writer.write(System.lineSeparator());
        }
    }

    private void writeIntradayClose(FileWriter writer, LocalTime start, LocalTime end)
            throws IOException {
        writer.write(start + "," + end + ",0," + BucketType.INTRADAY_CLOSE);
        writer.write(System.lineSeparator());
    }

    private void writeCloseAuction(FileWriter writer, LocalTime start, LocalTime end,
            double closeAuctionVol) throws IOException {
        writer.write(start + "," + end + "," + closeAuctionVol + "," + BucketType.CLOSE_AUCTION);
        writer.write(System.lineSeparator());
        writer.write(end + ",23:59,0," + BucketType.CLOSE_AUCTION);
        writer.write(System.lineSeparator());
    }
}
