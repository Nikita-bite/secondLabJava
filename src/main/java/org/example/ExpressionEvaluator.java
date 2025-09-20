package org.example;

import java.util.*;
import java.lang.Math;

public class ExpressionEvaluator {

    public ExpressionEvaluator() {

    }

    private static boolean isValidExpression(String eval) {
        eval = eval.replace(" ", "");
        int counterBrackets = 0;
        for (char c : eval.toCharArray()) {
            if (c == '(') counterBrackets++;
            if (c == ')') counterBrackets--;
            if (counterBrackets < 0) return false;
        }
        if (counterBrackets != 0) return false;

        if (!eval.matches("[0-9a-zA-Z+\\-*/^().,]+")) {
            return false;
        }
        int len = eval.length();
        if (eval.charAt(0) == '+' || eval.charAt(0) == '*' || eval.charAt(0) == '/' || eval.charAt(0) == '^'
                || eval.charAt(len - 1) == '+' || eval.charAt(len - 1) == '-' || eval.charAt(len - 1) == '*' || eval.charAt(len - 1) == '/' || eval.charAt(len - 1) == '^') {
            return false;
        }
        for (int i = 0; i < len - 1; i++) {
            if ((eval.charAt(i) == '+' && eval.charAt(i + 1) == '+')
                    || (eval.charAt(i) == '-' && eval.charAt(i + 1) == '-')
                    || (eval.charAt(i) == '*' && eval.charAt(i + 1) == '*')
                    || (eval.charAt(i) == '/' && eval.charAt(i + 1) == '/')
                    || (eval.charAt(i) == '^' && eval.charAt(i + 1) == '^')) {
                return false;
            }
        }
        return true;
    }

    private static  String replaceVariables(String eval, Map<String, Double> variables) {
        int len = eval.length();
        int counter = 0;
        Set<String> linkedSet = new LinkedHashSet<>();
        String varName = "";
        for (int i = 0; i < len; i++) {
            char c = eval.charAt(i);
            boolean isLatinLetter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            if (isLatinLetter) {
                counter++;
                varName = varName + c;
            } else {
                if (counter > 0) {
                    if (!varName.equals("sin") && !varName.equals("cos")) {
                        linkedSet.add(varName);
                    }
                    varName = "";
                }
                counter = 0;
            }
        }
        if (!varName.isEmpty()) {
            if (!varName.equals("sin") && !varName.equals("cos")) {
                linkedSet.add(varName);
            }
            varName = "";
        }

        for (String name : linkedSet) {
            if (!variables.containsKey(name)) {
                throw new IllegalArgumentException("Variable not found: " + name);
            }
        }

        String[] varNames = linkedSet.toArray(new String[0]);

        List<String> sortedVariables = new ArrayList<>();

        for (String name : varNames) {
            sortedVariables.add(name);
        }

        sortedVariables.sort((s1, s2) -> Integer.compare(s2.length(), s1.length()));

        for (String name : sortedVariables) {
            eval = eval.replace(name, variables.get(name).toString());
        }

        return eval;
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double parseNumber(String str) {
        try {
            double number = Double.parseDouble(str.trim());
            return Math.round(number * 1e10) / 1e10;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number: " + str);
        }
    }

    private static  String calculateFunctions(String eval) {
        List<int[]> listOfStartAndEndEvals = new ArrayList<>();
        int len = eval.length();
        int counter = 0;
        String varName = "";
        for (int i = 0; i < len; i++) {
            char c = eval.charAt(i);
            boolean isLatinLetter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '(' && varName.length() == 3);
            if (isLatinLetter) {
                counter++;
                varName = varName + c;
            } else {
                if (counter > 0) {
                    if (varName.equals("sin(") || varName.equals("cos(")) {
                        int j = i;
                        int counterBrackets = 1;
                        while (counterBrackets != 0) {
                            j++;
                            char inner_c = eval.charAt(j);
                            if (inner_c == '(') counterBrackets++;
                            if (inner_c == ')') counterBrackets--;
                        }
                        listOfStartAndEndEvals.add(new int[]{i, j});
                    }
                    varName = "";
                }
                counter = 0;
            }
        }

        listOfStartAndEndEvals.sort((a, b) -> Integer.compare(b[0], a[0]));
        int curElem = 0;
        for (int[] startAndEndEvals : listOfStartAndEndEvals) {
            String cutPart = eval.substring(startAndEndEvals[0], startAndEndEvals[1]);
            String func = eval.substring(startAndEndEvals[0] - 4, startAndEndEvals[0] - 1);
            double resultOfArg = evaluateWithoutVariables(cutPart);
            double resultOfFunc = switch (func) {
                case "sin" -> Math.sin(Math.toRadians(resultOfArg));
                case "cos" -> Math.cos(Math.toRadians(resultOfArg));
                default -> resultOfArg;
            };
            eval = eval.substring(0, startAndEndEvals[0] - 4) + parseNumber(String.valueOf(resultOfFunc)) + eval.substring(startAndEndEvals[1] + 1);
            int curCorrection = 0;
            for (int[] startAndEndEvalsForCorrectionIndexes : listOfStartAndEndEvals) {
                if (curElem <= curCorrection && startAndEndEvalsForCorrectionIndexes[1] > startAndEndEvals[1]) {
                    int resultOfArgLen = String.valueOf(parseNumber(String.valueOf(resultOfFunc))).length();
                    int evalFuncLen = startAndEndEvals[1] + 1 - (startAndEndEvals[0] - 4);
                    int diff = evalFuncLen - resultOfArgLen;
                    startAndEndEvalsForCorrectionIndexes[1] = startAndEndEvalsForCorrectionIndexes[1] - diff;
                }
                curCorrection++;
            }
            curElem++;
        }

        return eval;
    }

