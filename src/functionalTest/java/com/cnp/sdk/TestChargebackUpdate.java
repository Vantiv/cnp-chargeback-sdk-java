package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


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
    public void testChargebackRepresentFull(){
        ChargebackUpdateResponse response = cbk.representCase(123L, "Test represented");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRespondToUpdateRequest(){
        ChargebackUpdateResponse response = cbk.respondToRetrievalRequest(123L, "Test responded to Update request");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRequestArbitration(){
        ChargebackUpdateResponse response = cbk.requestArbitration(123L, "Test responded to Update request");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testErrorResponse(){
        try{
            ChargebackUpdateResponse response = cbk.addNoteToCase(404L, "ErrorResponse");
            fail("Expected Exception");
        } catch (ChargebackException e){
            assertEquals("404 : Not Found - Could not find requested object.", e.getMessage());
        }
    }
}
