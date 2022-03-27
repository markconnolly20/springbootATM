package com.mdc.atm.Exceptions;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(int accountNumber) {
        super("Insufficient funds for accountHolder " + accountNumber);
    }
}
