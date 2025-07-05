package com.effanville.curvedata;

import static org.junit.Assert.assertEquals;
import java.time.LocalTime;
import org.junit.Test;

public class CurveBucketTest {
    @Test
    public void canInstantiateAndReturnToString() {
        CurveBucket bucket = CurveBucket.Of(LocalTime.parse("09:00"), LocalTime.parse("09:30"),
                25.6, BucketType.CONT_TRADING);
        String toString = bucket.toString();
        assertEquals("Start=09:00, End=09:30, Vol=25.6, Type=CONT_TRADING", toString);
    }

    @Test
    public void bucketWithPositiveVolumeIsValidTest(){
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        CurveBucket bucket = CurveBucket.Of(start, end, 5, BucketType.OPEN_AUCTION);
        assertEquals(true, bucket.isValid());
    }

    @Test
    public void bucketWithIncorrectTimesIsNotValidTest(){
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        CurveBucket bucket = CurveBucket.Of(end, start, -5, BucketType.OPEN_AUCTION);
        assertEquals(false, bucket.isValid());
    }

    @Test
    public void bucketWithNegativeVolumeIsNotValidTest(){
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        CurveBucket bucket = CurveBucket.Of(start, end, -5, BucketType.OPEN_AUCTION);
        assertEquals(false, bucket.isValid());
    }
}
