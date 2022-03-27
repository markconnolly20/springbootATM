package com.mdc.atm.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.CreationTimestamp;

import javax.money.MonetaryAmount;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_holder")

@Builder
//The following are required by the JPA contract
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountHolder {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull(message = "Account Number is required")
    private int accountNumber;

    @NotNull(message = "PIN is required")
    private int PIN;

    @Columns(columns = {@Column(name = "opening_balance_amount"), @Column(name = "opening_balance_currency")})
    @NotNull(message = "Opening Balance is required")
    private MonetaryAmount openingBalance;

    @Columns(columns = {@Column(name = "overdraft_amount"), @Column(name = "overdraft_currency")})
    @NotNull(message = "Overdraft is required")
    private MonetaryAmount overdraft;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }

    public MonetaryAmount getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(MonetaryAmount openingBalance) {
        this.openingBalance = openingBalance;
    }

    public MonetaryAmount getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(MonetaryAmount overdraft) {
        this.overdraft = overdraft;
    }

    @Override
    public String toString() {
        return "AccountHolder{" + "id=" + this.id + ", accountNumber='" + this.accountNumber + '}';
    }

}
