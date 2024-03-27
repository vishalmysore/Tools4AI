package com.t4a.processor;

public class AIProcessingException extends Exception {
    public AIProcessingException(Exception e){
        super(e);
    }
    public AIProcessingException(String e) {
        super(e);
    }
}
