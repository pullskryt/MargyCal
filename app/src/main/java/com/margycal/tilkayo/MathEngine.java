package com.margycal.tilkayo;

import java.math.*;
import java.util.*;

final class MathEngine {
    static final MathContext PRECISION = MathContext.DECIMAL128;

    static String format(BigDecimal n) {
        return n.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    static String calculate(String input) {
        String trimmed = input.trim();
        if (!isLetterInput(trimmed)) return format(evaluate(input));
        if (trimmed.equals("цлтплцтп")) return "цлтпцдпй";

        int[] letters = trimmed.codePoints().filter(Character::isLetter).toArray();
        Random random = new Random();
        StringBuilder reply = new StringBuilder();
        int length = Math.min(32, Math.max(5, letters.length));
        for (int i = 0; i < length; i++) {
            reply.appendCodePoint(letters[random.nextInt(letters.length)]);
        }
        if (reply.toString().equals(trimmed)) reply.appendCodePoint(letters[0]);
        return reply.toString();
    }

    static boolean isLetterInput(String input) {
        return !input.isEmpty() && input.length() <= 250
            && input.codePoints().anyMatch(Character::isLetter)
            && input.codePoints().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c));
    }

    static BigDecimal evaluate(String input) {
        if (input.length() > 250) throw new IllegalArgumentException("Слишком длинное выражение");
        Parser parser = new Parser(input.replace(" ", "").replace(',', '.').replace('×', '*').replace('÷', '/').replace('−', '-'));
        BigDecimal result = parser.sum();
        if (parser.pos != parser.source.length()) throw new IllegalArgumentException("Проверьте выражение");
        return result;
    }

    private static final class Parser {
        final String source;
        int pos;
        Parser(String source) { this.source = source; }
        boolean take(char c) {
            if (pos >= source.length() || source.charAt(pos) != c) return false;
            pos++;
            return true;
        }
        BigDecimal sum() {
            BigDecimal value = product();
            while (true) {
                if (take('+')) value = value.add(product(), PRECISION);
                else if (take('-')) value = value.subtract(product(), PRECISION);
                else return value;
            }
        }
        BigDecimal product() {
            BigDecimal value = unary();
            while (true) {
                if (take('*')) value = value.multiply(unary(), PRECISION);
                else if (take('/')) {
                    BigDecimal divisor = unary();
                    if (divisor.signum() == 0) throw new IllegalArgumentException("Деление на ноль");
                    value = value.divide(divisor, PRECISION);
                } else return value;
            }
        }
        BigDecimal unary() {
            if (take('+')) return unary();
            if (take('-')) return unary().negate();
            BigDecimal value;
            if (take('(')) {
                value = sum();
                if (!take(')')) throw new IllegalArgumentException("Закройте скобку");
            } else {
                int start = pos;
                while (pos < source.length()) {
                    char c = source.charAt(pos);
                    if ((c < '0' || c > '9') && c != '.') break;
                    pos++;
                }
                try { value = new BigDecimal(source.substring(start, pos), PRECISION); }
                catch (NumberFormatException e) { throw new IllegalArgumentException("Введите число"); }
            }
            while (take('%')) value = value.movePointLeft(2);
            return value;
        }
    }

    record Column(String layout, String answer, List<String> steps) {}

    static Column column(String first, String second, String op) {
        String a = integer(first), b = integer(second);
        BigInteger x = new BigInteger(a), y = new BigInteger(b);
        ArrayList<String> steps = new ArrayList<>();
        if (op.equals("÷")) {
            if (y.signum() == 0) throw new IllegalArgumentException("Деление на ноль");
            BigInteger remainder = BigInteger.ZERO;
            StringBuilder quotient = new StringBuilder();
            StringBuilder layout = new StringBuilder();
            boolean started = false;
            for (int i = 0; i < a.length(); i++) {
                BigInteger current = remainder.multiply(BigInteger.TEN).add(BigInteger.valueOf(a.charAt(i) - '0'));
                BigInteger[] part = current.divideAndRemainder(y);
                quotient.append(part[0]);
                started = started || part[0].signum() != 0;
                if (started || i == a.length() - 1) {
                    String product = part[0].multiply(y).toString();
                    layout.append(repeat(' ', Math.max(0, i + 1 - current.toString().length()))).append(current).append('\n')
                        .append(repeat(' ', Math.max(0, i + 1 - product.length()))).append(product).append("  −\n")
                        .append(repeat('─', i + 1)).append('\n');
                }
                steps.add("Сносим " + a.charAt(i) + ": " + current + " ÷ " + b + " → " + part[0] + ", остаток " + part[1]);
                remainder = part[1];
            }
            String whole = new BigInteger(quotient.toString()).toString();
            String answer = whole + (remainder.signum() == 0 ? "" : " (ост. " + remainder + ")");
            layout.insert(0, a + " │ " + b + "\n" + repeat(' ', a.length()) + " ├"
                + repeat('─', Math.max(b.length(), whole.length()) + 1) + "\n"
                + repeat(' ', a.length()) + " │ " + whole + "\n");
            layout.append(right(remainder.toString(), a.length()));
            return new Column(layout.toString(), answer, steps);
        }
        BigInteger result = switch (op) {
            case "+" -> x.add(y);
            case "−" -> x.subtract(y);
            case "×" -> x.multiply(y);
            default -> throw new IllegalArgumentException("Выберите действие");
        };
        String top = a, bottom = b;
        if (op.equals("−") && result.signum() < 0) {
            top = b;
            bottom = a;
            steps.add("Меняем числа местами и ставим минус перед результатом.");
        }
        int width = Math.max(Math.max(a.length(), b.length()), result.toString().length()) + 2;
        StringBuilder layout = new StringBuilder(right(top, width)).append('\n').append(op).append(right(bottom, width - 1))
            .append('\n').append(repeat('─', width)).append('\n');
        if (op.equals("×")) {
            for (int i = 0; i < b.length(); i++) {
                int digit = digit(b, i);
                BigInteger product = x.multiply(BigInteger.valueOf(digit)).multiply(BigInteger.TEN.pow(i));
                if (b.length() > 1) layout.append(right(product.toString(), width)).append('\n');
                steps.add(a + " × " + digit + " × 10^" + i + " = " + product);
            }
            if (b.length() > 1) layout.append(repeat('─', width)).append('\n');
        } else {
            int carry = 0;
            for (int i = 0; i < Math.max(top.length(), bottom.length()); i++) {
                int left = digit(top, i), right = digit(bottom, i);
                boolean plus = op.equals("+");
                int value = plus ? left + right + carry : left - right - carry;
                String before = carry == 0 ? "" : plus ? " + 1" : " − 1";
                carry = plus ? value / 10 : value < 0 ? 1 : 0;
                steps.add(left + " " + op + " " + right + before + " → " + Math.floorMod(value, 10)
                    + (carry == 0 ? "" : plus ? ", переносим 1" : ", занимаем 1"));
            }
            if (op.equals("+") && carry == 1) steps.add("Записываем перенос 1 в старший разряд.");
        }
        layout.append(right(result.toString(), width));
        return new Column(layout.toString(), result.toString(), steps);
    }

    private static String repeat(char value, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, value);
        return new String(chars);
    }

    private static int digit(String value, int offset) {
        return offset < value.length() ? value.charAt(value.length() - 1 - offset) - '0' : 0;
    }
    private static String right(String value, int width) {
        return repeat(' ', Math.max(0, width - value.length())) + value;
    }
    private static String integer(String value) {
        if (!value.trim().matches("[0-9]{1,14}")) throw new IllegalArgumentException("Введите целое неотрицательное число (до 14 цифр)");
        return new BigInteger(value.trim()).toString();
    }

    record Month(int number, BigDecimal interest, BigDecimal total) {}
    record Deposit(BigDecimal paid, BigDecimal interest, BigDecimal total, List<Month> schedule) {}

    static Deposit deposit(String principal, String contribution, String rate, String term, boolean compound) {
        BigDecimal balance = decimal(principal, "начальную сумму", new BigDecimal("1000000000000"), 2);
        BigDecimal topUp = decimal(contribution, "пополнение", new BigDecimal("1000000000000"), 2);
        BigDecimal monthlyRate = decimal(rate, "ставку", new BigDecimal("1000"), 4).divide(BigDecimal.valueOf(1200), PRECISION);
        int count;
        try { count = Integer.parseInt(term.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Укажите срок от 1 до 360 месяцев"); }
        if (count < 1 || count > 360) throw new IllegalArgumentException("Укажите срок от 1 до 360 месяцев");
        BigDecimal earned = BigDecimal.ZERO;
        ArrayList<Month> schedule = new ArrayList<>();
        for (int month = 1; month <= count; month++) {
            BigDecimal base = compound ? balance.add(earned) : balance;
            BigDecimal interest = base.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            earned = earned.add(interest);
            balance = balance.add(topUp);
            schedule.add(new Month(month, interest, balance.add(earned)));
        }
        return new Deposit(balance, earned, balance.add(earned), schedule);
    }

    private static BigDecimal decimal(String raw, String label, BigDecimal max, int scale) {
        String value = raw.trim().replace(" ", "").replace(',', '.');
        if (!value.matches("[0-9]{1,13}(\\.[0-9]{1," + scale + "})?")) throw new IllegalArgumentException("Проверьте " + label);
        BigDecimal number = new BigDecimal(value);
        if (number.compareTo(max) > 0) throw new IllegalArgumentException("Проверьте " + label);
        return number;
    }
}
