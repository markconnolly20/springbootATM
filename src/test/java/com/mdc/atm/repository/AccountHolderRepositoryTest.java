package com.mdc.atm.repository;

import com.mdc.atm.domain.AccountHolder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AccountHolderRepositoryTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Test
    void findByAccountNumber() {

        CurrencyUnit eur = Monetary.getCurrency("EUR");

        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(1400, eur),Money.of(500, eur));
        AccountHolder account2 = createAccountHolder(44444,4321,Money.of(1500, eur),Money.of(1000, eur));

        accountHolderRepository.saveAll(List.of(account1, account2));

        AccountHolder account1Details = accountHolderRepository.findByAccountNumberAndPIN(22222,1234);
        assertEquals(1234, account1Details.getPIN());

    }

    private AccountHolder createAccountHolder(int accountNumber, int PIN, MonetaryAmount openingBalance, MonetaryAmount overdraft) {
        return AccountHolder.builder()
                .accountNumber(accountNumber)
                .PIN(PIN)
                .openingBalance(openingBalance)
                .overdraft(overdraft)
                .build();
    }
}