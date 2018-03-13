package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class TestChargebackUpdate {

    @Test
    public void testChargebackAssignCaseToUser(){
        ChargebackUpdate cbk = new ChargebackUpdate();
        ChargebackUpdateResponse re = cbk.assignCaseToUser(123L, "test_user", "Test assigned to user");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackAddNoteToCase(){
        ChargebackUpdate cbk = new ChargebackUpdate();
        ChargebackUpdateResponse re = cbk.addNoteToCase(123L, "Test added note");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackAssumeLiability(){
        ChargebackUpdate cbk = new ChargebackUpdate();
        ChargebackUpdateResponse re = cbk.assumeLiability(123L, "Test assumed liability");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackRepresent(){
        ChargebackUpdate cbk = new ChargebackUpdate();
        ChargebackUpdateResponse re = cbk.representCase(123L, 20L, "Test represented");
        assertNotNull(re.getTransactionId());
    }

    @Test
    public void testChargebackRespondToUpdateRequest(){
        ChargebackUpdate cbk = new ChargebackUpdate();
        ChargebackUpdateResponse re = cbk.respondToRetrievalRequest(123L, "Test responded to Update request");
        assertNotNull(re.getTransactionId());
    }
}
