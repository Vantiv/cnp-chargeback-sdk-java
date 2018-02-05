package com.cnp.sdk;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.cnp.sdk.generate.ObjectFactory;

/**
 * A factory that encapsulates singleton instances of a Cnp JAXB Context and ObjectFactory.
 * @author stephenhall
 *
 */
public class CnpContext {

    private static final JAXBContext jaxbContext = initJAXBContext();

    private static final ObjectFactory objectFactory = initObjectFactory();

    private static JAXBContext initJAXBContext() {
        try {
            return JAXBContext.newInstance("com.cnp.sdk.generate");
        } catch (JAXBException e) {
            throw new ChargebackException("Unable to load jaxb dependencies.  Perhaps a classpath issue?", e);
        }
    }

    private static ObjectFactory initObjectFactory() {
        return new ObjectFactory();
    }

    public static JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    public static ObjectFactory getObjectFactory() {
        return objectFactory;
    }

}