package com.brandpdfpro.service.license;

import com.brandpdfpro.exception.LicenseException;

import java.util.regex.Pattern;

public class LicenseValidator {

    /**
     * BrandPDF Pro license format:
     * <p>
     * BPPRO-XXXX-XXXX-XXXX
     */
    private static final Pattern LICENSE_PATTERN = Pattern.compile("^BPPRO-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$");

    /**
     * Validate license key format.
     */
    public void validateLicenseKey(String licenseKey) throws LicenseException {

        if (licenseKey == null || licenseKey.trim().isEmpty()) {

            throw new LicenseException("License key cannot be empty.");
        }

        if (!LICENSE_PATTERN.matcher(licenseKey.toUpperCase()).matches()) {

            throw new LicenseException("Invalid license key format.");
        }
    }
}