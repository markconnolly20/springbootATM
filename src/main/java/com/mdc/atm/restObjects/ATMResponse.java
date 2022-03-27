package com.mdc.atm.restObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ATMResponse {

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("max_withdrawal_amount")
    private String maxWithdrawalAmount;

    @JsonProperty("notes_dispensed")
    private String notesDispensed;

    @JsonProperty("message")
    private String message;

    // A default constructor is required for serialization/deserialization to work
    public ATMResponse() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getMaxWithdrawalAmount() {
        return maxWithdrawalAmount;
    }

    public void setMaxWithdrawalAmount(String maxWithdrawalAmount) {
        this.maxWithdrawalAmount = maxWithdrawalAmount;
    }

    public String getNotesDispensed() {
        return notesDispensed;
    }

    public void setNotesDispensed(String notesDispensed) {
        this.notesDispensed = notesDispensed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
