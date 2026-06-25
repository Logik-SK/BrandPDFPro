package com.brandpdfpro.service.license;

import com.brandpdfpro.enums.LicenseType;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.util.LicenseChecksumUtil;

import java.time.LocalDate;
import java.util.UUID;

public class LicenseGenerator {

    /**
     * Generate Professional or Enterprise License
     */
    public LicenseInfo generateLicense(String customerName, String customerEmail, LicenseType licenseType, int validityDays) {

        LicenseInfo licenseInfo = new LicenseInfo();

        licenseInfo.setLicenseType(licenseType);

        licenseInfo.setCustomerName(customerName);

        licenseInfo.setCustomerEmail(customerEmail);

        licenseInfo.setLicenseKey(generateLicenseKey());

        licenseInfo.setActivationDate(LocalDate.now());

        licenseInfo.setExpiryDate(LocalDate.now().plusDays(validityDays));

        // v1.5.0 Offline Licensing
        // Generated License = Activated License
        licenseInfo.setActivated(true);

        licenseInfo.setChecksum(LicenseChecksumUtil.generateChecksum(licenseInfo));

        return licenseInfo;
    }

    private String generateLicenseKey() {

        String key = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        return "BPPRO-" + key.substring(0, 4) + "-" + key.substring(4, 8) + "-" + key.substring(8, 12);
    }
}