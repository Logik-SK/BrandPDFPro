package com.brandpdfpro.service.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MachineFingerprintService {

    private static final Logger logger = LoggerFactory.getLogger(MachineFingerprintService.class);

    /**
     * Generates a unique machine fingerprint.
     */
    public String generateMachineId() {

        try {

            String computerName = InetAddress.getLocalHost().getHostName();

            String userName = System.getProperty("user.name");

            String osName = System.getProperty("os.name");

            String osVersion = System.getProperty("os.version");

            String rawFingerprint = computerName + "|" + userName + "|" + osName + "|" + osVersion;

            String hash = sha256(rawFingerprint);

            // Keep first 12 hexadecimal characters (48 bits)
            String shortHash = hash.substring(0, 12).toUpperCase();

            return "M" + shortHash;

        } catch (Exception ex) {

            logger.error("Failed to generate machine fingerprint.", ex);

            return "MUNKNOWN";
        }
    }

    /**
     * Returns a short display ID for UI screens.
     */
    public String getDisplayMachineId() {

        String machineId = generateMachineId();

        if (machineId.length() > 12) {

            return machineId.substring(0, 12);
        }

        return machineId;
    }

    /**
     * SHA-256 hashing utility.
     */
    private String sha256(String input) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder builder = new StringBuilder();

        for (byte b : hash) {

            builder.append(String.format("%02x", b));
        }

        return builder.toString().toUpperCase();
    }
}