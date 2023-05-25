package org.vitaliistf.util;

import java.math.BigDecimal;

public class PrintFormatter {

    public static String formatNumber(double number) {
        String temp;
        if(Double.toString(number).length() < 9) {
            temp = Double.toString(number);
        } else {
            temp = String.format("%6.1e", number);
        }
        return String.format("%-9s", temp);
    }

    public static String formatPercent(double number) {
        return String.format("%.2f", number) + "%";
    }

    public static String formatString(String input, int spaces) {
        return String.format("%-" + spaces + "s", input);
    }

    public static String getFullLengthNumber(double number) {
        return BigDecimal.valueOf(number).toPlainString();
    }

    public static String formatDate(String date) {
        return date.substring(0, 10);
    }

}
