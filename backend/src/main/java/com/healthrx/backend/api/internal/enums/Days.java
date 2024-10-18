package com.healthrx.backend.api.internal.enums;

import java.time.DayOfWeek;

public enum Days {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    public DayOfWeek toDayOfWeek() {
        return switch (this) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }

    public static Days from(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> MONDAY;
            case TUESDAY -> TUESDAY;
            case WEDNESDAY -> WEDNESDAY;
            case THURSDAY -> THURSDAY;
            case FRIDAY -> FRIDAY;
            case SATURDAY -> SATURDAY;
            case SUNDAY -> SUNDAY;
        };
    }

    public static String getTranslation(Days day) {
        return switch (day) {
            case MONDAY -> "Pon";
            case TUESDAY -> "Wt";
            case WEDNESDAY -> "Śr";
            case THURSDAY -> "Czw";
            case FRIDAY -> "Pt";
            case SATURDAY -> "Sob";
            case SUNDAY -> "Ndz";
        };
    }
}