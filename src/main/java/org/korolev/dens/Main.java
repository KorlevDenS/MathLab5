package org.korolev.dens;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws WrongInputException {
        InterpolationIO io = new InterpolationIO();
        List<List<Double>> lists  = io.tryToRead();

        System.out.println("Введите х для получения Y");
        double X = new Scanner(System.in).nextDouble();
        if (X > lists.get(0).get(lists.get(0).size() - 1) || X < lists.get(0).get(0))
            throw new WrongInputException("Введенный X не попадает в исследуемый интервал");

        if (io.equalsInterval(lists.get(0))) {
            NewtonInterpolation i1 = new NewtonInterpolation((ArrayList<Double>) lists.get(1), (ArrayList<Double>) lists.get(0));
            i1.interpolate(X);
            System.out.println("Метод Ньютона: " + i1.getNewYValue());
            //System.out.println("Интерполяционный многочлен: " + i1.getStringBuilder().toString());
            Expression exp = new ExpressionBuilder(i1.getStringBuilder().toString()).variable("x").build();
            io.drawFunction(exp, i1.getX_values().get(0), i1.getX_values().get(i1.getX_values().size() - 1), i1.x_values, i1.y_values);
            System.out.println("Конечные разности:");
            i1.getFiniteDifferences().forEach(System.out::println);
        }


        LagrangeInterpolation i2 = new LagrangeInterpolation((ArrayList<Double>) lists.get(1), (ArrayList<Double>) lists.get(0));
        i2.interpolate(X);
        System.out.println("Метод Лагранжа: " + i2.getNewYValue());
    }
}