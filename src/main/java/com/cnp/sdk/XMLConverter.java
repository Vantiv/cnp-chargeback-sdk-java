package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import com.cnp.sdk.generate.ChargebackUpdateRequest;
import com.cnp.sdk.generate.ChargebackUpdateResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLConverter {

    //TODO: handle JAXBexceptions

    public static ChargebackRetrievalResponse generateRetrievalResponse(String xmlResponse){
        ChargebackRetrievalResponse response = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChargebackRetrievalResponse.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            response= (ChargebackRetrievalResponse) jaxbUnmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static ChargebackUpdateResponse generateUpdateResponse(String xmlResponse){
        ChargebackUpdateResponse response = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChargebackUpdateResponse.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            response= (ChargebackUpdateResponse) jaxbUnmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String generateUpdateRequest(ChargebackUpdateRequest request){
        StringWriter sw = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChargebackUpdateRequest.class);
            jaxbContext.createMarshaller().marshal(request, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
}
