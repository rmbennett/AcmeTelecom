package com.acmetelecom;

import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Lawliet
 * Date: 11/8/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MoneyFormatterTest {
    @Test
    public void testPenceToPounds() throws Exception {
        assertEquals("MoneyFormatter can handle < £1", "0.63", MoneyFormatter.penceToPounds(new BigDecimal(63)));
        assertEquals("MoneyFormatter can handle > £1", "9.63", MoneyFormatter.penceToPounds(new BigDecimal(963)));
    }
}
