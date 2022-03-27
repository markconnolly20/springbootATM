package com.mdc.atm.repository;

import com.mdc.atm.domain.ATMMachine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ATMMachineRepositoryTest {

    @Autowired
    private ATMMachineRepository ATMMachineRepository;

    @Test
    void findByMachineNumber() {


        ATMMachine machine1 = createATMMachine(22222,10,30,30,20);
        ATMMachineRepository.saveAll(List.of(machine1));

        ATMMachine machine1Details = ATMMachineRepository.findByMachineNumber(22222);
        assertEquals(10, machine1Details.getMachineFiftiesBalance());
        assertEquals(30, machine1Details.getMachineTwentiesBalance());
        assertEquals(30, machine1Details.getMachineTensBalance());
        assertEquals(20, machine1Details.getMachineFivesBalance());

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