    private static int findIndexMaxDepthBrackets(String eval) {
        int maxDepthBrackets = 0;
        int counterBrackets = 0;
        int j = 0;
        int indexMaxDepthBrackets = 0;
        int len = eval.length();
        while (j != len) {
            char inner_c = eval.charAt(j);
            if (inner_c == '(') counterBrackets++;
            if (inner_c == ')') counterBrackets--;
            if (maxDepthBrackets < counterBrackets) {
                maxDepthBrackets = counterBrackets;
                indexMaxDepthBrackets = j;
            }
            j++;
        }
        return indexMaxDepthBrackets;
    }

    private static int findMaxDepthBrackets(String eval) {
        int maxDepthBrackets = 0;
        int counterBrackets = 0;
        int j = 0;
        int len = eval.length();
        while (j != len) {
            char inner_c = eval.charAt(j);
            if (inner_c == '(') counterBrackets++;
            if (inner_c == ')') counterBrackets--;
            if (maxDepthBrackets < counterBrackets) {
                maxDepthBrackets = counterBrackets;
            }
            j++;
        }
        return maxDepthBrackets;
    }

    private static int findNextCloseBracket(String eval, int start) {
        int j = start + 1;
        int len = eval.length();
        char c = eval.charAt(j);
        while (c != ')') {
            j++;
            if (j == len) {
                return -1;
            }
            c = eval.charAt(j);
        }
        return j;
    }

    private static boolean isOperator(char c) {
        return c == '^' || c == '/' || c == '*' || c == '+' || c == '-';
    }

    private static double extractFirstNumber(String subEval, int i) {
        int j = i - 1;
        if (j < 0) return 0;

        while (j >= 0) {
            char c = subEval.charAt(j);
            boolean isOperator = (c == '^') || (c == '/') || (c == '*') || (c == '+') || (c == '-');
            if (isOperator) {
                if (c == '-') {
                    if (j == 0 || (isOperator(subEval.charAt(j - 1)))) {
                        if (j > 0) j--;
                        continue;
                    }
                }
                break;
            }
            j--;
        }

        String numberStr = subEval.substring(j + 1, i);
        return parseNumber(numberStr);
    }

    private static int findStartIndexFirstNumber(String subEval, int i) {
        int j = i - 1;
        if (j < 0) return 0;

        while (j >= 0) {
            char c = subEval.charAt(j);
            boolean isOperator = (c == '^') || (c == '/') || (c == '*') || (c == '+') || (c == '-');
            if (isOperator) {
                if (c == '-') {
                    if (j == 0 || (j > 0 && isOperator(subEval.charAt(j - 1)))) {
                        if (j > 0) j--;
                        continue;
                    }
                }
                break;
            }
            j--;
        }
        return j + 1;
    }

    private static double extractSecondNumber(String subEval, int i) {
        int j = i + 1;
        int len = subEval.length();

        if (j >= len) return 0;

        boolean isNegative = false;
        if (subEval.charAt(j) == '-') {
            isNegative = true;
            j++;
        }
        int start = j;
        while (j < len) {
            char c = subEval.charAt(j);
            boolean isOperator = (c == '^') || (c == '/') || (c == '*') || (c == '+') || (c == '-');
            boolean isBracket = (c == '(') || (c == ')');

            if (isOperator || isBracket) {
                break;
            }

            j++;
        }

        String numberStr = subEval.substring(start, j);
        double result = parseNumber(numberStr);
        return isNegative ? -result : result;
    }

    private static int findLastIndexSecondNumber(String subEval, int i) {
        int j = i + 1;
        int len = subEval.length();

        if (j >= len) return i + 1;

        if (subEval.charAt(j) == '-') {
            j++;
        }

        while (j < len) {
            char c = subEval.charAt(j);
            boolean isOperator = (c == '^') || (c == '/') || (c == '*') || (c == '+') || (c == '-');
            boolean isBracket = (c == '(') || (c == ')');
            if (isOperator || isBracket) {
                break;
            }
            j++;
        }

        return j;
    }

