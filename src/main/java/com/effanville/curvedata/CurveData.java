package com.effanville.curvedata;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a Curve
 */
public class CurveData implements Curve {
    private String Symbol;
    private List<CurveBucket> Buckets = new ArrayList<CurveBucket>();

    private CurveData(String symbol) {
        Symbol = symbol;
    }

    public static CurveData Of(String symbol) {
        return new CurveData(symbol);
    }

    @Override
    public String getSymbol() {
        return Symbol;
    }

    @Override
    public int numberBuckets() {
        return Buckets.size();
    }

    public LocalTime getStartTime() {
        return Buckets.getFirst().getStartTime();
    }

    @Override
    public void addBucket(CurveBucket bucket) {
        Buckets.add(bucket);
        Buckets.sort(new CurveBucketStartTimeComparator());
    }

    /**
     * This provides some simple validation that is applicable for all curves,
     * namely that the
     * volume in each bucket is +ve, no time is in multiple buckets, and that there
     * is exactly 100%
     * of volume over the curve. Given an arbitrary symbol, there is not much more
     * one can validate.
     * <p>
     * We could go further and validate that the various bucket types were are
     * correct points, i.e.
     * that open auction was at the start, and close auction was the last positive
     * bucket, but we
     * leave that to future work.
     * <p>
     * If there was more information on the specific symbol, i.e. what exchange it
     * was listed on,
     * then one could further validate that the bucket types aligned with the
     * relevant sessions,
     * i.e. that one did not observe open auction volume in continuous trading
     * (unless that was
     * expected, e.g. TSE), or that there wasn't continuous trading volume in
     * intraday closes.
     * <p>
     * If one further knew that this was a liquid symbol, then one could further
     * verify that the
     * curve was "smooth" in some sense, possibly that the deviation in % between
     * continuous trading
     * buckets was not over a threshold too often.
     */
    @Override
    public Boolean isValid() {
        if (doBucketsOverlap())
            return false;

        for (int index = 0; index < Buckets.size(); index++) {
            CurveBucket bucket = Buckets.get(index);
            if (!bucket.isValid())
                return false;
        }

        if (!doesVolumeSumTo100())
            return false;

        return true;
    }

    /**
     * Checks if the curve has overlapping buckets
     */
    public Boolean doBucketsOverlap() {
        int length = Buckets.size();
        if (length < 2) {
            return false;
        }

        CurveBucket previousBucket = Buckets.get(0);
        for (int index = 1; index < length; index++) {
            CurveBucket bucket = Buckets.get(index);
            if (previousBucket.getEndTime().isAfter(bucket.getStartTime())) {
                return true;
            }

            previousBucket = bucket;
        }

        return false;
    }

    /**
     * Total volume in the curve should sum to 100
     */
    public Boolean doesVolumeSumTo100() {
        double totalVolume = 0;
        for (int index = 0; index < Buckets.size(); index++) {
            CurveBucket bucket = Buckets.get(index);
            totalVolume += bucket.getPercentDayVolume();
        }

        return Math.abs(totalVolume - 100) < 1e-8;
    }

    @Override
    public double getVolume(LocalTime start, LocalTime end) {
        if (end.isBefore(start))
            return 0.0;

        if (end.isBefore(Buckets.getFirst().getStartTime()))
            return 0.0;

        if (start.isAfter(Buckets.getLast().getEndTime()))
            return 0.0;

        int length = Buckets.size();
        double startVolume = 0;
        double endVolume = 0;

        // Iterate through buckets, and record the volume up
        // until each time required.
        for (int index = 0; index < length; index++) {
            CurveBucket bucket = Buckets.get(index);

            startVolume = AddBucketVolume(start, bucket, startVolume);
            endVolume = AddBucketVolume(end, bucket, endVolume);
        }

        // volume between the two times is the difference of the two cumulative
        // volumes.
        return endVolume - startVolume;
    }

    @Override
    public double getRelativeVolume(LocalTime start, LocalTime end, LocalTime time) {
        if (end.isBefore(start))
            return 0.0;

        if (end.isBefore(Buckets.getFirst().getStartTime()))
            return 0.0;

        if (start.isAfter(Buckets.getLast().getEndTime()))
            return 0.0;

        if (time.isBefore(start))
            return 0.0;

        if (time.isAfter(end))
            return 0.0;

        int length = Buckets.size();
        double startVolume = 0;
        double endVolume = 0;
        double timeVolume = 0;
        for (int index = 0; index < length; index++) {
            CurveBucket bucket = Buckets.get(index);
            startVolume = AddBucketVolume(start, bucket, startVolume);
            timeVolume = AddBucketVolume(time, bucket, timeVolume);
            endVolume = AddBucketVolume(end, bucket, endVolume);
        }

        return 100 * (timeVolume - startVolume) / (endVolume - startVolume);
    }

    /**
     * Given a bucket we calculate the relative fraction of volume in the bucket up
     * until the time
     * given. The timeVolume is the already observed volume in the curve.
     */
    private double AddBucketVolume(LocalTime time, CurveBucket bucket, double timeVolume) {
        if (time.isAfter(bucket.getEndTime())) {
            timeVolume += bucket.getPercentDayVolume();
        } else if (time.isAfter(bucket.getStartTime())) {
            Duration numerator = Duration.between(bucket.getStartTime(), time);
            Duration denum = Duration.between(bucket.getStartTime(), bucket.getEndTime());
            double volumeTime = (double) numerator.getSeconds() / (double) denum.getSeconds();
            timeVolume += volumeTime * bucket.getPercentDayVolume();
        }

        return timeVolume;
    }

    @Override
    public String toString() {
        return "Symbol='" + Symbol + "', BucketCount=" + Buckets.size();
    }
}
