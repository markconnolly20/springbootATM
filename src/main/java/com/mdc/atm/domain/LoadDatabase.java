package com.mdc.atm.domain;

import com.mdc.atm.repository.ATMMachineRepository;
import com.mdc.atm.repository.AccountHolderRepository;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initAccountHolders(AccountHolderRepository repository) {

        return args -> {
            CurrencyUnit eur = Monetary.getCurrency("EUR");

            AccountHolder account1 = new AccountHolder();
            account1.setAccountNumber(123456789);
            account1.setPIN(1234);
            account1.setOpeningBalance(Money.of(800, eur));;
            account1.setOverdraft(Money.of(200, eur));

            AccountHolder account2 = new AccountHolder();
            account2.setAccountNumber(987654321);
            account2.setPIN(4321);
            account2.setOpeningBalance(Money.of(1230, eur));;
            account2.setOverdraft(Money.of(150, eur));


            log.info("Preloading " + repository.save(account1));
            log.info("Preloading " + repository.save(account2));
        };
    }

    @Bean
    CommandLineRunner initATMMachine(ATMMachineRepository repository) {

        return args -> {
            ATMMachine machine1 = new ATMMachine();
            machine1.setMachineNumber(11111);
            machine1.setMachineFiftiesBalance(10);
            machine1.setMachineTwentiesBalance(30);
            machine1.setMachineTensBalance(30);
            machine1.setMachineFivesBalance(20);
            log.info("Preloading " + repository.save(machine1));
        };

    }

//    @Bean
//    CommandLineRunner initDatabase(FeeRepository repository) {
//
//        return args -> {
//            CurrencyUnit eur = Monetary.getCurrency("EUR");
//
//            Fee fee1 = new Fee();
//
//
//            fee1.setClientId("1234");
//            fee1.setAmount(Money.of(500, eur));
//
//            log.info("Preloading " + repository.save(fee1));
//
//        };
//    }

}