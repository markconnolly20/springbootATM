package com.mdc.atm.controller;

import java.util.List;
import java.util.Locale;

import com.mdc.atm.Exceptions.InsufficientFundsException;
import com.mdc.atm.domain.ATMMachine;
import com.mdc.atm.domain.AccountHolder;
import com.mdc.atm.repository.ATMMachineRepository;
import com.mdc.atm.repository.AccountHolderRepository;
import com.mdc.atm.Exceptions.ATMOutOfCashException;
import com.mdc.atm.Exceptions.AccountHolderNotFoundException;
import com.mdc.atm.Exceptions.InvalidAmountRequestedException;
import com.mdc.atm.restObjects.ATMResponse;
import org.javamoney.moneta.Money;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

@RestController
class ATMController {

    private final AccountHolderRepository accountHolderRepository;
    private final com.mdc.atm.repository.ATMMachineRepository ATMMachineRepository;
    private final int ATM_MIN_AMOUNT = 5;

    ATMController(AccountHolderRepository accountHolderRepository, ATMMachineRepository ATMMachineRepository) {
        this.accountHolderRepository = accountHolderRepository;
        this.ATMMachineRepository = ATMMachineRepository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/getAccountsDebug")
    List<AccountHolder> allAccounts() {
        return accountHolderRepository.findAll();
    }

    @GetMapping("/getATMMachinesDebug")
    List<ATMMachine> allATMMachines() {
        return ATMMachineRepository.findAll();
    }
    // end::get-aggregate-root[]

    @GetMapping("/getAccountByAccountNumberAndPin/{id}/{pin}")
    AccountHolder getAccountByAccountNumberAndPin(@PathVariable int id,@PathVariable int pin) {
        return accountHolderRepository.findByAccountNumberAndPIN(id, pin);
    }

    @GetMapping("/getBalance/{id}/{pin}")
    ATMResponse getBalance(@PathVariable int id, @PathVariable int pin) {
        ATMResponse atmResponse = new ATMResponse();

        try {
            AccountHolder accountHolder = accountHolderRepository.findByAccountNumberAndPIN(id, pin);

            if(accountHolder == null) {
                throw new AccountHolderNotFoundException(id);
            }

            MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault());
            atmResponse.setBalance(format.format(accountHolder.getOpeningBalance()));

            MonetaryAmount maxWithdrawalAmount =
                    Money.of(0, accountHolder.getOpeningBalance().getCurrency());

            maxWithdrawalAmount = maxWithdrawalAmount.add(accountHolder.getOpeningBalance());
            maxWithdrawalAmount = maxWithdrawalAmount.add(accountHolder.getOverdraft());

            atmResponse.setMaxWithdrawalAmount(format.format(maxWithdrawalAmount));
        } catch (AccountHolderNotFoundException e) {
            atmResponse.setMessage("Please check account number and PIN are valid " + e );
        } catch (Error e) {
            atmResponse.setMessage("There was an exception during this request " + e );
        }

        return atmResponse;
    }

