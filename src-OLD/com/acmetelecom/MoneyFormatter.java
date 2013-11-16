package com.acmetelecom;

import java.math.BigDecimal;

class MoneyFormatter {

    // This class only has static methods. Make it non-inheritable, and instantiable
    private MoneyFormatter() {
    }

    public static String penceToPounds(BigDecimal pence) {
        BigDecimal pounds = pence.divide(new BigDecimal(100));
        return String.format("%.2f", pounds.doubleValue());
    }
}
