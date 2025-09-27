package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private Map<String, Double> mapa;
    private Map<String, Double> mapa2;
    private Map<String, Double> mapaE;

    @BeforeEach
    void setUp() {
        mapa = new HashMap<>();
        mapa2 = new HashMap<>();
        mapaE = new HashMap<>();
    }

    @Test
    void testBasicOperations() {
        assertEquals(15.0, ExpressionEvaluator.evaluate("10+5", mapa));
        assertEquals(5.0, ExpressionEvaluator.evaluate("10+-5", mapa));
    }

    @Test
    void testDecimalOperations() {
        assertEquals(2.7 * (10 - 3.5), ExpressionEvaluator.evaluate("2.7*(10-3.5)", mapa), 1e-10);
    }

    @Test
    void testTrigonometricFunctions() {
        assertEquals(Math.sin(Math.toRadians(180)), ExpressionEvaluator.evaluate("sin(180)", mapa), 1e-10);
        assertEquals(Math.cos(Math.toRadians(180)), ExpressionEvaluator.evaluate("cos(180)", mapa), 1e-10);
        assertEquals(1.0, ExpressionEvaluator.evaluate("sin(90+sin(180))", mapa), 1e-10);
    }

    @Test
    void testNestedFunctions() {
        assertEquals(Math.sin(Math.toRadians(90 + Math.sin(Math.toRadians(90 + Math.sin(Math.toRadians(90)))))),
                ExpressionEvaluator.evaluate("sin(90 + sin(90 + sin(90)))", mapa), 1e-10);
    }

    @Test
    void testComplexExpression() {
        assertEquals(10 + Math.pow(10, 2) / (Math.sin(Math.toRadians(90)) * 2),
                ExpressionEvaluator.evaluate("(10+10^2/(sin(90)*2))", mapa), 1e-10);
    }

    @Test
    void testVariables() {
        mapa.put("angle", 180.0);
        assertEquals(Math.cos(Math.toRadians(180)), ExpressionEvaluator.evaluate("cos(angle)", mapa), 1e-10);

        mapa2.put("angle", 90.0);
        mapa2.put("halfangle", 45.0);
        double expected = Math.cos(Math.toRadians(90 + Math.cos(Math.toRadians(90 + Math.cos(Math.toRadians(90)) + 45)) + 45));
        assertEquals(expected, ExpressionEvaluator.evaluate("cos(angle + cos(angle + cos(angle) + angle/2) + halfangle)", mapa2), 1e-10);
    }

    @Test
    void testDivisionByZeroThrowsException() {
        assertThrows(ArithmeticException.class, () -> {
            ExpressionEvaluator.evaluate("10/0", mapa);
        });

        assertThrows(ArithmeticException.class, () -> {
            ExpressionEvaluator.evaluate("5/(3-3)", mapa);
        });

        assertThrows(ArithmeticException.class, () -> {
            ExpressionEvaluator.evaluate("sin(90)/(cos(90)-cos(90))", mapa);
        });
    }

    @Test
    void testInvalidExpressions() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("2.7*(10-3.5))", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("10++5", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("10$+5", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("*10+5", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("10+5-", mapa);
        });
    }

    @Test
    void testMissingVariables() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("x + 5", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("x + y * z", mapa);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("sin + 5", mapa);
        });
    }

    @Test
    void testEmptyMapWithVariablesInExpressionEvaluator() {
        Map<String, Double> emptyMap = new HashMap<>();
        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("variable + 5", emptyMap);
        });
    }

    @Test
    void testEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExpressionEvaluator.evaluate("", mapa);
        });

        assertEquals(42.0, ExpressionEvaluator.evaluate("42", mapa));

        assertEquals(-5.0, ExpressionEvaluator.evaluate("-5", mapa));

        assertEquals(2.0, ExpressionEvaluator.evaluate("((((2))))", mapa));
    }

    @Test
    void testExtraFunctions() {
        mapaE.put("e", 2.71828);
        assertEquals(1.0, ExpressionEvaluator.evaluate("ln(e)", mapaE), 1e-5);
        assertEquals(2.0, ExpressionEvaluator.evaluate("log(4)", mapaE), 1e-5);
        assertEquals(0.0, ExpressionEvaluator.evaluate("ln(1)", mapaE), 1e-5);
        assertEquals(0.0, ExpressionEvaluator.evaluate("log(1)", mapaE), 1e-5);
        assertEquals(13.2877034416, ExpressionEvaluator.evaluate("ln(e^2) * log(100) / tan(45)", mapaE), 1e-4);
        assertEquals(6.6438568625, ExpressionEvaluator.evaluate("cot(45) + log(100) - ln(e)", mapaE), 1e-4);
        assertEquals(4.0, ExpressionEvaluator.evaluate("sin(90) + cos(0) + tan(45) + cot(45)", mapaE), 1e-4);
    }

    @Test
    void testMathematicalExceptions() {
        ArithmeticException exception1 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("10 / 0", mapa));
        assertTrue(exception1.getMessage().contains("Division by zero"));

        ArithmeticException exception2 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("tan(90)", mapa));
        assertTrue(exception2.getMessage().contains("Тангенс не определен"));

        ArithmeticException exception3 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("cot(0)", mapa));
        assertTrue(exception3.getMessage().contains("Котангенс не определен"));

        ArithmeticException exception4 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("ln(-5)", mapa));
        assertTrue(exception4.getMessage().contains("Логарифм определен только для положительных чисел"));

        ArithmeticException exception5 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("log(-10)", mapa));
        assertTrue(exception5.getMessage().contains("Логарифм определен только для положительных чисел"));

        ArithmeticException exception6 = assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate("ln(0)", mapa));
        assertTrue(exception6.getMessage().contains("Логарифм определен только для положительных чисел"));
    }

}