package com.brandpdfpro.model.license;

import com.brandpdfpro.enums.StartupStatus;
import com.brandpdfpro.enums.LicenseType;

public class StartupResult {

    private StartupStatus status;

    private LicenseType licenseType;

    private long remainingDays;

    private String message;

    public StartupResult() {
    }

    public StartupStatus getStatus() {
        return status;
    }

    public void setStatus(StartupStatus status) {
        this.status = status;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(long remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}