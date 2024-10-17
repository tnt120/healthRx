package com.healthrx.backend.api.internal.enums;

public enum TrendType {
    VERY_INCREASING,
    SLIGHTLY_INCREASING,
    STABLE,
    SLIGHTLY_DECREASING,
    VERY_DECREASING;


    public static TrendType fromSlope(double slope, double threshold, double stabilityThreshold) {
        if (slope > threshold) return VERY_INCREASING;
        if (slope > stabilityThreshold) return SLIGHTLY_INCREASING;
        if (slope < -threshold) return VERY_DECREASING;
        if (slope < -stabilityThreshold) return SLIGHTLY_DECREASING;
        return STABLE;
    }

    public static String getTranslation(TrendType trendType) {
        return switch (trendType) {
            case VERY_INCREASING -> "Rosnący";
            case SLIGHTLY_INCREASING -> "Lekko rosnący";
            case STABLE -> "Stabilny";
            case SLIGHTLY_DECREASING -> "Lekko malejący";
            case VERY_DECREASING -> "Malejący";
            default -> "Nieznany";
        };
    }
}