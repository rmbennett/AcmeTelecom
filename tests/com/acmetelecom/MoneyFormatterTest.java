package com.acmetelecom;

import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;

public class MoneyFormatterTest {
    @Test
    public void testPenceToPounds() throws Exception {
        assertEquals("MoneyFormatter can handle < £1", "0.63", MoneyFormatter.penceToPounds(new BigDecimal(63)));
        assertEquals("MoneyFormatter can handle > £1", "9.63", MoneyFormatter.penceToPounds(new BigDecimal(963)));
    }
}
