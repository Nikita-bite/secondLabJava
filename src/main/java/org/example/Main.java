package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        Map<String, Double> mapa2 = new LinkedHashMap<>();
        Map<String, Double> mapaE = new LinkedHashMap<>();
        System.out.println();
        System.out.println("10 + 5 = " + ExpressionEvaluator.evaluate("10 + 5", mapa));
        System.out.println("10 + -5 = " + ExpressionEvaluator.evaluate("10 + -5", mapa));
        System.out.println("2.7 * (10 - 3.5) = " + ExpressionEvaluator.evaluate("2.7 * (10 - 3.5)", mapa));
        System.out.println("sin(180) = " + ExpressionEvaluator.evaluate("sin(180)", mapa));
        System.out.println("cos(180) = " + ExpressionEvaluator.evaluate("cos(180)", mapa));
        System.out.println("sin(90 + sin(180)) = " + ExpressionEvaluator.evaluate("sin(90 + sin(180))", mapa));
        System.out.println("sin(90 + sin(90 + sin(90))) = " + ExpressionEvaluator.evaluate("sin(90 + sin(90 + sin(90)))", mapa));
        System.out.println("cos(90 + cos(90 + cos(90) + 45) + 15*3) = " + ExpressionEvaluator.evaluate("cos(90 + cos(90 + cos(90) + 45) + 15*3)", mapa));
        System.out.println("(10 + 10/2) = " + ExpressionEvaluator.evaluate("(10 + 10/2)", mapa));
        System.out.println("((10 + 10^2)) = " + ExpressionEvaluator.evaluate("(10 + 10^2)", mapa));
        System.out.println("(10 + 10^2/(sin(90) * 2)) = " + ExpressionEvaluator.evaluate("(10 + 10^2/(sin(90) * 2))", mapa));
        System.out.println("sin(90) + cos(0) + tan(45) + cot(45) = " + ExpressionEvaluator.evaluate("sin(90) + cos(0) + tan(45) + cot(45)", mapa));
        System.out.println("(sin(30) + cos(60)) * tan(45) = " + ExpressionEvaluator.evaluate("(sin(30) + cos(60)) * tan(45)", mapa));
        System.out.println("С переменными: ");
        mapa.put("angle", 180.0);
        System.out.println("angle = 180");
        System.out.println("cos(angle) = " + ExpressionEvaluator.evaluate("cos(angle)", mapa));
        mapa2.put("angle", 90.0);
        mapa2.put("halfangle", 45.0);
        System.out.println("angle = 90");
        System.out.println("halfangle = 45");
        System.out.println("cos(angle + cos(angle + cos(angle) + angle/2) + halfangle) = " + ExpressionEvaluator.evaluate("cos(angle + cos(angle + cos(angle) + angle/2) + halfangle)", mapa2));
        mapaE.put("e", 2.71828);
        System.out.println("e = 2.71828");
        System.out.println("ln(e^2) * log(100) / tan(45) = " + ExpressionEvaluator.evaluate("ln(e^2) * log(100) / tan(45)", mapaE));
        System.out.println("cot(45) + log(100) - ln(e) = " + ExpressionEvaluator.evaluate("cot(45) + log(100) - ln(e)", mapaE));
    }
}