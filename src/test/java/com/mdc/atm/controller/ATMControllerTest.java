package com.mdc.atm.controller;

import com.mdc.atm.domain.ATMMachine;
import com.mdc.atm.domain.AccountHolder;
import com.mdc.atm.repository.AccountHolderRepository;
import com.mdc.atm.restObjects.ATMResponse;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ATMControllerTest {

    @Autowired
    private com.mdc.atm.repository.ATMMachineRepository ATMMachineRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    // tests if after withdrawing 500 euro from an account with 1400 (including overdraft) the remaining balance is 900
    @Test
    void withdrawValidAmount() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "1234";
        String amount = "500";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(900, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EUR900.00", response.getBody().getMaxWithdrawalAmount());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }

    // test "should not dispense funds if the pin is incorrect"
    @Test
    void dontDispenseWhenPinIncorrect() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "8123";
        String amount = "500";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(900, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        AccountHolder account1Reloaded =  accountHolderRepository.findByAccountNumberAndPIN(22222,1234);

        assertEquals(Money.of(900, eur), account1Reloaded.getOpeningBalance());
        assertEquals(Money.of(500, eur), account1Reloaded.getOverdraft());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Please check account number and PIN are valid", response.getBody().getMessage());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);
    }

    // test "cannot dispense more funds than customer have access to"
    @Test
    void dontDispenseWhenCustomerInsufficientFunds() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "1234";
        String amount = "1500";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(900, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        AccountHolder account1Reloaded =  accountHolderRepository.findByAccountNumberAndPIN(22222,1234);

        assertEquals(Money.of(900, eur), account1Reloaded.getOpeningBalance());
        assertEquals(Money.of(500, eur), account1Reloaded.getOverdraft());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You have insufficient funds available in your account", response.getBody().getMessage());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }



    // test "should not expose the customer balance if the pin is incorrect"
    @Test
    void dontExposeCustomerBalanceWhenIncorrectPinWithdrawal() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "5555";
        String amount = "1500";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(900, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody().getBalance());
        assertEquals(null, response.getBody().getMaxWithdrawalAmount());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }

    // test "should not expose the customer balance if the pin is incorrect"
    @Test
    void dontExposeCustomerBalanceWhenIncorrectPinCheckBalance() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "5555";
        String amount = "1500";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(900, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        ResponseEntity<ATMResponse> response =
                testRestTemplate.getForEntity("/getBalance/" + accountNumber + "/" + pin , ATMResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody().getBalance());
        assertEquals(null, response.getBody().getMaxWithdrawalAmount());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }

    // test "cannot dispense more money than it holds"
    @Test
    void dontDispenseMoreMoneyThanMachineHolds() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "1234";
        String amount = "1600";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(2000, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        AccountHolder account1Reloaded =  accountHolderRepository.findByAccountNumberAndPIN(22222,1234);

        assertEquals(Money.of(2000, eur), account1Reloaded.getOpeningBalance());
        assertEquals(Money.of(500, eur), account1Reloaded.getOverdraft());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("There is not enough cash left in the ATM to dispense this amount", response.getBody().getMessage());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }

    /* test "should only dispense the exact amounts requested"
       e.g when the bank machine has all types of notes available and the user requests 40 then only 2 x 20s should be dispensed
    */
    @Test
    void dispenseExactAmounts() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "1234";
        String amount = "40";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(2000, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        AccountHolder account1Reloaded =  accountHolderRepository.findByAccountNumberAndPIN(22222,1234);

        assertEquals(Money.of(1960, eur), account1Reloaded.getOpeningBalance());
        assertEquals(Money.of(500, eur), account1Reloaded.getOverdraft());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notes: Fifties: 0, Twenties: 2, Tens: 0, Fives:0", response.getBody().getNotesDispensed());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }


    /* test "should dispense the minimum number of notes per withdrawal"
    e.g if user has requested 470 euro then 9 x 50 notes and 1 x 20 note should be dispensed
     */
    @Test
    void dispenseMinimimAmountofNotes() {

        //params for rest endpoint
        String machineNumber = "33333";
        String accountNumber = "22222";
        String pin = "1234";
        String amount = "470";

        //set up atm machine
        ATMMachine machine1 = createATMMachine(33333,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        //set up account holder
        CurrencyUnit eur = Monetary.getCurrency("EUR");
        AccountHolder account1 = createAccountHolder(22222,1234,Money.of(2000, eur),Money.of(500, eur));

        accountHolderRepository.saveAll(List.of(account1));

        //Set up post request with parameters to withdrawal endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set the form inputs in a multivaluemap
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", accountNumber);
        map.add("pin", pin);
        map.add("machinenumber", machineNumber);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(map, headers);

        ResponseEntity<ATMResponse> response =
                testRestTemplate.postForEntity("/requestWithdrawal/" , request, ATMResponse.class);

        AccountHolder account1Reloaded =  accountHolderRepository.findByAccountNumberAndPIN(22222,1234);

        assertEquals(Money.of(1530, eur), account1Reloaded.getOpeningBalance());
        assertEquals(Money.of(500, eur), account1Reloaded.getOverdraft());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notes: Fifties: 9, Twenties: 1, Tens: 0, Fives:0", response.getBody().getNotesDispensed());

        //clean db
        accountHolderRepository.delete(account1);
        ATMMachineRepository.delete(machine1);

    }


    private AccountHolder createAccountHolder(int accountNumber, int PIN, MonetaryAmount openingBalance, MonetaryAmount overdraft) {
        return AccountHolder.builder()
                .accountNumber(accountNumber)
                .PIN(PIN)
                .openingBalance(openingBalance)
                .overdraft(overdraft)
                .build();
    }

    private ATMMachine createATMMachine(int machineNumber, int machineFiftiesBalance, int machineTwentiesBalance,
                                        int machineTensBalance, int machineFivesBalance) {
        return ATMMachine.builder()
                .machineNumber(machineNumber)
                .machineFiftiesBalance(machineFiftiesBalance)
                .machineTwentiesBalance(machineTwentiesBalance)
                .machineTensBalance(machineTensBalance)
                .machineFivesBalance(machineFivesBalance)
                .build();

    }
}