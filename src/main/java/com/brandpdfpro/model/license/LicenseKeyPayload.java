package com.brandpdfpro.model.license;

import com.brandpdfpro.enums.LicenseType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class LicenseKeyPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String customerName;

    private String customerEmail;

    private String machineId;

    private LicenseType licenseType;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private int maxDevices;

    public LicenseKeyPayload() {
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

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
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

    @Override
    public String toString() {

        return "LicenseKeyPayload{" +
                "customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", machineId='" + machineId + '\'' +
                ", licenseType=" + licenseType +
                ", issueDate=" + issueDate +
                ", expiryDate=" + expiryDate +
                ", maxDevices=" + maxDevices +
                '}';
    }
}