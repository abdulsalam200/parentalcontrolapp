package com.example.myparentalcontrolapp.models;

public class Child {
    private String name;
    private String age;
    private String gender;
    private String id;

    private Long timeLimit;

    private String blockedApps;
    public Child(String name, String age, String gender, String id, Long timeLimit, String blockedApps) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.id = id;
        this.timeLimit = timeLimit;
        this.blockedApps = blockedApps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeLimit(Long timeLimit) {this.timeLimit = timeLimit;}

    public Long getTimeLimit() {return timeLimit;}

    public void setBlockedApps(String blockedApps) {
        this.blockedApps = blockedApps;
    }

    public String getBlockedApps() {
        return blockedApps;
    }
}

