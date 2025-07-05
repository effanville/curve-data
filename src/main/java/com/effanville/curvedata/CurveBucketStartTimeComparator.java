package com.effanville.curvedata;

import java.util.Comparator;

/**
 * Compares two CurveBuckets by their start time.
 */
public class CurveBucketStartTimeComparator implements Comparator<CurveBucket> {
    @Override
    public int compare(CurveBucket o1, CurveBucket o2) {
        return o1.getStartTime().compareTo(o2.getStartTime());
    }
}
