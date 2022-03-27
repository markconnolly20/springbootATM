package com.mdc.atm.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;

import javax.money.MonetaryAmount;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "atm_machine")

@Builder
//The following are required by the JPA contract
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ATMMachine {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull(message = "Machine Number is required")
    private int machineNumber;

    @NotNull(message = "Machine Fifties Balance is required")
    private int machineFiftiesBalance;

    @NotNull(message = "Machine Twenties Balance is required")
    private int machineTwentiesBalance;

    @NotNull(message = "Machine Tens Balance is required")
    private int machineTensBalance;

    @NotNull(message = "Machine Fives Balance is required")
    private int machineFivesBalance;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(int machineNumber) {
        this.machineNumber = machineNumber;
    }

    public int getMachineFiftiesBalance() {
        return machineFiftiesBalance;
    }

    public void setMachineFiftiesBalance(int machineFiftiesBalance) {
        this.machineFiftiesBalance = machineFiftiesBalance;
    }

    public int getMachineTwentiesBalance() {
        return machineTwentiesBalance;
    }

    public void setMachineTwentiesBalance(int machineTwentiesBalance) {
        this.machineTwentiesBalance = machineTwentiesBalance;
    }

    public int getMachineTensBalance() {
        return machineTensBalance;
    }

    public void setMachineTensBalance(int machineTensBalance) {
        this.machineTensBalance = machineTensBalance;
    }

    public int getMachineFivesBalance() {
        return machineFivesBalance;
    }

    public void setMachineFivesBalance(int machineFivesBalance) {
        this.machineFivesBalance = machineFivesBalance;
    }

    @Override
    public String toString() {
        return "ATMMachine{" + "id=" + this.id + '}';
    }

}