    private static double evaluateSubEval(String subEval) {
        int[] opearations = {0, 0, 0, 0, 0};
        for (char c : subEval.toCharArray()) {
            if (c == '^') opearations[0]++;
            if (c == '/') opearations[1]++;
            if (c == '*') opearations[2]++;
            if (c == '+') opearations[3]++;
            if (c == '-') opearations[4]++;
        }

        int len = subEval.length();

        while (opearations[0] != 0) {
            opearations[0]--;
            int i = 0;
            while(i < len) {
                char c = subEval.charAt(i);
                if (c == '^') {
                    double firstNumber = extractFirstNumber(subEval, i);
                    double secondNumber = extractSecondNumber(subEval, i);
                    double resultOfOperation = Math.pow(firstNumber, secondNumber);
                    int firstIndex = findStartIndexFirstNumber(subEval, i);
                    int secondIndex = findLastIndexSecondNumber(subEval, i);
                    subEval = subEval.substring(0, firstIndex) + resultOfOperation + subEval.substring(secondIndex);
                    len = subEval.length();
                    break;
                }
                i++;
            }
        }

        while (opearations[1] != 0) {
            opearations[1]--;
            int i = 0;
            while(i < len) {
                char c = subEval.charAt(i);
                if (c == '/') {
                    double firstNumber = extractFirstNumber(subEval, i);
                    double secondNumber = extractSecondNumber(subEval, i);
                    double resultOfOperation = firstNumber / secondNumber;
                    int firstIndex = findStartIndexFirstNumber(subEval, i);
                    int secondIndex = findLastIndexSecondNumber(subEval, i);
                    if (Math.abs(secondNumber) < 1e-15) {
                        throw new ArithmeticException("Division by zero");
                    }
                    subEval = subEval.substring(0, firstIndex) + resultOfOperation + subEval.substring(secondIndex);
                    len = subEval.length();
                    break;
                }
                i++;
            }
        }

        while (opearations[2] != 0) {
            opearations[2]--;
            int i = 0;
            while(i < len) {
                char c = subEval.charAt(i);
                if (c == '*') {
                    double firstNumber = extractFirstNumber(subEval, i);
                    double secondNumber = extractSecondNumber(subEval, i);
                    double resultOfOperation = firstNumber * secondNumber;
                    int firstIndex = findStartIndexFirstNumber(subEval, i);
                    int secondIndex = findLastIndexSecondNumber(subEval, i);
                    subEval = subEval.substring(0, firstIndex) + resultOfOperation + subEval.substring(secondIndex);
                    len = subEval.length();
                    break;
                }
                i++;
            }
        }

        while (opearations[3] != 0) {
            opearations[3]--;
            int i = 0;
            while(i < len) {
                char c = subEval.charAt(i);
                if (c == '+') {
                    double firstNumber = extractFirstNumber(subEval, i);
                    double secondNumber = extractSecondNumber(subEval, i);
                    double resultOfOperation = firstNumber + secondNumber;
                    int firstIndex = findStartIndexFirstNumber(subEval, i);
                    int secondIndex = findLastIndexSecondNumber(subEval, i);
                    subEval = subEval.substring(0, firstIndex) + resultOfOperation + subEval.substring(secondIndex);
                    len = subEval.length();
                    break;
                }
                i++;
            }
        }

        while (opearations[4] != 0) {
            opearations[4]--;
            int i = 0;
            while(i < len) {
                char c = subEval.charAt(i);
                if (c == '-') {
                    double firstNumber = extractFirstNumber(subEval, i);
                    double secondNumber = extractSecondNumber(subEval, i);
                    double resultOfOperation = firstNumber - secondNumber;
                    int firstIndex = findStartIndexFirstNumber(subEval, i);
                    int secondIndex = findLastIndexSecondNumber(subEval, i);
                    subEval = subEval.substring(0, firstIndex) + resultOfOperation + subEval.substring(secondIndex);
                    len = subEval.length();
                    break;
                }
                i++;
            }
        }

        return parseNumber(subEval);
    }

    private static double evaluateWithoutVariables(String eval) {
        if (isNumeric(eval)) {
            return parseNumber(eval);
        }
        eval = calculateFunctions(eval);
        int maxDepth = findMaxDepthBrackets(eval);
        while (maxDepth != 0) {
            int indexCharStartEval = findIndexMaxDepthBrackets(eval);
            int indexCharEndEval = findNextCloseBracket(eval, indexCharStartEval);
            String subEval = eval.substring(indexCharStartEval + 1, indexCharEndEval);
            double valueOfSubEval = evaluateSubEval(subEval);
            eval = eval.substring(0, indexCharStartEval) + valueOfSubEval + eval.substring(indexCharEndEval + 1);
            maxDepth = findMaxDepthBrackets(eval);
        }

        return evaluateSubEval(eval);
    }


    public static double evaluate(String eval, Map<String, Double> variables) {
        eval = eval.replace(" ", "");
        boolean isValid = isValidExpression(eval);
        if (!isValid) {
            throw new IllegalArgumentException("expression is not correct.");
        }

        if (!variables.isEmpty()) {
            eval = replaceVariables(eval, variables);
        }

        return evaluateWithoutVariables(eval);
    }
}
