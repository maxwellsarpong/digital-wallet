package com.example.DigitalWallet.exception;

public class InsufficientFundsException extends RuntimeException{

    public InsufficientFundsException(String message){
        super(message);
    }
}
