package com.brandpdfpro.service.license;

import com.brandpdfpro.enums.LicenseType;
import com.brandpdfpro.model.license.LicenseKeyPayload;
import com.brandpdfpro.util.CryptoUtil;

import java.time.LocalDate;

public class LicenseKeyVerifier {

    private static final String SECRET_KEY = "BPPRO_LICENSE_SECRET_V1";

    public LicenseKeyPayload verify(String licenseKey) {

        try {

            String decoded = CryptoUtil.base64Decode(licenseKey);

            int lastSeparator = decoded.lastIndexOf("|");

            if (lastSeparator < 0) {

                return null;
            }

            String payloadText = decoded.substring(0, lastSeparator);

            String signature = decoded.substring(lastSeparator + 1);

            boolean valid = CryptoUtil.verifyHmac(payloadText, signature, SECRET_KEY);

            if (!valid) {

                return null;
            }

            String[] parts = payloadText.split("\\|");

            if (parts.length != 7) {

                return null;
            }

            LicenseKeyPayload payload = new LicenseKeyPayload();

            payload.setCustomerName(parts[0]);

            payload.setCustomerEmail(parts[1]);

            payload.setMachineId(parts[2]);

            payload.setLicenseType(LicenseType.valueOf(parts[3]));

            payload.setIssueDate(LocalDate.parse(parts[4]));

            payload.setExpiryDate(LocalDate.parse(parts[5]));

            payload.setMaxDevices(Integer.parseInt(parts[6]));

            return payload;

        } catch (Exception ex) {

            return null;
        }
    }
}