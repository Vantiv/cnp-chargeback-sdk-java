package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;


public class TestChargebackUpdate {

    private ChargebackUpdate cbk = new ChargebackUpdate();

    @Test
    public void testChargebackAssignCaseToUser(){
        ChargebackUpdateResponse response = cbk.assignCaseToUser(123L, "test_user", "Test assigned to user");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackAddNoteToCase(){
        ChargebackUpdateResponse response = cbk.addNoteToCase(123L, "Test added note");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackAssumeLiability(){
        ChargebackUpdateResponse response = cbk.assumeLiability(123L, "Test assumed liability");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRepresent(){
        ChargebackUpdateResponse response = cbk.representCase(123L, 20L, "Test represented");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRespondToUpdateRequest(){
        ChargebackUpdateResponse response = cbk.respondToRetrievalRequest(123L, "Test responded to Update request");
        assertNotNull(response.getTransactionId());
    }
}
