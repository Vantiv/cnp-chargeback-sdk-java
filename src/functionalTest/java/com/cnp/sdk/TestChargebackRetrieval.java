package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import org.junit.Test;
import static junit.framework.Assert.assertNotNull;

public class TestChargebackRetrieval {

    @Test
    public void testChargebackByDate(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getChargebacksByDate("2018", "02", "15");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByFinancialImpact(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getChargebacksByFinancialImpact("2018", "02", "15", true);
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByActionable(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getActivityByActionable(true);
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByCaseId(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getActivityByCaseId(123L);
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByToken(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getActivityByToken("00000");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByCardNum(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getActivityByCardNum("1111000011110000", "03", "20");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackByARN(){
        ChargebackRetrieval cbk = new ChargebackRetrieval();
        ChargebackRetrievalResponse re = cbk.getActivityByARN("000000000");
        assertNotNull(re.getTransactionId());
    }
}
