package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackApiCase;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestChargebackRetrieval {

    private ChargebackRetrieval cbk;

    @Before
    public void before(){
        cbk = new ChargebackRetrieval();
    }

    @Test
    public void TestGetChargebacksByDate(){
        String expectedRequestUrl = ".*?chargebacks.*?/?date=2018-01-01";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getChargebacksByDate("2018-01-01");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
        assertEquals("2018-01-01", CalendarPrinter.printDate(case1.getDateReceivedByVantivCnp()));
    }

    @Test
    public void TestGetChargebacksByFinancialImpact(){

        String expectedRequestUrl = ".*?chargebacks.*?/?date=2018-01-01&financialOnly=true";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);

        ChargebackRetrievalResponse response = cbk.getChargebacksByFinancialImpact("2018-01-01", true);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
        assertEquals("2018-01-01", CalendarPrinter.printDate(case1.getDateReceivedByVantivCnp()));

    }

    @Test
    public void TestGetACtivityByACtionable(){
        String expectedRequestUrl = ".*?chargebacks.*?/?actionable=true";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByActionable(true);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());

    }

    @Test
    public void TestGetActivityByCaseId(){
        String expectedRequestUrl = ".*?chargebacks.*?/123";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>123</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByCaseId(123L);
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals(123L, case1.getCaseId().longValue());

    }

    @Test
    public void TestGetActivityByToken(){
        String expectedRequestUrl = ".*?chargebacks.*?/?token=00000";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>00000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByToken("00000");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
        assertEquals("00000", case1.getToken());

    }

    @Test
    public void TestGetActivityByCardNum(){
        String expectedRequestUrl = ".*?chargebacks.*?/?cardNumber=1111000011110000&expirationDate=0118";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>0000</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
                "    <chargebackType>Deposit</chargebackType>\n" +
                "    <representedAmount>2002</representedAmount>\n" +
                "    <representedCurrencyType>USD</representedCurrencyType>\n" +
                "    <reasonCode>4837</reasonCode>\n" +
                "    <reasonCodeDescription>No Cardholder Authorization</reasonCodeDescription>\n" +
                "    <currentQueue>Network Assumed</currentQueue>\n" +
                "    <fraudNotificationStatus>AFTER</fraudNotificationStatus>\n" +
                "    <acquirerReferenceNumber>2220000043980284700</acquirerReferenceNumber>\n" +
                "    <chargebackReferenceNumber>00143789</chargebackReferenceNumber>\n" +
                "    <merchantTxnId>600001</merchantTxnId>\n" +
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByCardNum("1111000011110000", "0118");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals("0000", case1.getCardNumberLast4());

    }

    @Test
    public void TestGetActivityByARN(){
        String expectedRequestUrl = ".*?chargebacks.*?/?arn=000000000";
        String mockedResponse = "<chargebackRetrievalResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <transactionId>1234567890</transactionId>\n" +
                "  <chargebackCase>\n" +
                "    <caseId>216000553703</caseId>\n" +
                "    <merchantId>Merchant01</merchantId>\n" +
                "    <dayIssuedByBank>2018-01-01</dayIssuedByBank>\n" +
                "    <dateReceivedByVantivCnp>2018-01-01</dateReceivedByVantivCnp>\n" +
                "    <vantivCnpTxnId>21200000820903</vantivCnpTxnId>\n" +
                "    <cycle>REPRESENTMENT</cycle>\n" +
                "    <orderId>TEST02.2</orderId>\n" +
                "    <cardNumberLast4>2203</cardNumberLast4>\n" +
                "    <cardType>MC</cardType>\n" +
                "    <chargebackAmount>2002</chargebackAmount>\n" +
                "    <chargebackCurrencyType>USD</chargebackCurrencyType>\n" +
                "    <originalTxnDay>2018-01-01</originalTxnDay>\n" +
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
                "    <fraudNotificationDate>2018-01-01</fraudNotificationDate>\n" +
                "    <bin>532499</bin>\n" +
                "    <token>10000000</token>\n" +
                "    <historicalWinPercentage>80</historicalWinPercentage>\n" +
                "    <customerId>123abc</customerId>\n" +
                "    <paymentAmount>3099</paymentAmount>\n" +
                "    <replyByDay>2018-01-01</replyByDay>\n" +
                "    <activity>\n" +
                "      <activityDate>2018-01-01</activityDate>\n" +
                "      <activityType>ASSIGN_TO_USER</activityType>\n" +
                "      <fromQueue>Vantiv</fromQueue>\n" +
                "      <toQueue>Merchant</toQueue>\n" +
                "      <settlementAmount>2002</settlementAmount>\n" +
                "      <settlementCurrencyType>USD</settlementCurrencyType>\n" +
                "      <notes>notes on activiy</notes>\n" +
                "    </activity>\n" +
                "  </chargebackCase>\n" +
                "</chargebackRetrievalResponse>";

        ChargebackRetrievalResponse expectedResponse = XMLConverter.generateRetrievalResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByARN("000000000");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertEquals("000000000", case1.getAcquirerReferenceNumber());
    }

    @Test(expected = ChargebackException.class)
    public void testErrorResponse(){
        String expectedRequestUrl = ".*?chargebacks.*?/500";

        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.getRetrievalRequest(matches(expectedRequestUrl), any(Properties.class))).thenThrow(new ChargebackException("500:Error"));

        cbk.setCommunication(mockedCommunication);
        ChargebackRetrievalResponse response = cbk.getActivityByCaseId(500L);
    }
}
