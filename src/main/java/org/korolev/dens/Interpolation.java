package org.korolev.dens;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Interpolation {

    protected ArrayList<Double> y_values;
    protected ArrayList<Double> x_values;
    protected int vars_amount_N;
    protected double newYValue;
    protected double xToKnowY;

    public Interpolation(ArrayList<Double> y_values, ArrayList<Double> x_values) {
        this.y_values = new ArrayList<>(y_values);
        this.x_values = new ArrayList<>(x_values);
        this.vars_amount_N = y_values.size();
    }

    public void interpolate(double x) {

    }

}
