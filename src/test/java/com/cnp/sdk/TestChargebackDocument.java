package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestChargebackDocument {

    private ChargebackDocument cbk;
    private File documentToUpload;

    @Before
    public void setup() throws IOException {
        cbk = new ChargebackDocument();
        documentToUpload = new File("test.tiff");
        documentToUpload.createNewFile();
    }

    @Test
    public void testChargebackUploadDocument(){
        String expectedRequestUrl = ".*?/upload/123000/test.tiff";
        String mockedResponse = "<chargebackDocumentUploadResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <merchantId>999</merchantId>\n" +
                "  <caseId>123000</caseId>\n" +
                "  <documentId>test.tiff</documentId>\n" +
                "  <responseCode>000</responseCode>\n" +
                "  <responseMessage>Success</responseMessage>\n" +
                "</chargebackDocumentUploadResponse>";
        ChargebackDocumentUploadResponse expectedResponse = XMLConverter.generateDocumentResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPostDocumentRequest(any(File.class), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackDocumentUploadResponse response = cbk.uploadDocument(123000L, documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackRetrieveDocument(){
        String expectedRequestUrl = ".*?/retrieve/123000/logo.tiff";
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpGetDocumentRequest(matches("test.tiff"), matches(expectedRequestUrl), any(Properties.class))).thenReturn(documentToUpload);

        cbk.setCommunication(mockedCommunication);
        assertNotNull(cbk);
        File documentToRetrieve = cbk.retrieveDocument(123000L, "logo.tiff", "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();
    }

    @Test
    public void testChargebackReplaceDocument(){
        String expectedRequestUrl = ".*?/replace/123000/test.tiff";
        String mockedResponse = "<chargebackDocumentUploadResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <merchantId>999</merchantId>\n" +
                "  <caseId>123000</caseId>\n" +
                "  <documentId>test.tiff</documentId>\n" +
                "  <responseCode>000</responseCode>\n" +
                "  <responseMessage>Success</responseMessage>\n" +
                "</chargebackDocumentUploadResponse>";
        ChargebackDocumentUploadResponse expectedResponse = XMLConverter.generateDocumentResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpPutDocumentRequest(any(File.class), matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackDocumentUploadResponse response = cbk.replaceDocument(123000L, "logo.tiff", documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackRemoveDocument(){
        String expectedRequestUrl = ".*?/remove/123000/logo.tiff";
        String mockedResponse = "<chargebackDocumentUploadResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <merchantId>999</merchantId>\n" +
                "  <caseId>123000</caseId>\n" +
                "  <documentId>logo.tiff</documentId>\n" +
                "  <responseCode>000</responseCode>\n" +
                "  <responseMessage>Success</responseMessage>\n" +
                "</chargebackDocumentUploadResponse>";
        ChargebackDocumentUploadResponse expectedResponse = XMLConverter.generateDocumentResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpDeleteDocumentRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackDocumentUploadResponse response = cbk.removeDocument(123000L, "logo.tiff");
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackListDocument(){
        String expectedRequestUrl = ".*?/list/123000";
        String mockedResponse = "<chargebackDocumentUploadResponse xmlns=\"http://www.vantivcnp.com/chargebacks\">\n" +
                "  <merchantId>999</merchantId>\n" +
                "  <caseId>123000</caseId>\n" +
                "  <documentId>logo.tiff</documentId>\n" +
                "  <documentId>doc.tiff</documentId>\n" +
                "  <responseCode>000</responseCode>\n" +
                "  <responseMessage>Success</responseMessage>\n" +
                "</chargebackDocumentUploadResponse>";
        ChargebackDocumentUploadResponse expectedResponse = XMLConverter.generateDocumentResponse(mockedResponse);
        Communication mockedCommunication = mock(Communication.class);
        when(mockedCommunication.httpGetDocumentListRequest(matches(expectedRequestUrl), any(Properties.class))).thenReturn(expectedResponse);

        cbk.setCommunication(mockedCommunication);
        ChargebackDocumentUploadResponse response = cbk.listDocuments(123000L);
        assertTrue(response.getDocumentIds().contains("logo.tiff"));
        assertTrue(response.getDocumentIds().contains("doc.tiff"));
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @After
    public void tearDown(){
        documentToUpload.delete();
    }
}
