package com.cnp.sdk;

import com.cnp.sdk.generate.ChargebackApiCase;
import com.cnp.sdk.generate.ChargebackRetrievalResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class TestCertRetrieval {

    private ChargebackRetrieval cbk;

    @Before
    public void setup() throws IOException {
        Properties config = new Properties();
        FileInputStream fileInputStream = new FileInputStream((new Configuration()).location());
        config.load(fileInputStream);
        config.setProperty("url", "https://prelive.litle.com/");
        cbk = new ChargebackRetrieval(config);
    }

    @Test
    public void test1(){
        ChargebackRetrievalResponse response = cbk.getChargebacksByDate("2013", "01", "01");
        assertNotNull(response.getTransactionId());
        ChargebackApiCase case1 = response.getChargebackCases().get(0);
        assertNotNull(case1);
        assertNotNull(case1.getCaseId());
    }
}
