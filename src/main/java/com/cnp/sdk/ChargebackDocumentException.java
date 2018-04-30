package com.cnp.sdk;

public class ChargebackDocumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String code;

    public ChargebackDocumentException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ChargebackDocumentException(String message, Exception ume) {
        super(message, ume);
        this.code = "0";
    }

    public ChargebackDocumentException(String message) {
        super(message);
        this.code = "0";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
