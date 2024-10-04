package com.healthrx.backend.api.internal.enums;

public enum TrendType {
    VERY_INCREASING, SLIGHTLY_INCREASING, STABLE, SLIGHTLY_DECREASING, VERY_DECREASING;

    public static TrendType fromSlope(double slope, double threshold, double stabilityThreshold) {
        if (slope > threshold) return VERY_INCREASING;
        if (slope > stabilityThreshold) return SLIGHTLY_INCREASING;
        if (slope < -threshold) return VERY_DECREASING;
        if (slope < -stabilityThreshold) return SLIGHTLY_DECREASING;
        return STABLE;
    }
}
