package calculator;

public class CalculatorExample {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        System.out.println("10 + 5 = " + calculator.calculate("add", 10, 5));
        System.out.println("10 - 5 = " + calculator.calculate("subtract", 10, 5));
        System.out.println("10 * 5 = " + calculator.calculate("multiply", 10, 5));
        System.out.println("10 / 5 = " + calculator.calculate("divide", 10, 5));
        System.out.println("10 % 5 = " + calculator.calculate("modulus", 10, 5));  // 존재하지 않는 연산
    }
}
