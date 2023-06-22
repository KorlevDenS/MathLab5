package org.korolev.dens;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Getter
public class NewtonInterpolation extends Interpolation {

    private ArrayList<ArrayList<Double>> finiteDifferences;
    private StringBuilder stringBuilder;

    public NewtonInterpolation(ArrayList<Double> y_values, ArrayList<Double> x_values) {
        super(y_values, x_values);
    }

    @Override
    public void interpolate(double x) {
        this.xToKnowY = x;
        if (this.finiteDifferences == null) calcFiniteDiffs();
        chooseFormula();
        formFunction();
    }

    private void formFunction() {
        double stepH = BigDecimal.valueOf(x_values.get(1))
                .subtract(BigDecimal.valueOf(x_values.get(0))).doubleValue();
        stringBuilder = new StringBuilder();
        int n = vars_amount_N - 1;
        stringBuilder.append(finiteDifferences.get(0).get(0));


        //System.out.println(finiteDifferences.get(0).get(0));//////////////

        for (int i = 1; i <= n; i++) {
            stringBuilder.append(" + ").append(finiteDifferences.get(i).get(0)/ (Math.pow(stepH, i) * getFactorial(i)));
            //System.out.println(finiteDifferences.get(i).get(0));////////////////////
            appendX(i);
        }
    }

    private void appendX(int amount) {
        for (int i = 0; i < amount; i++ ) {
            stringBuilder.append(" * ").append("(x - ").append(x_values.get(i)).append(")");
        }
    }

    public void calcFiniteDiffs() {
        finiteDifferences = new ArrayList<>();
        finiteDifferences.add(new ArrayList<>(y_values));
        for (int i = 0; i < y_values.size() - 1; i++) {
            int counter = finiteDifferences.get(i).size() - 1;
            ArrayList<Double> list = new ArrayList<>();
            for (int k = 0; k < counter; k ++) {
                list.add(BigDecimal.valueOf(finiteDifferences.get(i).get(k + 1))
                        .subtract(BigDecimal.valueOf(finiteDifferences.get(i).get(k))).doubleValue());
            }
            finiteDifferences.add(new ArrayList<>(list));
        }
    }

    private void chooseFormula() {
        double middle = (x_values.get(vars_amount_N - 1) + x_values.get(0)) / 2;
        if (xToKnowY <= middle) interpolateForward();
        else interpolateBack();
    }

    private void interpolateForward() {
        System.out.println("Интерполирование вперед");
        double stepH = BigDecimal.valueOf(x_values.get(1))
                .subtract(BigDecimal.valueOf(x_values.get(0))).doubleValue();
        double t = 0;
        int order = 0;
        int index = 0;
        for (int a = 0; a < vars_amount_N; a++) {
            if (xToKnowY > x_values.get(a) && xToKnowY < x_values.get(a+1)) {
                t = BigDecimal.valueOf(xToKnowY).subtract(BigDecimal.valueOf(x_values.get(a)))
                        .divide(BigDecimal.valueOf(stepH), 5, RoundingMode.HALF_UP).doubleValue();
                order = vars_amount_N - 1 - a;
                index = a;
                break;
            }
        }
        double newY = finiteDifferences.get(0).get(index);
        for (int i = 1; i <= order; i++) {
            newY += mulT(t, i) / getFactorial(i) * finiteDifferences.get(i).get(index);
        }
        this.newYValue = newY;
    }

    private double mulT(double t, int order) {
        double res = t;
        for (int i = 1; i < order; i++ ) {
            res *= t - i;
        }
        return res;
    }

    private long getFactorial(long f) {
        if (f <= 1) {
            return 1;
        }
        else {
            return f * getFactorial(f - 1);
        }
    }

    private void interpolateBack() {
        System.out.println("Интерполирование назад");
        double stepH = BigDecimal.valueOf(x_values.get(1))
                .subtract(BigDecimal.valueOf(x_values.get(0))).doubleValue();
        double t = 0;
        int order = 0;
        int index = 0;
        for (int a = 0; a < vars_amount_N; a++) {
            if (xToKnowY < x_values.get(a)) {
                t = BigDecimal.valueOf(xToKnowY).subtract(BigDecimal.valueOf(x_values.get(a)))
                        .divide(BigDecimal.valueOf(stepH), 5, RoundingMode.HALF_UP).doubleValue();
                order = a;
                index = a;
                break;
            }
        }
        double newY = finiteDifferences.get(0).get(index);
        for (int i = 1; i <= order; i++) {
            newY += mulT2(t, i) / getFactorial(i) * finiteDifferences.get(i).get(index-i);
        }
        this.newYValue = newY;
    }


    private double mulT2(double t, int order) {
        double res = t;
        for (int i = 1; i < order; i++ ) {
            res *= t + i;
        }
        return res;
    }
}
