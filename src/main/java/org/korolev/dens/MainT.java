package org.korolev.dens;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainT {

    public static void main(String[] args) throws WrongInputException {
        InterpolationIO io = new InterpolationIO();
        List<List<Double>> li = io.tryToRead();
        List<Double> x = li.get(0).stream().sorted().toList();
        System.out.println(x);

    }


    public static long getFactorial(long f) {
        if (f <= 1) {
            return 1;
        }
        else {
            return f * getFactorial(f - 1);
        }
    }
}
