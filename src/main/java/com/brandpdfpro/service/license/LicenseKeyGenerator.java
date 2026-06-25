package com.brandpdfpro.service.license;

import com.brandpdfpro.enums.LicenseType;
import com.brandpdfpro.model.license.LicenseKeyPayload;
import com.brandpdfpro.util.CryptoUtil;

import java.time.LocalDate;

public class LicenseKeyGenerator {

    /**
     * TEMPORARY
     * <p>
     * Later this constant must exist only
     * inside the License Generator Tool.
     */
    private static final String SECRET_KEY = "BPPRO_LICENSE_SECRET_V1";

    /**
     * Generate signed license key.
     */
    public String generateLicenseKey(String customerName, String customerEmail, String machineId, LicenseType licenseType, int validityDays, int maxDevices) {

        LicenseKeyPayload payload = new LicenseKeyPayload();

        payload.setCustomerName(customerName);

        payload.setCustomerEmail(customerEmail);

        payload.setMachineId(machineId);

        payload.setLicenseType(licenseType);

        payload.setIssueDate(LocalDate.now());

        payload.setExpiryDate(LocalDate.now().plusDays(validityDays));

        payload.setMaxDevices(maxDevices);

        String payloadText = buildPayload(payload);

        String signature = CryptoUtil.generateHmac(payloadText, SECRET_KEY);

        String finalPayload = payloadText + "|" + signature;

        return CryptoUtil.base64Encode(finalPayload);
    }

    /**
     * Create payload string.
     */
    private String buildPayload(LicenseKeyPayload payload) {

        return payload.getCustomerName() + "|" + payload.getCustomerEmail() + "|" + payload.getMachineId() + "|" + payload.getLicenseType().name() + "|" + payload.getIssueDate() + "|" + payload.getExpiryDate() + "|" + payload.getMaxDevices();
    }
}