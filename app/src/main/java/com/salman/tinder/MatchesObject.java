package com.salman.tinder;

/**
 * Created by SALMAN on 3/25/2018.
 */

public class MatchesObject {
    private String userId;
    private String profileUrl;
    private String name;
    private String phone;

    public MatchesObject(String userId, String profileUrl, String name, String phone) {
        this.userId = userId;
        this.profileUrl = profileUrl;
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {

        return name;
    }

    public String getPhone() {
        return phone;
    }



    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
