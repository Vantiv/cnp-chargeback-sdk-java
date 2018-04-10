package com.cnp.sdk;

import com.cnp.sdk.generate.*;
import org.junit.Before;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestCert {

    private ChargebackRetrieval cbkRetrieval;
    private ChargebackUpdate cbkUpdate;

    @Before
    public void setup() throws IOException {
        Properties config = new Properties();
        FileInputStream fileInputStream = new FileInputStream((new Configuration()).location());
        config.load(fileInputStream);
        config.setProperty("url", "https://prelive.litle.com/");
        config.setProperty("proxyHost", "");
        config.setProperty("proxyPort", "");
        cbkRetrieval = new ChargebackRetrieval(config);
        cbkUpdate = new ChargebackUpdate(config);
    }

    @Test
    public void test1(){
        ChargebackRetrievalResponse response = cbkRetrieval.getChargebacksByDate("2013", "01", "01");
        List<ChargebackApiCase> cases = response.getChargebackCases();

        Collections.sort(cases, new Comparator<ChargebackApiCase>() {
            @Override
            public int compare(ChargebackApiCase o1, ChargebackApiCase o2) {
                return o1.getAcquirerReferenceNumber().compareTo(o2.getAcquirerReferenceNumber());
            }
        });

        assertEquals(11, cases.size());

        testChargebackCase(cases.get(0), "1111111111", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(1), "2222222222", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(2), "3333333333", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(3), "4444444444", "Generic", "FIRST_CHARGBACK");
        testChargebackCase(cases.get(4), "5555555550", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(5), "5555555551", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(6), "5555555552", "Visa Collaboration", "PRE_ARB_CHARGBACK");
        testChargebackCase(cases.get(7), "6666666660", "Visa Collaboration", "ARBITRATION_CHARGEBACK");
        testChargebackCase(cases.get(8), "7777777770", "Visa Allocation", "ISSUER_DECLINE_PRESAB");
        testChargebackCase(cases.get(9), "7777777771", "Visa Allocation", "ISSUER_DECLINE_PRESAB");
        testChargebackCase(cases.get(10), "7777777772", "Visa Allocation", "ISSUER_DECLINE_PRESAB");


    }

    @Test
    public void test2(){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByARN("1111111111");
        ChargebackApiCase caseBefore = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesBefore = caseBefore.getActivities();
        Long caseId = caseBefore.getCaseId();


        String note = "Cert test2: " + System.currentTimeMillis();
        ChargebackUpdateResponse updateResponse = cbkUpdate.addNoteToCase(caseId, note);

        retrievalResponse = cbkRetrieval.getActivityByARN("1111111111");
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();

        boolean activityFound = false;

        for (ChargebackApiActivity activity: activitiesAfter) {
            if (activity.getNotes().equals(note)) {
                assertEquals(ActivityType.ADD_NOTE.toString(), activity.getActivityType());
                activityFound = true;
            }
        }

        assertTrue(activityFound);

    }

    @Test
    public void test3_1(){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByARN("2222222222");
        ChargebackApiCase caseBefore = retrievalResponse.getChargebackCases().get(0);
//        List<ChargebackApiActivity> activitiesBefore = caseBefore.getActivities();
        Long caseId = caseBefore.getCaseId();

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, "Cert test3_1");

        retrievalResponse = cbkRetrieval.getActivityByARN("2222222222");
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();
        ChargebackApiActivity activity = activitiesAfter.get(activitiesAfter.size() -1);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals("Cert test3_1", activity.getNotes());

    }

    @Test
    public void test3_2(){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByARN("3333333333");
        ChargebackApiCase caseBefore = retrievalResponse.getChargebackCases().get(0);
//        List<ChargebackApiActivity> activitiesBefore = caseBefore.getActivities();
        Long caseId = caseBefore.getCaseId();

        ChargebackUpdateResponse updateResponse = cbkUpdate.representCase(caseId, 10027L,"Cert test3_2");

        retrievalResponse = cbkRetrieval.getActivityByARN("3333333333");
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();
        ChargebackApiActivity activity = activitiesAfter.get(activitiesAfter.size() -1);

        assertEquals(ActivityType.MERCHANT_REPRESENT.toString(), activity.getActivityType());
        assertEquals(new Long(10027L), activity.getSettlementAmount());
        assertEquals("Cert test3_2", activity.getNotes());

    }

    @Test
    public void test4(){
        ChargebackRetrievalResponse retrievalResponse = cbkRetrieval.getActivityByARN("4444444444");
        ChargebackApiCase caseBefore = retrievalResponse.getChargebackCases().get(0);
//        List<ChargebackApiActivity> activitiesBefore = caseBefore.getActivities();
        Long caseId = caseBefore.getCaseId();

        ChargebackUpdateResponse updateResponse = cbkUpdate.assumeLiability(caseId, "Cert test4");

        retrievalResponse = cbkRetrieval.getActivityByARN("4444444444");
        ChargebackApiCase caseAfter = retrievalResponse.getChargebackCases().get(0);
        List<ChargebackApiActivity> activitiesAfter = caseAfter.getActivities();
        ChargebackApiActivity activity = activitiesAfter.get(activitiesAfter.size() -1);

        assertEquals(ActivityType.MERCHANT_ACCEPTS_LIABILITY.toString(), activity.getActivityType());
        assertEquals("Cert test3_2", activity.getNotes());

    }


    @Test
    private void testChargebackCase(ChargebackApiCase cbkcase, String arn, String cbkflow, String casecycle){
        assertEquals(arn, cbkcase.getAcquirerReferenceNumber());
//        assertEquals(cbkflow, cbkcase.get());
        assertEquals(casecycle, cbkcase.getCycle());
    }
}
