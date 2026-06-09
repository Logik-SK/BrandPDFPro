package com.brandpdfpro.model;

import java.util.ArrayList;
import java.util.List;

public class RecentItems {

    private List<String> recentPdfs =
            new ArrayList<>();

    private List<String> recentInputFolders =
            new ArrayList<>();

    private List<String> recentOutputFolders =
            new ArrayList<>();

    public List<String> getRecentPdfs() {
        return recentPdfs;
    }

    public void setRecentPdfs(List<String> recentPdfs) {
        this.recentPdfs = recentPdfs;
    }

    public List<String> getRecentInputFolders() {
        return recentInputFolders;
    }

    public void setRecentInputFolders(List<String> recentInputFolders) {
        this.recentInputFolders = recentInputFolders;
    }

    public List<String> getRecentOutputFolders() {
        return recentOutputFolders;
    }

    public void setRecentOutputFolders(List<String> recentOutputFolders) {
        this.recentOutputFolders = recentOutputFolders;
    }
}