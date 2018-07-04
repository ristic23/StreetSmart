package com.jovan_ristic.streetsmart.Model;

import java.util.ArrayList;

public class User
{

    private String userName;
    private String firstName;
    private String lastName;
    private String imagePath;

    private String email;

    private String phone;

    private ArrayList<Question> activeQuestions;
    private ArrayList<Friend> friendsList;

    private int totalPoints;
    private int rank;

    private long langitude, longitude;


    public User()
    {
        this.totalPoints = rank = -1;
        userName = lastName = firstName = imagePath = email = phone = "";
        activeQuestions = new ArrayList<>();
        friendsList = new ArrayList<>();

        longitude = langitude = 0;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<Question> getActiveQuestions() {
        return activeQuestions;
    }

    public void setActiveQuestions(ArrayList<Question> activeQuestions) {
        this.activeQuestions = activeQuestions;
    }

    public ArrayList<Friend> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<Friend> friendsList) {
        this.friendsList = friendsList;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getLangitude() {
        return langitude;
    }

    public void setLangitude(long langitude) {
        this.langitude = langitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}