    @PostMapping("/requestWithdrawal")
    ATMResponse requestWithdrawal(@RequestParam(value="id") int id , @RequestParam(value="pin") int pin, @RequestParam(value="machinenumber") int machinenumber , @RequestParam(value="amount") int amount) {
        ATMResponse atmResponse = new ATMResponse();


        try {
            AccountHolder accountHolder = accountHolderRepository.findByAccountNumberAndPIN(id, pin);

            //crosscheck account with pin
            if(accountHolder == null) {
                throw new AccountHolderNotFoundException(id);
            }

            //check amount requested is valid
            if(amount % ATM_MIN_AMOUNT != 0) {
                throw new InvalidAmountRequestedException(id);
            }

            //check if user has enough money in their account for their request
            CurrencyUnit currency = accountHolder.getOpeningBalance().getCurrency();
            MonetaryAmount amountRequested = Money.of(amount, currency);

            MonetaryAmount maxWithdrawalAmount =
                    Money.of(0, accountHolder.getOpeningBalance().getCurrency());

            maxWithdrawalAmount = maxWithdrawalAmount.add(accountHolder.getOpeningBalance());
            maxWithdrawalAmount = maxWithdrawalAmount.add(accountHolder.getOverdraft());


            if(maxWithdrawalAmount.isLessThan(amountRequested)) {
                throw new InsufficientFundsException(id);
            }


            //check if we should deduct from overdraft and deduct if necessary
            if(accountHolder.getOpeningBalance().isLessThan(amountRequested)){
                MonetaryAmount amountToSubtractFromOverdraft = Money.of(0, currency);
                MonetaryAmount amountToSubtractFromOpeningBalance = Money.of(0, currency);

                amountToSubtractFromOverdraft = accountHolder.getOpeningBalance().subtract(amountRequested);
                amountToSubtractFromOpeningBalance = amountRequested.add(amountToSubtractFromOverdraft);
                accountHolder.setOpeningBalance(accountHolder.getOpeningBalance().subtract(amountToSubtractFromOpeningBalance));
                accountHolder.setOverdraft(accountHolder.getOverdraft().add(amountToSubtractFromOverdraft));
            } else {
                accountHolder.setOpeningBalance(accountHolder.getOpeningBalance().subtract(amountRequested));
            }

            // dispense notes
            ATMMachine ATMMachine = ATMMachineRepository.findByMachineNumber(machinenumber);
            int dispenseAmount = 0;
            int fifties = 0;
            int twenties = 0;
            int tens = 0;
            int fives = 0;

            while(dispenseAmount < amount){
                if(amount - dispenseAmount >= 50 && ATMMachine.getMachineFiftiesBalance() > 0) {
                    ATMMachine.setMachineFiftiesBalance(ATMMachine.getMachineFiftiesBalance() - 1);
                    dispenseAmount += 50;
                    fifties++;
                } else if (amount - dispenseAmount >= 20 && ATMMachine.getMachineTwentiesBalance() > 0) {
                    ATMMachine.setMachineTwentiesBalance(ATMMachine.getMachineTwentiesBalance() - 1);
                    dispenseAmount += 20;
                    twenties++;
                } else if (amount - dispenseAmount >= 10 && ATMMachine.getMachineTensBalance() > 0) {
                    ATMMachine.setMachineTensBalance(ATMMachine.getMachineTensBalance() - 1);
                    dispenseAmount += 10;
                    tens++;
                } else if (amount - dispenseAmount >= 5 && ATMMachine.getMachineFivesBalance() > 0) {
                    ATMMachine.setMachineFivesBalance(ATMMachine.getMachineFivesBalance() - 1);
                    dispenseAmount += 5;
                    fives++;
                } else {
                    break;
                }
            }

            if(dispenseAmount < amount){
                throw new ATMOutOfCashException(id);
            }


            accountHolderRepository.save(accountHolder);
            ATMMachineRepository.save(ATMMachine);

            maxWithdrawalAmount = maxWithdrawalAmount.subtract(amountRequested);

            atmResponse.setNotesDispensed("Notes: Fifties: " + fifties + ", Twenties: " + twenties + ", Tens: " + tens + ", Fives:" + fives);
            MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault());
            atmResponse.setBalance(format.format(accountHolder.getOpeningBalance()));
            atmResponse.setMaxWithdrawalAmount(format.format(maxWithdrawalAmount));

        } catch (AccountHolderNotFoundException e) {
            atmResponse.setMessage("Please check account number and PIN are valid");
        } catch (InsufficientFundsException e) {
            atmResponse.setMessage("You have insufficient funds available in your account");
        } catch (ATMOutOfCashException e) {
            atmResponse.setMessage("There is not enough cash left in the ATM to dispense this amount");
        } catch (InvalidAmountRequestedException e)    {
            atmResponse.setMessage("You have requested an invalid amount");
        } catch (Error e) {
            atmResponse.setMessage("There was an exception during this request");
        }

        return atmResponse;
    }


}