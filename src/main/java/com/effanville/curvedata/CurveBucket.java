package com.effanville.curvedata;

import java.time.LocalTime;

/**
 * A bucket of volume determining the times between which the volume will be observed, and the
 * session of the trading that this bucket corresponds to.
 */
public class CurveBucket {
    private LocalTime StartTime;
    private LocalTime EndTime;
    private double PercentDayVolume;
    private BucketType Type;

    private CurveBucket(LocalTime startTime, LocalTime endTime, double percentDayVolume,
            BucketType bucketType) {
        StartTime = startTime;
        EndTime = endTime;
        PercentDayVolume = percentDayVolume;
        Type = bucketType;
    }

    public static CurveBucket Of(LocalTime startTime, LocalTime endTime, double percentDayVolume,
            BucketType bucketType) {
        return new CurveBucket(startTime, endTime, percentDayVolume, bucketType);
    }

    public LocalTime getStartTime() {
        return StartTime;
    }

    public LocalTime getEndTime() {
        return EndTime;
    }

    public double getPercentDayVolume() {
        return PercentDayVolume;
    }

    public String getBucketType() {
        return Type.name();
    }

    public Boolean isValid() {
        if (PercentDayVolume < 0)
            return false;

        if(StartTime.isAfter(EndTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Start=" + StartTime + ", End=" + EndTime + ", Vol=" + PercentDayVolume + ", Type="
                + Type;
    }
}

