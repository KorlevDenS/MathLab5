package org.korolev.dens;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InterpolationIO {

    private List<Interpolation> appList;
    private final Scanner stdinReader;
    private Scanner fileReader;

    private Expression expressionIfExists;

    public InterpolationIO() {
        this.stdinReader = new Scanner(System.in);
    }


    public boolean equalsInterval(List<Double> list) {
        BigDecimal interval = BigDecimal.valueOf(list.get(1)).subtract(BigDecimal.valueOf(list.get(0)));
        for (int i = 2; i < list.size(); i++) {
            if (!BigDecimal.valueOf(list.get(i)).subtract(BigDecimal.valueOf(list.get(i-1))).equals(interval))
                return false;
        }
        return true;
    }

    public List<List<Double>> tryToRead() throws WrongInputException {
        System.out.println("Введите значения (x;y) с клавиатуры или укажите ссылку на файл");
        System.out.println("Для ввода с помощью функции введите \"function\"");

        String input = this.stdinReader.nextLine();
        if (input.equals("")) throw new WrongInputException("Строка пуста");

        String firstLine = input.trim();

        if (firstLine.matches(".*\\.(txt|doc|pdf)$")) {
            try {
                this.fileReader = new Scanner(new FileReader(firstLine));
            } catch (FileNotFoundException e) {
                throw new WrongInputException("Файл не существует или имеет недопустимое имя.");
            }
            if (!fileReader.hasNext()) throw new WrongInputException("Файл пуст.");
            return readFromFile();
        } else if (firstLine.matches("exit"))
            System.exit(0);
        else if (firstLine.matches("function")) {
            return useFunction();
        }
        return readFromStdin(firstLine);
    }


    private List<List<Double>> useFunction() {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        System.out.println("Введите уравнение");
        String func = this.stdinReader.nextLine();
        Expression expression = new ExpressionBuilder(func).variable("x").build();
        this.expressionIfExists = expression;
        System.out.println("Введите желаемый интервал (два значения х через пробел)");
        String x1x2 = this.stdinReader.nextLine();
        Scanner sc = new Scanner(x1x2);
        double x1 = sc.nextDouble();
        double x2 = sc.nextDouble();
        System.out.println("Введите количество точек на интервале");
        int n = this.stdinReader.nextInt();
        double step = BigDecimal.valueOf(x2 - x1)
                .divide(BigDecimal.valueOf(n - 1), 5, RoundingMode.HALF_DOWN).doubleValue();

        double currentX = x1;
        for (int i = 0; i < n - 1; i ++) {
            xData.add(currentX);
            yData.add(expression.setVariable("x", currentX).evaluate());
            currentX += step;
        }
        xData.add(x2);
        yData.add(expression.setVariable("x", x2).evaluate());

        List<List<Double>> list = new ArrayList<>();
        list.add(xData);
        list.add(yData);
        //System.out.println(list);
        return list;
    }

    private List<List<Double>> readFromFile() {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        String x = this.fileReader.nextLine();
        Scanner xScan = new Scanner(x);
        String y = this.fileReader.nextLine();
        Scanner yScan = new Scanner(y);

        while (xScan.hasNext()) {
            xData.add(xScan.nextDouble());
            yData.add(yScan.nextDouble());
        }

        List<List<Double>> list = new ArrayList<>();
        list.add(xData);
        list.add(yData);
        return list;
    }

    private List<List<Double>> readFromStdin(String firstLine) {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        Scanner xScan = new Scanner(firstLine);
        String y = this.stdinReader.nextLine();
        Scanner yScan = new Scanner(y);

        while (xScan.hasNext()) {
            xData.add(xScan.nextDouble());
            yData.add(yScan.nextDouble());
        }
        List<List<Double>> list = new ArrayList<>();
        list.add(xData);
        list.add(yData);
        return list;
    }


    public void drawFunction(Expression exp, double x1, double x2, ArrayList<Double> xs, ArrayList<Double> ys) {
        List<Double> xData1 = new ArrayList<>();
        List<Double> yData1 = new ArrayList<>();
        double xMin = x1;
        double xMax = x2;
        double len = xMax - xMin;
        double part = len / 100;
        while (xMin < xMax) {
            yData1.add(exp.setVariable("x", xMin).evaluate());
            xData1.add(xMin);
            xMin += part;
        }
        yData1.add(exp.setVariable("x", xMin).evaluate());
        xData1.add(xMin);

        XYChart chart = new XYChartBuilder().width(1000).height(600).theme(Styler.ChartTheme.Matlab).title("Interpolation").build();

        XYSeries series1 = chart.addSeries("interpolate", xData1, yData1);
        series1.setMarker(SeriesMarkers.NONE);


        if (expressionIfExists != null) {
            List<Double> xData2 = new ArrayList<>();
            List<Double> yData2 = new ArrayList<>();
            double xMin2 = x1;
            double xMax2 = x2;
            double len2 = xMax2 - xMin2;
            double part2 = len2 / 100;
            while (xMin2 < xMax2) {
                yData2.add(expressionIfExists.setVariable("x", xMin2).evaluate());
                xData2.add(xMin2);
                xMin2 += part2;
            }
            yData2.add(expressionIfExists.setVariable("x", xMin2).evaluate());
            xData2.add(xMin2);

            XYSeries series2 = chart.addSeries("function", xData2, yData2);
            series2.setMarker(SeriesMarkers.NONE);
        }

        XYSeries points = chart.addSeries("points", xs, ys);
        points.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

        new SwingWrapper<>(chart).displayChart();
    }


}
