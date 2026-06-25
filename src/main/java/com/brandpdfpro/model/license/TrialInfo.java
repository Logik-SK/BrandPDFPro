package com.brandpdfpro.model.license;

import java.io.Serializable;
import java.time.LocalDate;

public class TrialInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String machineId;

    private LocalDate trialStartDate;

    private LocalDate trialExpiryDate;

    public TrialInfo() {
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public LocalDate getTrialStartDate() {
        return trialStartDate;
    }

    public void setTrialStartDate(LocalDate trialStartDate) {
        this.trialStartDate = trialStartDate;
    }

    public LocalDate getTrialExpiryDate() {
        return trialExpiryDate;
    }

    public void setTrialExpiryDate(LocalDate trialExpiryDate) {
        this.trialExpiryDate = trialExpiryDate;
    }

    public boolean isExpired() {

        return LocalDate.now()
                .isAfter(trialExpiryDate);
    }
}