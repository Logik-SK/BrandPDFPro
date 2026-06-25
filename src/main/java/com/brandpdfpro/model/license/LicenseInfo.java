package com.brandpdfpro.model.license;

import com.brandpdfpro.enums.LicenseType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class LicenseInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LicenseType licenseType;

    private String licenseKey;

    private String customerName;

    private String customerEmail;

    private String machineId;

    private LocalDate activationDate;

    private LocalDate expiryDate;

    private int maxDevices = 1;

    private int currentDevices;

    private boolean activated;
    private String checksum;

    private LocalDate issueDate;

    public LicenseInfo() {
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getMaxDevices() {
        return maxDevices;
    }

    public void setMaxDevices(int maxDevices) {
        this.maxDevices = maxDevices;
    }

    public int getCurrentDevices() {
        return currentDevices;
    }

    public void setCurrentDevices(int currentDevices) {
        this.currentDevices = currentDevices;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isExpired() {

        if (expiryDate == null) {
            return false;
        }

        return LocalDate.now().isAfter(expiryDate);
    }

    public long getRemainingDays() {

        if (expiryDate == null) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "LicenseInfo{" +
                "licenseType=" + licenseType +
                ", licenseKey='" + licenseKey + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", machineId='" + machineId + '\'' +
                ", activationDate=" + activationDate +
                ", expiryDate=" + expiryDate +
                ", maxDevices=" + maxDevices +
                ", currentDevices=" + currentDevices +
                ", activated=" + activated +
                ", checksum='" + checksum + '\'' +
                ", issueDate=" + issueDate +
                '}';
    }
}