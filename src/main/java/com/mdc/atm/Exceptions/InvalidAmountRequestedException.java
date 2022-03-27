package com.mdc.atm.Exceptions;

public class InvalidAmountRequestedException extends RuntimeException {

    public InvalidAmountRequestedException(int accountNumber) {
        super("Invalid amount requested on this machine " + accountNumber);
    }
}
