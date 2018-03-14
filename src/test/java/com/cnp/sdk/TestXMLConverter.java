package com.cnp.sdk;

import com.cnp.sdk.generate.*;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class TestXMLConverter {

    @Test
    public void testGenerateRetrievalResponse(){
        String responseStr = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>123</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-02-15</dayIssuedByBank>\n" +
                "    <dayReceivedByVantivCnp>2018-02-15</dayReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-02-15</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>000000000</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-02-15</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-02-15</replyByDay>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";
        ChargebackRetrievalResponse response = XMLConverter.generateRetrievalResponse(responseStr);
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
    }

    @Test
    public void testGenerateUpdateResponse(){
        String responseStr = "<chargebackUpdateResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>21260530003675</transactionId>\n" +
                "</chargebackUpdateResponse>";
        ChargebackUpdateResponse response = XMLConverter.generateUpdateResponse(responseStr);
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
    }

    @Test
    public void testGenerateDocumentResponse(){
        String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<chargebackDocumentUploadResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <merchantId>999</merchantId>\n" +
                "  <caseId>123009</caseId>\n" +
                "  <documentId>logo.tiff</documentId>\n" +
                "  <responseCode>009</responseCode>\n" +
                "  <responseMessage>Document Not Found</responseMessage>\n" +
                "</chargebackDocumentUploadResponse>\n";
        ChargebackDocumentUploadResponse response = XMLConverter.generateDocumentResponse(xmlResponse);
        assertNotNull(response);
    }

    @Test
    public void testGenerateUpdateRequest(){
        ChargebackUpdateRequest request = new ChargebackUpdateRequest();
        request.setActivityType(ActivityType.ASSIGN_TO_USER);
        request.setAssignedTo("test_user");
        request.setNote("Test assigned to user");
        String expectedStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><chargebackUpdateRequest xmlns=\"http://www.vantivcnp.com/chargebacks\"><activityType>ASSIGN_TO_USER</activityType><assignedTo>test_user</assignedTo><note>Test assigned to user</note></chargebackUpdateRequest>";
        String requestStr = XMLConverter.generateUpdateRequest(request);
        assertNotNull(request);
        assertEquals(expectedStr, requestStr);
    }
}
