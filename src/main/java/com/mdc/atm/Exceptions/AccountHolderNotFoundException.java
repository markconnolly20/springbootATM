package com.mdc.atm.Exceptions;

public class AccountHolderNotFoundException extends RuntimeException {

    public AccountHolderNotFoundException(int accountNumber) {
        super("Could not find accountHolder " + accountNumber);
    }
}
