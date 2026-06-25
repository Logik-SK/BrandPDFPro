package com.brandpdfpro.util;

import com.brandpdfpro.model.license.LicenseInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class LicenseChecksumUtil {

    private LicenseChecksumUtil() {
    }

    /**
     * Generate checksum for a license.
     */
    public static String generateChecksum(LicenseInfo licenseInfo) {

        try {

            String rawData = licenseInfo.getLicenseKey() + "|" + licenseInfo.getCustomerEmail() + "|" + licenseInfo.getLicenseType() + "|" + licenseInfo.getExpiryDate();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            for (byte b : hash) {

                builder.append(String.format("%02x", b));
            }

            return builder.toString().toUpperCase();

        } catch (Exception ex) {

            throw new RuntimeException("Unable to generate checksum.", ex);
        }
    }

    /**
     * Verify checksum.
     */
    public static boolean isChecksumValid(LicenseInfo licenseInfo) {

        String expectedChecksum = generateChecksum(licenseInfo);

        return expectedChecksum.equals(licenseInfo.getChecksum());
    }
}