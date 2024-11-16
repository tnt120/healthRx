package com.healthrx.backend.utils.statistics;

import org.springframework.stereotype.Component;

import static com.healthrx.backend.handler.BusinessErrorCodes.WRONG_BMI_DATA;

@Component
public class BmiCalculator {
    public Double calculateBMI(Double weight, Double height) {
        if (height <= 0 || weight <= 0) throw WRONG_BMI_DATA.getError();
        return Math.round(weight / Math.pow(height / 100, 2) * 100) / 100.0;
    }
}
