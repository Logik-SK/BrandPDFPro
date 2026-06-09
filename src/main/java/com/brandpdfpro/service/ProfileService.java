package com.brandpdfpro.service;

import com.brandpdfpro.model.ProcessingProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileService {

    private static final String PROFILES_DIR = "profiles";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProfileService() {

        File profilesFolder = new File(PROFILES_DIR);

        if (!profilesFolder.exists()) {
            profilesFolder.mkdirs();
        }
    }

    /**
     * Save profile as JSON
     */
    public void saveProfile(ProcessingProfile profile) throws IOException {

        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null.");
        }

        if (profile.getProfileName() == null ||
                profile.getProfileName().trim().isEmpty()) {
            throw new IllegalArgumentException("Profile name cannot be empty.");
        }

        File profileFile = new File(
                PROFILES_DIR,
                profile.getProfileName().trim() + ".json"
        );

        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(profileFile, profile);

        System.out.println(
                "Profile saved : "
                        + profileFile.getAbsolutePath()
        );
    }

    /**
     * Load profile by name
     */
    public ProcessingProfile loadProfile(String profileName)
            throws IOException {

        File profileFile = new File(
                PROFILES_DIR,
                profileName + ".json"
        );

        if (!profileFile.exists()) {
            throw new IllegalArgumentException(
                    "Profile not found : " + profileName
            );
        }

        return objectMapper.readValue(
                profileFile,
                ProcessingProfile.class
        );
    }

    /**
     * Get all profile names
     */
    public List<String> getAllProfiles() {

        File profilesFolder = new File(PROFILES_DIR);

        File[] profileFiles =
                profilesFolder.listFiles(
                        file ->
                                file.isFile()
                                        && file.getName()
                                        .toLowerCase()
                                        .endsWith(".json")
                );

        List<String> profiles = new ArrayList<>();

        if (profileFiles != null) {

            Arrays.sort(profileFiles);

            for (File file : profileFiles) {

                String profileName =
                        file.getName()
                                .replace(".json", "");

                profiles.add(profileName);
            }
        }

        return profiles;
    }

    /**
     * Delete profile
     */
    public boolean deleteProfile(String profileName) {

        File profileFile = new File(
                PROFILES_DIR,
                profileName + ".json"
        );

        if (!profileFile.exists()) {
            return false;
        }

        boolean deleted = profileFile.delete();

        if (deleted) {
            System.out.println(
                    "Profile deleted : "
                            + profileName
            );
        }

        return deleted;
    }

    /**
     * Check if profile exists
     */
    public boolean profileExists(String profileName) {

        File profileFile = new File(
                PROFILES_DIR,
                profileName + ".json"
        );

        return profileFile.exists();
    }
}