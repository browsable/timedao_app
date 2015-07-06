package com.daemin.enumclass;

/**
 * Created by hernia on 2015-06-27.
 */
public enum User {
    USER;

    User() {}

    private String korUnivName;
    private String engUnivName;
    private boolean groupListDownloadState = false;
    private boolean subjectDownloadStat = false;
    public boolean isGroupListDownloadState() {
        return groupListDownloadState;
    }
    public void setGroupListDownloadState(boolean groupListDownload) {
        this.groupListDownloadState = groupListDownload;
    }
    public boolean isSubjectDownloadState() {
        return subjectDownloadStat;
    }
    public void setSubjectDownloadState(boolean subjectDownload) {
        this.subjectDownloadStat = subjectDownload;
    }
    public String getKorUnivName() {
        return korUnivName;
    }
    public void setKorUnivName(String korUnivName) {
        this.korUnivName = korUnivName;
    }
    public String getEngUnivName() {
        return engUnivName;
    }
    public void setEngUnivName(String engUnivName) {
        this.engUnivName = engUnivName;
    }
}
