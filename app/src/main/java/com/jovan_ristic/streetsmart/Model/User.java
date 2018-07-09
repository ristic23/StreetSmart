package com.jovan_ristic.streetsmart.Model;

import java.util.ArrayList;
import java.util.List;

public class User
{

    private String userName;
    private String firstName;
    private String lastName;
    private String imagePath;

    private String email;

    private String phone;

    private List<Question> activeQuestions;
    private List<Friend> friendsList;

    private int totalPoints;
    private int rank;

    private double latitude, longitude;


    public User()
    {
        this.totalPoints = rank = -1;
        userName = lastName = firstName = imagePath = email = phone = "";
        activeQuestions = new ArrayList<>();
        friendsList = new ArrayList<>();

        longitude = latitude = 0;
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

    public List<Question> getActiveQuestions() {
        return activeQuestions;
    }

    public void setActiveQuestions(List<Question> activeQuestions) {
        this.activeQuestions = activeQuestions;
    }

    public void addNewQuestion(Question question)
    {
        activeQuestions.add(question);
    }

    public void addNewFriend(Friend friend)
    {
        friendsList.add(friend);
    }

    public List<Friend> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Friend> friendsList) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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