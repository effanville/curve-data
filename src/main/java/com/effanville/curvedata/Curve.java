package com.effanville.curvedata;

import java.time.LocalTime;
import java.util.List;

/**
 * Representation of a volume curve, with methods to retrieve volume for different parts of the
 * curve, and basic validation methods.
 */
public interface Curve {
    /**
     * Returns the symbol this curve is associated to.
     * 
     * @return The symbol.
     */
    String getSymbol();

    /**
     * Returns the buckets in this Curve.
     * 
     * @return The list of the buckets.
     */
    List<CurveBucket> getBuckets();

    /**
     * Add a bucket into the buckets of the curve.
     * 
     * @param bucket The bucket to add to the curve
     */
    void addBucket(CurveBucket bucket);

    /**
     * Validates and checks if the curve is a valid curve.
     * 
     * @return Boolean determining if the curve is valid or not.
     */
    Boolean isValid();

    /**
     * Returns the volume between the two times.
     * 
     * @param start the starting instant to calculate volume from
     * @param end the ending instant to calculate volume until
     * @return the volume between the start and end times
     */
    double getVolume(LocalTime start, LocalTime end);

    /**
     * Returns the normalised volume at the time relative to the volume between the start and end
     * time.
     * 
     * @param start the starting instant to calculate volume from
     * @param end the ending instant to calculate volume until
     * @param time the time to calculate the relative volume
     * @return the volume between the start and end times
     */
    double getRelativeVolume(LocalTime start, LocalTime end, LocalTime time);
}
