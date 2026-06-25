package com.brandpdfpro.enums;

/**
 * Defines all supported BrandPDF Pro license types.
 *
 * TRIAL        - 30 day evaluation period
 * PROFESSIONAL - Annual subscription for individual users
 * ENTERPRISE   - Annual subscription for teams and organizations
 */
public enum LicenseType {

    TRIAL("Trial", 30, 1),

    PROFESSIONAL("Professional", 365, 3),

    ENTERPRISE("Enterprise", 365, 25);

    private final String displayName;
    private final int validityDays;
    private final int maxDevices;

    LicenseType(String displayName,
                int validityDays,
                int maxDevices) {

        this.displayName = displayName;
        this.validityDays = validityDays;
        this.maxDevices = maxDevices;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public int getMaxDevices() {
        return maxDevices;
    }

    public boolean isTrial() {
        return this == TRIAL;
    }

    public boolean isProfessional() {
        return this == PROFESSIONAL;
    }

    public boolean isEnterprise() {
        return this == ENTERPRISE;
    }

    /**
     * Convert display name to enum.
     */
    public static LicenseType fromDisplayName(String displayName) {

        for (LicenseType type : values()) {

            if (type.getDisplayName()
                    .equalsIgnoreCase(displayName)) {

                return type;
            }
        }

        throw new IllegalArgumentException(
                "Unknown License Type: " + displayName
        );
    }

    @Override
    public String toString() {
        return displayName;
    }
}