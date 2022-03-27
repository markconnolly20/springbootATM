package com.mdc.atm.repository;

import com.mdc.atm.domain.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, UUID> {

  //  List<AccountHolder> findByAccountNumber(int accountNumber);

    AccountHolder findByAccountNumberAndPIN(int accountNumber, int PIN);

}
