package com.cnp.sdk;

import java.util.List;

public class ChargebackRetrievalResponse extends Response{
    private String transactionId;
    private List<ChargebackCase> chargebackCaseList;

    public Boolean isChargebackRetrievalResponse(){
        return true;
    }

}
