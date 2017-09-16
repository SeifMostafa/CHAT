package com.example.seif.seshatplayer.model;

import java.util.ArrayList;

public class Person {
    ArrayList<String> whyLearn = null;
    ArrayList<String> favNames = null;
    private String name = null, address = null, job = null, gender = null, birthAddress = null, envLocation = null, orgHome = null;
    private int age;

    public Person(String name, String address, int age) {
        super();
        this.name = name;
        this.address = address;
        this.age = age;
    }


    public Person(String name, String address, String job, String gender, String birthAddress, String envLocation,
                  String orgHome, ArrayList<String> whyLearn, ArrayList<String> favNames, int age) {
        super();
        this.name = name;
        this.address = address;
        this.job = job;
        this.gender = gender;
        this.birthAddress = birthAddress;
        this.envLocation = envLocation;
        this.orgHome = orgHome;
        this.whyLearn = whyLearn;
        this.favNames = favNames;
        this.age = age;
    }


    public Person() {
        super();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public ArrayList<String> getFavNames() {
        return favNames;
    }

    public void setFavNames(ArrayList<String> favNames2) {
        this.favNames = favNames2;
    }

    public ArrayList<String> getWhyLearn() {
        return whyLearn;
    }

    public void setWhyLearn(ArrayList<String> whyMsgs) {
        this.whyLearn = whyMsgs;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthAddress() {
        return birthAddress;
    }

    public void setBirthAddress(String birthAddress) {
        this.birthAddress = birthAddress;
    }

    public String getEnvLocation() {
        return envLocation;
    }

    public void setEnvLocation(String envLocation) {
        this.envLocation = envLocation;
    }

    public String getOrgHome() {
        return orgHome;
    }

    public void setOrgHome(String orgHome) {
        this.orgHome = orgHome;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}