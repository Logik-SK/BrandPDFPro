package com.brandpdfpro.service.license;

import com.brandpdfpro.exception.LicenseException;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.util.AppPaths;
import com.brandpdfpro.util.LicenseChecksumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    /**
     * Save license information.
     */
    public void saveLicense(LicenseInfo licenseInfo) throws LicenseException {

        try {

            createLicenseDirectory();

            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(AppPaths.LICENSE_FILE))) {

                outputStream.writeObject(licenseInfo);
            }

            logger.info("License saved successfully. Type={}", licenseInfo.getLicenseType());

        } catch (Exception ex) {

            logger.error("Failed to save license.", ex);

            throw new LicenseException("Unable to save license.", ex);
        }
    }

    /**
     * Load license information.
     */
    public LicenseInfo loadLicense() throws LicenseException {

        File file = new File(AppPaths.LICENSE_FILE);

        if (!file.exists()) {

            logger.warn("No license file found.");

            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {

            LicenseInfo licenseInfo = (LicenseInfo) inputStream.readObject();

            if (!LicenseChecksumUtil.isChecksumValid(licenseInfo)) {

                logger.error("License checksum validation failed.");

                throw new LicenseException("License file has been modified or corrupted.");
            }

            logger.info("License loaded and validated successfully.");

            return licenseInfo;

        } catch (Exception ex) {

            logger.error("Failed to load license.", ex);

            throw new LicenseException("Unable to load license.", ex);
        }
    }

    /**
     * Check whether license file exists.
     */
    public boolean hasLicense() {

        return new File(AppPaths.LICENSE_FILE).exists();
    }

    /**
     * Validate license.
     */
    public boolean isLicenseValid(LicenseInfo licenseInfo) {

        if (licenseInfo == null) {
            return false;
        }

        if (!licenseInfo.isActivated()) {
            return false;
        }

        if (licenseInfo.isExpired()) {
            return false;
        }

        return LicenseChecksumUtil.isChecksumValid(licenseInfo);
    }

    /**
     * Delete license.
     */
    public void deleteLicense() throws LicenseException {

        File file = new File(AppPaths.LICENSE_FILE);

        if (!file.exists()) {
            return;
        }

        if (!file.delete()) {

            throw new LicenseException("Unable to delete license.");
        }

        logger.info("License deleted successfully.");
    }

    /**
     * Create license storage directory.
     */
    private void createLicenseDirectory() {

        File directory = new File(AppPaths.LICENSE_FOLDER);

        if (!directory.exists()) {

            boolean created = directory.mkdirs();

            if (created) {

                logger.info("Created license directory: {}", directory.getAbsolutePath());
            }
        }
    }
}