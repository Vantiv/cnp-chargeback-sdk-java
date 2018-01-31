package com.cnp.sdk;


public class ChargebackException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChargebackException(String message, Exception ume) {
        super(message, ume);
    }

    public ChargebackException(String message) {
        super(message);
    }

}