package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TestChargebackDocument {

    @Test
    public void testChargebackUploadDocument(){

        ChargebackDocument cbk = new ChargebackDocument();
        File documentToUpload = new File("test.txt");
        ChargebackDocumentUploadResponse response = cbk.uploadDocument(123L, documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackRetrieveDocument(){
        ChargebackDocument cbk = new ChargebackDocument();
        ChargebackDocumentUploadResponse response = cbk.retrieveDocument(123L, "test.txt", "text.txt");
        File documentToRetrieve = new File("test.txt");
        assertTrue(documentToRetrieve.exists());
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackReplaceDocument(){
        ChargebackDocument cbk = new ChargebackDocument();
        File documentToUpload = new File("test.txt");
        ChargebackDocumentUploadResponse response = cbk.replaceDocument(123L, documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackDeleteDocument(){
        ChargebackDocument cbk = new ChargebackDocument();
        ChargebackDocumentUploadResponse response = cbk.deleteDocument(123L, "test.txt");
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackListDocument(){
        ChargebackDocument cbk = new ChargebackDocument();
        ChargebackDocumentUploadResponse response = cbk.listDocuments(123L);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }
}
