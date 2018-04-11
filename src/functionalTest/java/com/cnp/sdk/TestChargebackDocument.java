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
        cbk = new ChargebackDocument(config);
        documentToUpload = new File("test.tiff");
        documentToUpload.createNewFile();
    }

    @Test
    public void testChargebackUploadDocument(){
        ChargebackDocumentUploadResponse response = cbk.uploadDocument(123000L, documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackRetrieveDocument(){
        assertNotNull(cbk);
        File documentToRetrieve = cbk.retrieveDocument(123000L, "logo.tiff", "test.tiff");
        assertTrue(documentToRetrieve.exists());
    }

    @Test
    public void testChargebackReplaceDocument(){
        ChargebackDocumentUploadResponse response = cbk.replaceDocument(123000L, documentToUpload);
        assertEquals("000", response.getResponseCode());
        assertEquals("Success", response.getResponseMessage());
    }

    @Test
    public void testChargebackDeleteDocument(){
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

    @After
    public void tearDown(){
        documentToUpload.delete();
    }
}
