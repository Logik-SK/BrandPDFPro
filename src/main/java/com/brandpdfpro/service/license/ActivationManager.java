package com.brandpdfpro.service.license;

import com.brandpdfpro.enums.LicenseType;
import com.brandpdfpro.exception.LicenseException;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.model.license.LicenseKeyPayload;
import com.brandpdfpro.util.LicenseChecksumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class ActivationManager {

    private static final Logger logger = LoggerFactory.getLogger(ActivationManager.class);

    private final LicenseService licenseService = new LicenseService();

    private final MachineFingerprintService fingerprintService = new MachineFingerprintService();

    private final LicenseValidator licenseValidator = new LicenseValidator();

    /**
     * =========================================
     * B2 ACTIVATION
     * =========================================
     */
    public LicenseInfo activateLicense(String licenseKey) throws LicenseException {

        LicenseKeyVerifier verifier = new LicenseKeyVerifier();

        LicenseKeyPayload payload = verifier.verify(licenseKey);

        if (payload == null) {

            throw new LicenseException("Invalid license key.");
        }

        String currentMachineId = fingerprintService.generateMachineId();

        if (!currentMachineId.equals(payload.getMachineId())) {

            throw new LicenseException("License belongs to another machine.");
        }

        LicenseInfo licenseInfo = new LicenseInfo();

        licenseInfo.setLicenseKey(licenseKey);

        licenseInfo.setCustomerName(payload.getCustomerName());

        licenseInfo.setCustomerEmail(payload.getCustomerEmail());

        licenseInfo.setMachineId(payload.getMachineId());

        licenseInfo.setLicenseType(payload.getLicenseType());

        licenseInfo.setIssueDate(payload.getIssueDate());

        licenseInfo.setActivationDate(LocalDate.now());

        licenseInfo.setExpiryDate(payload.getExpiryDate());

        licenseInfo.setMaxDevices(payload.getMaxDevices());

        licenseInfo.setCurrentDevices(1);

        licenseInfo.setActivated(true);

        licenseInfo.setChecksum(LicenseChecksumUtil.generateChecksum(licenseInfo));

        licenseService.saveLicense(licenseInfo);

        logger.info("License activated successfully.");

        return licenseInfo;
    }

    /**
     * =========================================
     * LEGACY ACTIVATION
     * Keep temporarily until migration complete
     * =========================================
     */
    @Deprecated
    public LicenseInfo activateLicense(String licenseKey, String customerName, String customerEmail, LicenseType licenseType) throws LicenseException {

        licenseValidator.validateLicenseKey(licenseKey);

        LicenseInfo licenseInfo = new LicenseInfo();

        licenseInfo.setLicenseKey(licenseKey.toUpperCase());

        licenseInfo.setCustomerName(customerName);

        licenseInfo.setCustomerEmail(customerEmail);

        licenseInfo.setLicenseType(licenseType);

        licenseInfo.setMachineId(fingerprintService.generateMachineId());

        licenseInfo.setActivationDate(LocalDate.now());

        licenseInfo.setExpiryDate(LocalDate.now().plusDays(licenseType.getValidityDays()));

        licenseInfo.setMaxDevices(licenseType.getMaxDevices());

        licenseInfo.setCurrentDevices(1);

        licenseInfo.setActivated(true);

        licenseInfo.setChecksum(LicenseChecksumUtil.generateChecksum(licenseInfo));

        licenseService.saveLicense(licenseInfo);

        logger.info("Legacy license activated successfully.");

        return licenseInfo;
    }

    /**
     * Deactivate Current License
     */
    public void deactivateLicense() throws LicenseException {

        licenseService.deleteLicense();

        logger.info("License deactivated successfully.");
    }

    /**
     * Check Activation Status
     */
    public boolean isActivated() {

        try {

            LicenseInfo licenseInfo = licenseService.loadLicense();

            return licenseService.isLicenseValid(licenseInfo);

        } catch (Exception ex) {

            logger.error("Activation status check failed.", ex);

            return false;
        }
    }

    /**
     * Get Active License
     */
    public LicenseInfo getActiveLicense() {

        try {

            return licenseService.loadLicense();

        } catch (Exception ex) {

            logger.error("Unable to load active license.", ex);

            return null;
        }
    }

    /**
     * Get Current Machine ID
     */
    public String getMachineId() {

        return fingerprintService.getDisplayMachineId();
    }
}