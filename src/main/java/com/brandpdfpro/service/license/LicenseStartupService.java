package com.brandpdfpro.service.license;

import com.brandpdfpro.enums.StartupStatus;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.model.license.StartupResult;
import com.brandpdfpro.model.license.TrialInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseStartupService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseStartupService.class);

    private final LicenseService licenseService = new LicenseService();

    private final TrialManager trialManager = new TrialManager();

    public StartupResult validateStartup() {

        StartupResult result = new StartupResult();

        try {

            /*
             * STEP 1
             * Check Active License
             */
            if (licenseService.hasLicense()) {

                LicenseInfo licenseInfo = licenseService.loadLicense();

                if (licenseService.isLicenseValid(licenseInfo)) {

                    result.setStatus(StartupStatus.LICENSE_ACTIVE);

                    result.setLicenseType(licenseInfo.getLicenseType());

                    result.setRemainingDays(licenseInfo.getRemainingDays());

                    result.setMessage("License Active");

                    logger.info("Valid license found.");

                    return result;
                }
            }

            /*
             * STEP 2
             * Check Trial
             */
            TrialInfo trialInfo;

            if (!trialManager.hasTrial()) {

                logger.info("No trial found. Creating new trial.");

                trialInfo = trialManager.createTrial();

            } else {

                trialInfo = trialManager.loadTrial();
            }

            if (trialManager.isTrialValid(trialInfo)) {

                result.setStatus(StartupStatus.TRIAL_ACTIVE);

                result.setRemainingDays(trialManager.getRemainingDays(trialInfo));

                result.setMessage("Trial Active");

                logger.info("Trial active. Remaining days={}", result.getRemainingDays());

                return result;
            }

            /*
             * STEP 3
             * Activation Required
             */
            result.setStatus(StartupStatus.ACTIVATION_REQUIRED);

            result.setRemainingDays(0);

            result.setMessage("Trial Expired");

            logger.warn("Activation required.");

            return result;

        } catch (Exception ex) {

            logger.error("Startup validation failed.", ex);

            result.setStatus(StartupStatus.ACTIVATION_REQUIRED);

            result.setRemainingDays(0);

            result.setMessage(ex.getMessage());

            return result;
        }
    }
}