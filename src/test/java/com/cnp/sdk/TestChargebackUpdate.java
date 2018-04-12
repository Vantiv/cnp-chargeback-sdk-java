package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestChargebackUpdate {
    private ChargebackUpdate cbk = new ChargebackUpdate();

    @Test
    public void testChargebackAssignCaseToUser(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String expectedRequest = ".*?<chargebackUpdateRequest .*?><activityType>ASSIGN_TO_USER</activityType><assignedTo>test_user</assignedTo><note>Test assigned to user</note></chargebackUpdateRequest>";
        String mockedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse expectedResponse = XMLConverter.generateUpdateResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutUpdateRequest(matches(expectedRequest), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        ChargebackUpdateResponse response = cbk.assignCaseToUser(123L, "test_user", "Test assigned to user");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackAddNoteToCase(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String expectedRequest = ".*?<chargebackUpdateRequest .*?><activityType>ADD_NOTE</activityType><note>Test added note</note></chargebackUpdateRequest>";
        String mockedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse expectedResponse = XMLConverter.generateUpdateResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutUpdateRequest(matches(expectedRequest), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        ChargebackUpdateResponse response = cbk.addNoteToCase(123L, "Test added note");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackAssumeLiability(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String expectedRequest = ".*?<chargebackUpdateRequest .*?><activityType>MERCHANT_ACCEPTS_LIABILITY</activityType><note>Test assumed liability</note></chargebackUpdateRequest>";
        String mockedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse expectedResponse = XMLConverter.generateUpdateResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutUpdateRequest(matches(expectedRequest), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        ChargebackUpdateResponse response = cbk.assumeLiability(123L, "Test assumed liability");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRepresent(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String expectedRequest = ".*?<chargebackUpdateRequest .*?><activityType>MERCHANT_REPRESENT</activityType><note>Test represented</note><representedAmount>20</representedAmount></chargebackUpdateRequest>";
        String mockedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse expectedResponse = XMLConverter.generateUpdateResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutUpdateRequest(matches(expectedRequest), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        ChargebackUpdateResponse response = cbk.representCase(123L, 20L, "Test represented");
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testChargebackRespondToUpdateRequest(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String expectedRequest = ".*?<chargebackUpdateRequest .*?><activityType>MERCHANT_RESPOND</activityType><note>Test responded to Update request</note></chargebackUpdateRequest>";
        String mockedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse expectedResponse = XMLConverter.generateUpdateResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutUpdateRequest(matches(expectedRequest), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        ChargebackUpdateResponse response = cbk.respondToRetrievalRequest(123L, "Test responded to Update request");
        assertNotNull(response.getTransactionId());
    }
}
