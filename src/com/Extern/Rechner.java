package com.Extern;

import java.util.ArrayList;

import static java.lang.Math.*;

public class Rechner {

    public static void berechneErgebnis(double n, double alpha){
        alpha = Math.toRadians(alpha); // convert degrees to radians
        double deltaAlphaRad = Math.toRadians(0.4); // convert Δα from degrees to radians
        double lambda = 587.56e-3; // example λ in appropriate units
        double deltaLambda = 10.9e-3; // Δλ in same units as λ

        // first term: |n λ cos(α) / sin^2(α)| * Δα
        double term1 = Math.abs(-n * lambda * (Math.cos(alpha)/Math.pow(Math.sin(alpha), 2))) * deltaAlphaRad;

        // second term: |n / sin(α)| * Δλ
        double term2 = Math.abs(n / Math.sin(alpha)) * deltaLambda;

        double ergebnis = term1 + term2;
        System.out.println(ergebnis);
    }

    public static void averageDistance(double[] total){
        double previous = total[0];
        double distances = 0.0;
        for(int i = 1; i < total.length; i++){
            double current = total[i];
            distances += (current-previous);
            previous = current;
        }
        System.out.printf("Average distance is: %f\n", distances/(total.length-1));
    }
}
