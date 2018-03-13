package com.cnp.sdk;

import com.cnp.sdk.generate.ObjectFactory;
import org.junit.Test;

import javax.xml.bind.JAXBContext;

import static org.junit.Assert.assertSame;

public class TestCnpContext {

    @Test
    public void testGetJAXBContextReturnsSameObject() {
        JAXBContext context1 = CnpContext.getJAXBContext();
        JAXBContext context2 = CnpContext.getJAXBContext();

        assertSame(context1, context2);
    }

    @Test
    public void testGetObjectFactoryReturnsSameObject() {
        ObjectFactory factory1 = CnpContext.getObjectFactory();
        ObjectFactory factory2 = CnpContext.getObjectFactory();

        assertSame(factory1, factory2);
    }

}
