package com.effanville.curvedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.Test;
import com.effanville.curvedata.IO.CurveCsvReader;

public class CurveReaderTest {
    @Test
    public void canReadCurveFileTest() {
        CurveCsvReader reader = new CurveCsvReader();
        Curve curve = reader.readCurve("5 HK", "src/test/resources/example-curve.csv");
        assertNotNull(curve);
        assertEquals(8, curve.getBuckets().size());
    }

    @Test
    public void canReadInvalidCurveFileTest() {
        CurveCsvReader reader = new CurveCsvReader();
        Curve curve = reader.readCurve("5 HK", "src/test/resources/invalid-example-curve.csv");
        assertNull(curve);
    }

    @Test
    public void canReadMissingCurveFileTest() {
        CurveCsvReader reader = new CurveCsvReader();
        Curve curve = reader.readCurve("5 HK", "src/test/resources/missing-example.csv");
        assertNull(curve);
    }
}
