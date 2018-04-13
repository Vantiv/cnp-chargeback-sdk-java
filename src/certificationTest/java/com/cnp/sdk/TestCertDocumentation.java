package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Prior to beginning the test sequence, your Implementation Consultant will establish a
 * test environment containing four chargeback cases. For these cases,
 * the <case Id> element in the URL will have the form yourMerchantId + 001 through 004.
 * For example, if your merchantId is 4321, then the first chargeback case Id would be 4321001.
 */

public class TestCertDocumentation {

    private ChargebackDocument cbkDocument;
    private ChargebackRetrieval cbkRetrieval;
    private ChargebackUpdate cbkUpdate;
    private String merchantId;
    private File documentToUpload1;
    private File documentToUpload2;
    private File documentToUpload3;
    private File documentToUpload4;

    @Before
    public void setup(){
        Properties config = (new Configuration()).getProperties();
        config.setProperty("url", "ChargebackDocument://services.vantivprelive.com/services/chargebacks/");
        merchantId = config.getProperty("merchantId");
        cbkRetrieval = new ChargebackRetrieval(config);
        cbkUpdate = new ChargebackUpdate(config);
        cbkDocument = new ChargebackDocument(config);
        documentToUpload1 = new File("test.jpg");
        documentToUpload2 = new File("test.gif");
        documentToUpload3 = new File("test.pdf");
        documentToUpload4 = new File("test.tiff");
    }

    @Test
    public void test1(){
        Long caseId = Long.valueOf(merchantId + "001");
        ChargebackDocumentUploadResponse documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload1);
        assertEquals("000", documentResponse.getResponseCode());
        assertEquals("Success", documentResponse.getResponseMessage());

        documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload2);
        assertEquals("000", documentResponse.getResponseCode());
        assertEquals("Success", documentResponse.getResponseMessage());

        documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload3);
        assertEquals("000", documentResponse.getResponseCode());
        assertEquals("Success", documentResponse.getResponseMessage());

        documentResponse = cbkDocument.listDocuments(caseId);
        List<String> documentList = documentResponse.getDocumentIds();
        assertTrue(documentList.contains(documentToUpload1.getName()));
        assertTrue(documentList.contains(documentToUpload2.getName()));
        assertTrue(documentList.contains(documentToUpload3.getName()));

        File documentToRetrieve = cbkDocument.retrieveDocument(caseId, documentToUpload1.getName(), "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();

        documentToRetrieve = cbkDocument.retrieveDocument(caseId, documentToUpload3.getName(), "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();

        documentToRetrieve = cbkDocument.retrieveDocument(caseId, documentToUpload3.getName(), "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();

        documentResponse = cbkDocument.replaceDocument(caseId, documentToUpload1.getName(), documentToUpload4);
        assertEquals("000", documentResponse.getResponseCode());
        assertEquals("Success", documentResponse.getResponseMessage());

        documentToRetrieve = cbkDocument.retrieveDocument(caseId, documentToUpload4.getName(), "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();

        documentResponse = cbkDocument.removeDocument(caseId, documentToUpload2.getName());
        assertEquals("000", documentResponse.getResponseCode());
        assertEquals("Success", documentResponse.getResponseMessage());

        documentResponse = cbkDocument.listDocuments(caseId);
        documentList = documentResponse.getDocumentIds();
        assertTrue(documentList.contains(documentToUpload4.getName()));
        assertTrue(documentList.contains(documentToUpload3.getName()));
    }

    @Test
    public void test2(){
        Long caseId = Long.valueOf(merchantId + "002");
        ChargebackDocumentUploadResponse documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload1);
        assertEquals("010", documentResponse.getResponseCode());
        assertEquals("Case not in valid cycle", documentResponse.getResponseMessage());
    }

    @Test
    public void test3(){
        Long caseId = Long.valueOf(merchantId + "004");
        ChargebackDocumentUploadResponse documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload1);
        assertEquals("004", documentResponse.getResponseCode());
        assertEquals("Case not in Merchant Queue", documentResponse.getResponseMessage());
    }

    @Test
    public void test4(){
        Long caseId = Long.valueOf(merchantId + "004");
        File maxsize = new File("maxsize.tif");
        ChargebackDocumentUploadResponse documentResponse = cbkDocument.uploadDocument(caseId, maxsize);
        assertEquals("005", documentResponse.getResponseCode());
        assertEquals("Document already exists", documentResponse.getResponseMessage());
        maxsize.delete();

        maxsize = new File("maxsize1.tif");
        try (FileOutputStream out = new FileOutputStream(maxsize)) {
            byte[] bytes = new byte[2050];
            new SecureRandom().nextBytes(bytes);
            out.write(bytes);
        } catch(IOException e){
            System.out.println(e);
        }

        documentResponse = cbkDocument.uploadDocument(caseId, maxsize);
        assertEquals("005", documentResponse.getResponseCode());
        assertEquals("Filesize exceeds limit of 1MB", documentResponse.getResponseMessage());
        maxsize.delete();

        documentResponse = cbkDocument.uploadDocument(caseId, documentToUpload1);
        assertEquals("008", documentResponse.getResponseCode());
        assertEquals("Max Document Limit Per Case Reached</", documentResponse.getResponseMessage());


    }

    @Test
    public void tearDown(){
        documentToUpload1.delete();
        documentToUpload2.delete();
        documentToUpload3.delete();
        documentToUpload4.delete();
    }

}
