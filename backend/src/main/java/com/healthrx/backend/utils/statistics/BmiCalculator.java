package com.healthrx.backend.utils.statistics;

import org.springframework.stereotype.Component;

@Component
public class BmiCalculator {
    public Double calculateBMI(Double weight, Double height) {
        return Math.round(weight / Math.pow(height / 100, 2) * 100) / 100.0;
    }
}
