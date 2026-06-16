package com.brandpdfpro.service;

import com.brandpdfpro.controller.MainController;
import com.brandpdfpro.model.ProcessingProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for managing configuration profiles using JSON serialization.
 */
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    private static final String PROFILES_DIR = "profiles";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProfileService() {
        File profilesFolder = new File(PROFILES_DIR);
        if (!profilesFolder.exists()) {
            logger.info("Profiles repository directory absent. Creating path container at: /{}", PROFILES_DIR);
            profilesFolder.mkdirs();
        }
    }

    /**
     * Serializes a runtime configuration profile into a local JSON storage record file.
     *
     * @throws IOException if writing to the physical filesystem channel fails
     */
    public void saveProfile(ProcessingProfile profile) throws IOException {
        if (profile == null) {
            logger.error("Save rejected: Operational profile reference cannot be null.");
            throw new IllegalArgumentException("Profile cannot be null.");
        }

        if (profile.getProfileName() == null || profile.getProfileName().trim().isEmpty()) {
            logger.error("Save rejected: Provided configuration profile name must not be empty.");
            throw new IllegalArgumentException("Profile name cannot be empty.");
        }

        String profileName = profile.getProfileName().trim();
        File profileFile = new File(PROFILES_DIR, profileName + ".json");

        logger.info("Writing workspace state layout indices to profile target record: {}", profileName);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(profileFile, profile);
        logger.info("Profile configurations flushed successfully down onto path: {}", profileFile.getAbsolutePath());
    }

    /**
     * Deserializes and loads a configuration profile from disk by name.
     *
     * @throws IOException if reading or mapping the JSON content fails
     */
    public ProcessingProfile loadProfile(String profileName) throws IOException {
        File profileFile = new File(PROFILES_DIR, profileName + ".json");

        if (!profileFile.exists()) {
            logger.warn("Lookup failed: Specified profile target registry record absent: {}", profileName);
            throw new IllegalArgumentException("Profile not found : " + profileName);
        }

        logger.info("Streaming and parsing layout properties for profile token: {}", profileName);
        return objectMapper.readValue(profileFile, ProcessingProfile.class);
    }

    /**
     * Discovers and compiles an ordered list of all available profile identifiers.
     */
    public List<String> getAllProfiles() {
        logger.debug("Scanning profile registry path directory for existing configuration contexts.");
        File profilesFolder = new File(PROFILES_DIR);

        File[] profileFiles = profilesFolder.listFiles(file ->
                file.isFile() && file.getName().toLowerCase().endsWith(".json")
        );

        List<String> profiles = new ArrayList<>();
        if (profileFiles != null) {
            Arrays.sort(profileFiles);
            for (File file : profileFiles) {
                String profileName = file.getName().replace(".json", "");
                profiles.add(profileName);
            }
        }

        logger.debug("Profiles lookup scan completed. Found {} entries.", profiles.size());
        return profiles;
    }

    /**
     * Permanently purges a configuration profile from filesystem storage.
     */
    public boolean deleteProfile(String profileName) {
        File profileFile = new File(PROFILES_DIR, profileName + ".json");

        if (!profileFile.exists()) {
            logger.warn("Truncation skipped: Profile record target not found: {}", profileName);
            return false;
        }

        logger.warn("Attempting file truncation mapping for profile configuration: {}", profileName);
        boolean deleted = profileFile.delete();

        if (deleted) {
            logger.info("Profile configuration record '{}' successfully erased from storage index.", profileName);
        } else {
            logger.error("OS file lock conflict caught preventing deletion execution on profile: {}", profileName);
        }

        return deleted;
    }

    /**
     * Determines whether a specific named configuration entry exists on disk.
     */
    public boolean profileExists(String profileName) {
        File profileFile = new File(PROFILES_DIR, profileName + ".json");
        return profileFile.exists();
    }

    public List<String> getProfileNames() {
        return List.of();
    }
}