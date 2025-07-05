package com.effanville.curvedata;

import static org.junit.Assert.assertEquals;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CurveDataTest {
    @Test
    public void canInstantiateAndReturnToSTringTest() {
        Curve curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(end, end2, 25, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.CONT_TRADING));

        String toString = curve.toString();
        assertEquals("Symbol='1 HK', BucketCount=2", toString);
    }

    @Test
    public void areBucketsSortedTest() {
        Curve curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(end, end2, 25, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.CONT_TRADING));
        CurveBucket firstBucket = curve.getBuckets().get(0);
        assertEquals(start, firstBucket.getStartTime());
    }

    @Test
    public void doBucketsNotOverlapTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(end, end2, 25, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(start, end, 75, BucketType.OPEN_AUCTION));

        assertEquals(false, curve.doBucketsOverlap());
        assertEquals(true, curve.isValid());
    }

    @Test
    public void curveWithNegativeBucketVolumeIsNotValidTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(start, end, -5, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(end, end2, 105, BucketType.OPEN_AUCTION));

        assertEquals(false, curve.isValid());
    }

    @Test
    public void curveWithAllPositiveVolumeIsValidTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(end, end2, 95, BucketType.OPEN_AUCTION));

        assertEquals(true, curve.isValid());
    }

    @Test
    public void doesVolumeSumTo100ReturnsFalseTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(end, end2, 90, BucketType.OPEN_AUCTION));

        assertEquals(false, curve.doesVolumeSumTo100());
        assertEquals(false, curve.isValid());
    }

    @Test
    public void doesVolumeSumTo100ReturnsTrueTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.OPEN_AUCTION));
        curve.addBucket(CurveBucket.Of(end, end2, 95, BucketType.OPEN_AUCTION));

        assertEquals(true, curve.doesVolumeSumTo100());
        assertEquals(true, curve.isValid());
    }

    @Test
    public void doBucketsOverlapTest() {
        CurveData curve = CurveData.Of("1 HK");
        LocalTime start = LocalTime.parse("09:00:00");
        LocalTime end = LocalTime.parse("09:05:00");
        curve.addBucket(CurveBucket.Of(start, end, 5, BucketType.OPEN_AUCTION));

        LocalTime end2 = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(start, end2, 25, BucketType.OPEN_AUCTION));

        assertEquals(true, curve.doBucketsOverlap());
        assertEquals(false, curve.isValid());
    }

    private static Stream<Arguments> providerForGetVolume() {
        return Stream.of(
            Arguments.of(LocalTime.parse("09:00:00"),
                LocalTime.parse("11:00:00"), 100),

            Arguments.of(LocalTime.parse("09:05:00"),
                LocalTime.parse("10:10:00"), 85),

            Arguments.of(LocalTime.parse("09:07:30"),
                LocalTime.parse("10:10:00"), 72.5),

            Arguments.of(LocalTime.parse("09:07:30"),
                LocalTime.parse("09:09:30"), 10),

            Arguments.of(LocalTime.parse("09:07:30"),
                LocalTime.parse("10:20:00"), 74.5));
    }

    @ParameterizedTest
    @MethodSource("providerForGetVolume")
    public void getVolumeTests(LocalTime start, LocalTime end, double expectedVolume) {
        Curve curve = setupTestCurve();
        double curveVolume = curve.getVolume(start, end);
        assertEquals(expectedVolume, curveVolume, 1e-8);
    }

    private static Stream<Arguments> providerForGetRelativeVolume() {
        return Stream.of(
            Arguments.of(LocalTime.parse("09:00:00"),
                    LocalTime.parse("09:10:00"),
                    LocalTime.parse("09:05:00"), 16.66666666),

            Arguments.of(LocalTime.parse("09:00:00"),
                    LocalTime.parse("11:00:00"),
                    LocalTime.parse("09:05:00"), 5),

            Arguments.of(LocalTime.parse("09:00:00"),
                    LocalTime.parse("11:00:00"),
                    LocalTime.parse("10:10:00"), 90),

            Arguments.of(LocalTime.parse("09:00:00"),
                    LocalTime.parse("11:00:00"),
                    LocalTime.parse("10:10:00"), 90),

            Arguments.of(LocalTime.parse("04:00:00"),
                    LocalTime.parse("07:00:00"),
                    LocalTime.parse("05:00:00"), Double.NaN));
   }

    @ParameterizedTest
    @MethodSource("providerForGetRelativeVolume")
    public void getRelativeVolumeTests(LocalTime start, LocalTime end, LocalTime time,
            double expectedRelativeVolume) {
        Curve curve = setupTestCurve();
        double curveVolume = curve.getRelativeVolume(start, end, time);
        assertEquals(expectedRelativeVolume, curveVolume, 1e-8);
    }

    private Curve setupTestCurve() {
        Curve curve = CurveData.Of("1 HK");
        LocalTime firstBucketStart = LocalTime.parse("09:00:00");

        curve.addBucket(CurveBucket.Of(LocalTime.parse("00:00:00"), firstBucketStart, 0,
                BucketType.MARKET_CLOSED));
        LocalTime firstBucketEnd = LocalTime.parse("09:05:00");
        curve.addBucket(CurveBucket.Of(firstBucketStart, firstBucketEnd, 5,
                BucketType.OPEN_AUCTION));

        LocalTime secondBucketEnd = LocalTime.parse("09:10:00");
        curve.addBucket(CurveBucket.Of(firstBucketEnd, secondBucketEnd, 25,
                BucketType.CONT_TRADING));

        LocalTime thirdBucketEnd = LocalTime.parse("10:10:00");
        curve.addBucket(CurveBucket.Of(secondBucketEnd, thirdBucketEnd, 60,
                BucketType.CONT_TRADING));

        LocalTime fourthBucketEnd = LocalTime.parse("11:00:00");
        curve.addBucket(CurveBucket.Of(thirdBucketEnd, fourthBucketEnd, 10,
                BucketType.CONT_TRADING));
        return curve;
    }  
}
