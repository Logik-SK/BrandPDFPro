package com.brandpdfpro.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class CryptoUtil {

    private CryptoUtil() {
    }

    /**
     * SHA-256 Hash
     */
    public static String sha256(String value) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (Exception ex) {

            throw new RuntimeException("Unable to generate SHA-256 hash.", ex);
        }
    }

    /**
     * HMAC SHA-256 Signature
     */
    public static String generateHmac(String payload, String secretKey) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

            mac.init(keySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (Exception ex) {

            throw new RuntimeException("Unable to generate HMAC.", ex);
        }
    }

    /**
     * Verify HMAC Signature
     */
    public static boolean verifyHmac(String payload, String signature, String secretKey) {

        String expectedSignature = generateHmac(payload, secretKey);

        return expectedSignature.equals(signature);
    }

    /**
     * Base64 Encode
     */
    public static String base64Encode(String value) {

        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 Decode
     */
    public static String base64Decode(String value) {

        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /**
     * Convert Bytes To Hex
     */
    public static String bytesToHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {

            builder.append(String.format("%02X", b));
        }

        return builder.toString();
    }
}