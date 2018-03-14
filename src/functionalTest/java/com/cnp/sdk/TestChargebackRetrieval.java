package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackApiCase;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class TestChargebackRetrieval {

    private ChargebackRetrieval cbk = new ChargebackRetrieval();

    @Test
    public void testChargebackByDate(){
        ChargebackRetrievalResponse response = cbk.getChargebacksByDate("2018", "02", "15");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
        assertEquals("2018-02-15", CalendarPrinter.printDate(case1.getDateReceivedByVantivCnp()));
    }

    @Test
    public void testChargebackByFinancialImpact(){
        ChargebackRetrievalResponse response = cbk.getChargebacksByFinancialImpact("2018", "02", "15", true);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
        assertEquals("2018-02-15", CalendarPrinter.printDate(case1.getDateReceivedByVantivCnp()));
    }

    @Test
    public void testActivityByActionable(){
        ChargebackRetrievalResponse response = cbk.getActivityByActionable(true);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
    }

    @Test
    public void testActivityByCaseId(){
        ChargebackRetrievalResponse response = cbk.getActivityByCaseId(123L);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals(123L, case1.getCaseId().longValue());
    }

    @Test
    public void testActivityByToken(){
        ChargebackRetrievalResponse response = cbk.getActivityByToken("00000");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
    }

    @Test
    public void testActivityByCardNum(){
        ChargebackRetrievalResponse response = cbk.getActivityByCardNum("1111000011110000", "20", "03");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals("0000", case1.getCardNumberLast4());
    }

    @Test
    public void testActivityByARN(){
        ChargebackRetrievalResponse response = cbk.getActivityByARN("000000000");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals("000000000", case1.getAcquirerReferenceNumber());
    }

    @Test(expected = ChargebackException.class)
    public void testErrorResponse(){
        ChargebackRetrievalResponse response = cbk.getActivityByCaseId(500L);
    }
}
