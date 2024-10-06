package com.healthrx.backend.utils;

import lombok.Data;

@Data
public class ComplianceStats {
    private int planned;
    private int taken;
    private int onTime;

    public void incrementTaken() {
        taken++;
    }

    public void incrementOnTime() {
        onTime++;
    }

    public double calcTotalPercentTaken() {
        if (planned == 0) return 0;

        return (double) taken / planned * 100;
    }

    public double calcTotalPercentOnTime() {
        if (taken == 0) return 0;

        return (double) onTime / taken * 100;
    }
}
