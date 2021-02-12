package com.netist.mygirlshostel.appversion;

public class AppVersionModel {

    private int appversionid;
    private String versionno;
    private String description;
    private String createdat;

    public AppVersionModel() {
    }

    public AppVersionModel(int appversionid, String versionno, String description, String createdat) {
        this.appversionid = appversionid;
        this.versionno = versionno;
        this.description = description;
        this.createdat = createdat;
    }

    @Override
    public String toString() {
        return "AppVersionModel{" +
                "appversionid=" + appversionid +
                ", versionno='" + versionno + '\'' +
                ", description='" + description + '\'' +
                ", createdat='" + createdat + '\'' +
                '}';
    }

    public int getAppversionid() {
        return appversionid;
    }

    public void setAppversionid(int appversionid) {
        this.appversionid = appversionid;
    }

    public String getVersionno() {
        return versionno;
    }

    public void setVersionno(String versionno) {
        this.versionno = versionno;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }
}