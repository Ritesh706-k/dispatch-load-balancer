package com.ritesh.dispatch.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HaversineCalculatorTest {

    @Test
    void samePointIsZero() {
        assertEquals(0.0, HaversineCalculator.distanceKm(28.6139, 77.2090, 28.6139, 77.2090), 1e-9);
    }

    @Test
    void oneDegreeOfLatitudeIsAbout111Km() {
        assertEquals(111.19, HaversineCalculator.distanceKm(0, 0, 1, 0), 0.1);
    }

    @Test
    void distanceIsSymmetric() {
        double ab = HaversineCalculator.distanceKm(28.6139, 77.2090, 19.0760, 72.8777);
        double ba = HaversineCalculator.distanceKm(19.0760, 72.8777, 28.6139, 77.2090);
        assertEquals(ab, ba, 1e-9);
    }

    @Test
    void delhiToMumbaiIsRoughly1150Km() {
        double km = HaversineCalculator.distanceKm(28.6139, 77.2090, 19.0760, 72.8777);
        assertEquals(1150, km, 30);
    }
}
