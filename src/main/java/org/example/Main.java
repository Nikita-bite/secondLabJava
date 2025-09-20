package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        Map<String, Double> mapa2 = new LinkedHashMap<>();
        System.out.println("result '10+5': " + ExpressionEvaluator.evaluate("10+5", mapa));
        System.out.println("result '10+-5': " + ExpressionEvaluator.evaluate("10+-5", mapa));
        System.out.println("result '2.7*(10-3.5)': " + ExpressionEvaluator.evaluate("2.7*(10-3.5)", mapa));
        //System.out.println("result '2.7*(10-3.5))': " + ExpressionEvaluator.evaluate("2.7*(10-3.5))", mapa));
        System.out.println("result 'sin(180)': " + ExpressionEvaluator.evaluate("sin(180)", mapa));
        System.out.println("result 'cos(180)': " + ExpressionEvaluator.evaluate("cos(180)", mapa));
        System.out.println("result 'sin(90 + sin(180))': " + ExpressionEvaluator.evaluate("sin(90+sin(180))", mapa));
        System.out.println("result 'sin(90 + sin(90 + sin(90)))': " + ExpressionEvaluator.evaluate("sin(90 + sin(90 + sin(90)))", mapa));
        System.out.println("result 'cos(90 + cos(90 + cos(90) + 45) + 15*3)': " + ExpressionEvaluator.evaluate("cos(90 + cos(90 + cos(90) + 45) + 15*3)", mapa));
        System.out.println("result '(10+10/2)': " + ExpressionEvaluator.evaluate("(10+10/2)", mapa));
        System.out.println("result '((10+10^2))': " + ExpressionEvaluator.evaluate("(10+10^2)", mapa));
        System.out.println("result '(10+10^2/(sin(90)*2))': " + ExpressionEvaluator.evaluate("(10+10^2/(sin(90)*2))", mapa));
        mapa.put("angle", 180.0);
        System.out.println("result 'cos(angle)': " + ExpressionEvaluator.evaluate("cos(angle)", mapa));
        mapa2.put("angle", 90.0);
        mapa2.put("halfangle", 45.0);
        System.out.println("result 'cos(angle + cos(angle + cos(angle) + angle/2) + halfangle)': " + ExpressionEvaluator.evaluate("cos(angle + cos(angle + cos(angle) + angle/2) + halfangle)", mapa2));

        //System.out.println(Math.cos(Math.toRadians(90 + Math.cos(Math.toRadians(90 + Math.cos(Math.toRadians(90)) + 45)) + 15*3)));
    }
}