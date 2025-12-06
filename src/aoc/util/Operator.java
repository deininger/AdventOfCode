package aoc.util;

import java.util.Arrays;

public enum Operator {
    PLUS('+'), MINUS('-'), DIVIDE('/'), TIMES('*');

    public static Operator getOperator(char symbol) {
        return Arrays.stream(Operator.values())
                .filter(operator -> operator.symbol() == symbol)
                .findFirst()
                .orElseThrow();
    }
    private final char symbol;

    Operator(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }

    public String toString() {
        return Character.toString(symbol);
    }

    public Long apply(Long a, Long b) {
        return switch (this) {
            case PLUS -> a + b;
            case MINUS -> a - b;
            case DIVIDE -> a / b;
            case TIMES -> a * b;
        };
    }
}
