package com.azati.study.testcontainersdemo.exception;

public class MyItemNotFoundException extends RuntimeException {
    public MyItemNotFoundException(String message) {
        super(message);
    }
}
