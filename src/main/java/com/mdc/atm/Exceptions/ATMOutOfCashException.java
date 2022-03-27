package com.mdc.atm.Exceptions;

public class ATMOutOfCashException extends RuntimeException {

    public ATMOutOfCashException(int accountNumber) {
        super("There is not enough cash left in the ATM to dispense this amount " + accountNumber);
    }
}
