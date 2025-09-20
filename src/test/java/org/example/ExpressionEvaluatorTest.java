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

    @BeforeEach
    void setUp() {
        mapa = new HashMap<>();
        mapa2 = new HashMap<>();
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
}