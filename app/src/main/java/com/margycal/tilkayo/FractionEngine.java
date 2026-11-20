package com.margycal.tilkayo;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

final class FractionEngine {
    private record Fraction(BigInteger n, BigInteger d) {
        private Fraction {
            if (d.signum() == 0) throw new IllegalArgumentException("Знаменатель не может быть нулём");
            if (d.signum() < 0) {
                n = n.negate();
                d = d.negate();
            }
        }

        @Override public String toString() { return n + "/" + d; }
    }

    record Solution(String answer, String mixed, List<String> steps) {}

    static Solution solve(String a, String b, String c, String d, String operator) {
        Fraction first = parse(a, b);
        Fraction raw;
        String operation;
        if (operator.equals("↓")) {
            raw = first;
            operation = "Сокращаем " + first;
        } else {
            Fraction second = parse(c, d);
            BigInteger left = first.n.multiply(second.d);
            BigInteger right = second.n.multiply(first.d);
            BigInteger common = first.d.multiply(second.d);
            switch (operator) {
                case "+", "−" -> {
                    raw = new Fraction(operator.equals("+") ? left.add(right) : left.subtract(right), common);
                    operation = "Общий знаменатель " + common + ". Считаем (" + left + " " + operator + " " + right + ")/" + common;
                }
                case "×" -> {
                    raw = new Fraction(first.n.multiply(second.n), common);
                    operation = "Перемножаем числители и знаменатели. Получаем " + raw;
                }
                case "÷" -> {
                    if (second.n.signum() == 0) throw new IllegalArgumentException("Деление на ноль");
                    raw = new Fraction(left, first.d.multiply(second.n));
                    operation = "Переворачиваем вторую дробь и умножаем. Получаем " + raw;
                }
                default -> throw new IllegalArgumentException("Выберите действие");
            }
        }
        BigInteger gcd = raw.n.gcd(raw.d);
        Fraction answer = new Fraction(raw.n.divide(gcd), raw.d.divide(gcd));
        BigInteger[] parts = answer.n.abs().divideAndRemainder(answer.d);
        String mixed = parts[0].signum() > 0 && parts[1].signum() > 0
            ? (answer.n.signum() < 0 ? "−" : "") + parts[0] + " " + parts[1] + "/" + answer.d : "";
        return new Solution(answer.d.equals(BigInteger.ONE) ? answer.n.toString() : answer.toString(), mixed,
             Arrays.asList(operation, "НОД числителя и знаменателя равен " + gcd + ". Делим оба на " + gcd));
    }

    private static Fraction parse(String n, String d) {
        if (!n.trim().matches("[+−-]?[0-9]{1,18}") || !d.trim().matches("[+−-]?[0-9]{1,18}"))
            throw new IllegalArgumentException("Введите целые числа до 18 цифр");
        return new Fraction(new BigInteger(n.trim().replace('−', '-')), new BigInteger(d.trim().replace('−', '-')));
    }
}
