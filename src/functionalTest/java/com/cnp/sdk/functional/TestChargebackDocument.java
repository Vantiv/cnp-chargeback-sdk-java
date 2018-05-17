package com.cnp.sdk.functional;

import com.cnp.sdk.*;
import com.cnp.sdk.generate.ChargebackDocumentUploadResponse;
import com.cnp.sdk.generate.ChargebackUpdateResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestChargebackDocument {

    private ChargebackDocument cbk;
    private File documentToUpload;

    @Before
    public void setup() throws IOException {
        String username = "merchant1";
        String password = "password";
        Properties config = new Properties();
        FileInputStream fileInputStream = new FileInputStream((new Configuration()).location());
        config.load(fileInputStream);
        fileInputStream.close();
        config.setProperty("username", username);
        config.setProperty("password", password);
        config.setProperty("printXml", "true");

        cbk = new ChargebackDocument(config);
        documentToUpload = new File("test.tiff");
        documentToUpload.createNewFile();
    }

//    @Test
//    public void testChargebackUploadDocument(){
//        ChargebackDocumentUploadResponse response = cbk.uploadDocument(123000L, documentToUpload);
//        assertEquals("000", response.getResponseCode());
//        assertEquals("Success", response.getResponseMessage());
//    }

    @Test
    public void testChargebackRetrieveDocument(){
        File documentToRetrieve = cbk.retrieveDocument(123000L, "logo.tiff", "test.tiff");
        assertTrue(documentToRetrieve.exists());
        documentToRetrieve.delete();
    }

//    @Test
//    public void testChargebackReplaceDocument(){
//        ChargebackDocumentUploadResponse response = cbk.replaceDocument(123000L, "logo.tiff", documentToUpload);
//        assertEquals("000", response.getResponseCode());
//        assertEquals("Success", response.getResponseMessage());
//    }

    @Test
    public void testChargebackRemoveDocument(){
        ChargebackDocumentUploadResponse response = cbk.deleteDocument(123000L, "logo.tiff");
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackListDocument(){
        ChargebackDocumentUploadResponse response = cbk.listDocuments(123000L);
        assertTrue(response.getDocumentIds().contains("logo.tiff"));
        assertTrue(response.getDocumentIds().contains("doc.tiff"));
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testErrorResponse(){
        ChargebackDocumentUploadResponse response = cbk.uploadDocument(123001L, documentToUpload);
        assertEquals("001", response.getResponseCode());
        assertEquals("Invalid Merchant", response.getResponseMessage());

        try{
            cbk.retrieveDocument(123000L, "logo.tiff", "test.tiff");
            fail("Expected Exception");
        } catch (ChargebackDocumentException e){
            assertEquals("Document Not Found", e.getMessage());
            assertEquals("009", e.getCode());
        }

        try{
            cbk.retrieveDocument(123000L, "logo.tiff", "test.tiff");
            fail("Expected Exception");
        } catch (ChargebackWebException e){
            assertEquals("Could not find requested object.", e.getMessage());
            assertEquals("404", e.getCode());
        }
    }

    @After
    public void tearDown(){
        documentToUpload.delete();
    }
}
