package com.mdc.atm.repository;

import com.mdc.atm.domain.ATMMachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ATMMachineRepository extends JpaRepository<ATMMachine, UUID> {
    ATMMachine findByMachineNumber(int machineNumber);
}
