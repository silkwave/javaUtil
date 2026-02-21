package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Calculator {
    private final Map<String, BiFunction<Integer, Integer, Integer>> operations = new HashMap<>();

    public Calculator() {
        operations.put("add", this::add);
        operations.put("subtract", this::subtract);
        operations.put("multiply", this::multiply);
        operations.put("divide", this::divide);
    }

    public int calculate(String operation, int a, int b) {
        BiFunction<Integer, Integer, Integer> func = operations.get(operation);
        if (func == null) {
            throw new RuntimeException("지원하지 않는 연산입니다: " + operation);
        }
        return func.apply(a, b);
    }

    private int add(int a, int b) {
        return a + b;
    }

    private int subtract(int a, int b) {
        return a - b;
    }

    private int multiply(int a, int b) {
        return a * b;
    }

    private int divide(int a, int b) {
        return a / b;
    }
}
