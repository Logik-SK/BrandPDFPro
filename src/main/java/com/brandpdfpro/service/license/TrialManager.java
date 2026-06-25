package com.brandpdfpro.service.license;

import com.brandpdfpro.model.license.TrialInfo;
import com.brandpdfpro.util.AppPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TrialManager {

    private static final Logger logger =
            LoggerFactory.getLogger(TrialManager.class);

    private static final int TRIAL_DAYS = 30;

    private final MachineFingerprintService
            fingerprintService =
            new MachineFingerprintService();

    /**
     * Creates a new trial.
     */
    public TrialInfo createTrial()
            throws IOException {

        TrialInfo trialInfo =
                new TrialInfo();

        trialInfo.setMachineId(
                fingerprintService.generateMachineId()
        );

        trialInfo.setTrialStartDate(
                LocalDate.now()
        );

        trialInfo.setTrialExpiryDate(
                LocalDate.now()
                        .plusDays(TRIAL_DAYS)
        );

        saveTrial(trialInfo);

        logger.info(
                "Trial created successfully."
        );

        return trialInfo;
    }

    /**
     * Save trial information.
     */
    public void saveTrial(
            TrialInfo trialInfo)
            throws IOException {

        createDirectory();

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             new FileOutputStream(
                                     AppPaths.TRIAL_FILE))) {

            outputStream.writeObject(
                    trialInfo
            );
        }
    }

    /**
     * Load trial information.
     */
    public TrialInfo loadTrial()
            throws IOException,
            ClassNotFoundException {

        File file =
                new File(
                        AppPaths.TRIAL_FILE
                );

        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             new FileInputStream(file))) {

            return (TrialInfo)
                    inputStream.readObject();
        }
    }

    /**
     * Check whether trial exists.
     */
    public boolean hasTrial() {

        return new File(
                AppPaths.TRIAL_FILE
        ).exists();
    }

    /**
     * Remaining trial days.
     */
    public long getRemainingDays(
            TrialInfo trialInfo) {

        if (trialInfo == null) {
            return 0;
        }

        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                trialInfo.getTrialExpiryDate()
        );
    }

    /**
     * Trial validity.
     */
    public boolean isTrialValid(
            TrialInfo trialInfo) {

        if (trialInfo == null) {
            return false;
        }

        return !trialInfo.isExpired();
    }

    private void createDirectory() {

        File folder =
                new File(
                        AppPaths.LICENSE_FOLDER
                );

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}