package com.cnp.sdk;

public class ErrorResponse extends Response {
    public Errors error;


    public Boolean isErrorResponse(){
        return true;
    }

    public class Errors{
        public String error;
    }
}

