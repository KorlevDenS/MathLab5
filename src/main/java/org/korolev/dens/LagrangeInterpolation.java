package org.korolev.dens;

import java.util.ArrayList;

public class LagrangeInterpolation extends Interpolation {

    public LagrangeInterpolation(ArrayList<Double> y_values, ArrayList<Double> x_values) {
        super(y_values, x_values);
    }

    @Override
    public void interpolate(double x) {
        this.xToKnowY = x;
        calcNewY();
    }

    private void calcNewY() {
        //double order = calcOrder();
        double order = vars_amount_N - 1;
        double newY = 0;
        for (int i = 0; i <= order; i++ ) {
            double mul = 1;
            for (int j = 0; j <= order; j++ ) {
                if (i == j) continue;
                mul *= (xToKnowY - x_values.get(j)) / (x_values.get(i) - x_values.get(j));
            }
            newY += y_values.get(i) * mul;
        }
        this.newYValue = newY;
    }

    private int calcOrder() {
        for (int i = 0; i < vars_amount_N; i++ ) {
            if (xToKnowY > x_values.get(i) && xToKnowY < x_values.get(i + 1)) {
                return i + 1;
            }
        }
        return -1;
    }
}
