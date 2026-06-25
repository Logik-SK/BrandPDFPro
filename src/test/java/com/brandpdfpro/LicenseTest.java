package com.brandpdfpro;

import com.brandpdfpro.enums.LicenseType;
import com.brandpdfpro.model.license.LicenseInfo;
import com.brandpdfpro.model.license.LicenseKeyPayload;
import com.brandpdfpro.model.license.StartupResult;
import com.brandpdfpro.service.license.*;

public class LicenseTest {

    public static void main(String[] args) {

        try {

            //testLicenseGenerationAndSave();

            //testStartupValidation();

           // testLicenseKeyGenerationAndVerification();

           testActivationManagerB2();


        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    /**
     * TEST 1
     * Generate -> Save -> Load License
     */
    private static void testLicenseGenerationAndSave() throws Exception {

        System.out.println();
        System.out.println("================================");
        System.out.println("TEST 1 : LICENSE GENERATION");
        System.out.println("================================");

        LicenseGenerator generator = new LicenseGenerator();

        LicenseInfo license = generator.generateLicense("Sanjeev Kumar", "sanjeev@test.com", LicenseType.PROFESSIONAL, 365);

        LicenseService service = new LicenseService();

        service.saveLicense(license);

        LicenseInfo loadedLicense = service.loadLicense();

        System.out.println("License Key  : " + loadedLicense.getLicenseKey());

        System.out.println("Activated    : " + loadedLicense.isActivated());

        System.out.println("License Type : " + loadedLicense.getLicenseType());

        System.out.println("Expiry Date  : " + loadedLicense.getExpiryDate());

        System.out.println("Checksum     : " + loadedLicense.getChecksum());

        System.out.println("PASS");
    }

    /**
     * TEST 2
     * Startup Validation Flow
     */
    private static void testStartupValidation() {

        System.out.println();
        System.out.println("================================");
        System.out.println("TEST 2 : STARTUP VALIDATION");
        System.out.println("================================");

        LicenseStartupService startupService = new LicenseStartupService();

        StartupResult result = startupService.validateStartup();

        System.out.println("Status        : " + result.getStatus());

        System.out.println("Message       : " + result.getMessage());

        System.out.println("RemainingDays : " + result.getRemainingDays());

        System.out.println("PASS");
    }

    /**
     * TEST 3
     * B2 License Key Flow
     */
    private static void testLicenseKeyGenerationAndVerification() {

        System.out.println();
        System.out.println("================================");
        System.out.println("TEST 3 : LICENSE KEY");
        System.out.println("================================");

        LicenseKeyGenerator generator = new LicenseKeyGenerator();

        String licenseKey = generator.generateLicenseKey("Sanjeev Kumar", "sanjeev@test.com", "BPPRO-PC-A7D8F2", LicenseType.PROFESSIONAL, 365, 1);

        System.out.println();
        System.out.println("Generated License Key:");
        System.out.println(licenseKey);

        LicenseKeyVerifier verifier = new LicenseKeyVerifier();

        LicenseKeyPayload payload = verifier.verify(licenseKey);

        if (payload == null) {

            System.out.println();
            System.out.println("FAILED");
            return;
        }

        System.out.println();
        System.out.println("Payload Restored");

        System.out.println("Customer Name : " + payload.getCustomerName());

        System.out.println("Customer Email: " + payload.getCustomerEmail());

        System.out.println("Machine ID    : " + payload.getMachineId());

        System.out.println("License Type  : " + payload.getLicenseType());

        System.out.println("Issue Date    : " + payload.getIssueDate());

        System.out.println("Expiry Date   : " + payload.getExpiryDate());

        System.out.println("Max Devices   : " + payload.getMaxDevices());

        System.out.println("PASS");
    }

    /**
     * TEST 4
     * B2 Activation Flow
     */
    private static void testActivationManagerB2() throws Exception {

        System.out.println();
        System.out.println("================================");
        System.out.println("TEST 4 : ACTIVATION MANAGER");
        System.out.println("================================");

        MachineFingerprintService machineService = new MachineFingerprintService();

        String machineId = machineService.generateMachineId();

        System.out.println("Machine ID : " + machineId);

        LicenseKeyGenerator keyGenerator = new LicenseKeyGenerator();

        String licenseKey = keyGenerator.generateLicenseKey("Sanjeev Kumar", "sanjeev@test.com", machineId, LicenseType.PROFESSIONAL, 365, 1);

        System.out.println();
        System.out.println("Generated License Key:");

        System.out.println(licenseKey);

        ActivationManager activationManager = new ActivationManager();

        LicenseInfo licenseInfo = activationManager.activateLicense(licenseKey);

        System.out.println();
        System.out.println("License Activated");

        System.out.println("Customer Name : " + licenseInfo.getCustomerName());

        System.out.println("Customer Email: " + licenseInfo.getCustomerEmail());

        System.out.println("Machine ID    : " + licenseInfo.getMachineId());

        System.out.println("License Type  : " + licenseInfo.getLicenseType());

        System.out.println("Issue Date    : " + licenseInfo.getIssueDate());

        System.out.println("Expiry Date   : " + licenseInfo.getExpiryDate());

        System.out.println("Activated     : " + licenseInfo.isActivated());

        System.out.println();
        System.out.println("PASS");
    }
}