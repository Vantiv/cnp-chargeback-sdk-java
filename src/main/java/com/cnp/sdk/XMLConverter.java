package com.cnp.sdk;

import com.cnp.sdk.generate.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLConverter {

    public static ChargebackRetrievalResponse generateRetrievalResponse(String xmlResponse){
        ChargebackRetrievalResponse response;
        try {
            response = (ChargebackRetrievalResponse) CnpContext.getJAXBContext().createUnmarshaller().unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            throw new ChargebackException("Error validating xml data against the schema", e);
        }
        return response;
    }

    public static ChargebackUpdateResponse generateUpdateResponse(String xmlResponse){
        ChargebackUpdateResponse response;
        try {
            response = (ChargebackUpdateResponse) CnpContext.getJAXBContext().createUnmarshaller().unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            throw new ChargebackException("Error validating xml data against the schema", e);
        }
        return response;
    }

    public static ChargebackDocumentUploadResponse generateDocumentResponse(String xmlResponse){
        ChargebackDocumentUploadResponse response;
        try {
            response = (ChargebackDocumentUploadResponse) CnpContext.getJAXBContext().createUnmarshaller().unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            throw new ChargebackException("Error validating xml data against the schema", e);
        }
        return response;
    }

    public static String generateUpdateRequest(ChargebackUpdateRequest request){
        StringWriter sw = new StringWriter();
        try {
            CnpContext.getJAXBContext().createMarshaller().marshal(request, sw);
        } catch (JAXBException e) {
            throw new ChargebackException("Error validating xml data against the schema", e);
        }
        return sw.toString();
    }

    public static ErrorResponse generateErrorResponse(String xmlResponse){
        ErrorResponse response;
        try {
            response = (ErrorResponse) CnpContext.getJAXBContext().createUnmarshaller().unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            throw new ChargebackException("Error validating xml data against the schema", e);
        }
        return response;
    }
}
