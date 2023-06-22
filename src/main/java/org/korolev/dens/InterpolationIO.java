package org.korolev.dens;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InterpIO {

    private List<Approximation> appList;
    private final Scanner stdinReader;
    private Scanner fileReader;

    public InterpIO() {
        this.stdinReader = new Scanner(System.in);
    }


    public List<List<Double>> tryToRead() throws WrongInputException {
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
        return readFromStdin(firstLine);
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



    public String formDataArrays(List<Approximation> appList) {
        this.appList = new ArrayList<>(appList);
        String resultString = "";
        StringBuilder builder = new StringBuilder(resultString);

        builder.append("Значения х:\t\t");
        appList.get(0).getOld_x_values().forEach(a -> builder.append(a).append("\t\t"));
        builder.append("\nЗначения y:\t\t");
        appList.get(0).getY_values().forEach(a -> builder.append(a).append("\t\t"));
        builder.append("\n\n");

        for (Approximation approximation : appList) {
            builder.append("Вид функции:  ").append(approximation.getEvaluationToString()).append("\n");
            builder.append("Коэффициенты:  ");
            builder.append("a = ").append(approximation.getApprox_coefficients().get(0)).append("\t");
            builder.append("b = ").append(approximation.getApprox_coefficients().get(1)).append("\t");
            try {
                builder.append("c = ").append(approximation.getApprox_coefficients().get(2)).append("\t\n");
            } catch (Exception e) {
                builder.append("-\t\n");
            }
            if (approximation.getClass() == Approximation.class) {
                builder.append("Коэффициент корреляции Пирсона:  ")
                        .append(BigDecimal.valueOf(approximation.getPirson_correlation_coefficient()).setScale(5, RoundingMode.HALF_UP)).append("\n");
            }
            builder.append("Мера отклонения S:  ")
                    .append(BigDecimal.valueOf(approximation.getDeviation_measure_S())
                            .setScale(5, RoundingMode.HALF_UP)).append("\n");
            builder.append("Среднеквадратичное отклонение:  ").append(BigDecimal.valueOf(approximation.getStandard_square_deviation())
                    .setScale(5, RoundingMode.HALF_UP)).append("\n");
            builder.append("Массив fi(х):\t\t");
            approximation.getNew_y_values().forEach(a -> builder.append(a).append("\t\t"));
            builder.append("\n");

            builder.append("Массив отклонений epsilon:\t\t");
            approximation.getEpsilon_list().forEach(a -> builder.append(a).append("\t\t"));
            builder.append("\n");

            builder.append("\n");
        }

        builder.append(makeConclusion()).append("\n");

        resultString = builder.toString();
        return resultString;
    }

    public String makeConclusion() {
        Approximation bestOne = appList.get(0);
        for (int i = 1; i < appList.size(); i++ ) {
            if (appList.get(i).getStandard_square_deviation() < bestOne.getStandard_square_deviation())
                bestOne = appList.get(i);
        }
        return "Вывод: Наиболее подходящая функция к данному набору точек: " + bestOne.getEvaluationToString() + "\n" +
                "Её среднеквадратичное отклонение оказалось наименьшим и равно " + bestOne.getStandard_square_deviation();
    }
}
