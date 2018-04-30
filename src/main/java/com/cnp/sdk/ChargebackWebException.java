package com.cnp.sdk;

import java.util.List;

public class ChargebackWebException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String code;
    private List<String> errorList;

    public ChargebackWebException(String message, String code, List<String> errorList) {
        super(message);
        this.code = code;
        this.errorList = errorList;
    }

    public ChargebackWebException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ChargebackWebException(String message, Exception ume) {
        super(message, ume);
        this.code = "0";
    }

    public ChargebackWebException(String message) {
        super(message);
        this.code = "0";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
