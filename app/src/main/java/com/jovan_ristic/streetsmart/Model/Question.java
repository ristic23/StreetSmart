package com.jovan_ristic.streetsmart.Model;

public class Question
{


    private String headerQ, bodyQ, aA, aB, aC, aD;

    private boolean booleanA, booleanB, booleanC, booleanD;

    private double latitude, longitude;

    public Question()
    {
        headerQ = bodyQ = aA = aB = aC = aD = "";
    }

    public String getHeaderQ() {
        return headerQ;
    }

    public void setHeaderQ(String headerQ) {
        this.headerQ = headerQ;
    }

    public String getBodyQ() {
        return bodyQ;
    }

    public void setBodyQ(String bodyQ) {
        this.bodyQ = bodyQ;
    }

    public String getaA() {
        return aA;
    }

    public void setaA(String aA) {
        this.aA = aA;
    }

    public String getaB() {
        return aB;
    }

    public void setaB(String aB) {
        this.aB = aB;
    }

    public String getaC() {
        return aC;
    }

    public void setaC(String aC) {
        this.aC = aC;
    }

    public String getaD() {
        return aD;
    }

    public void setaD(String aD) {
        this.aD = aD;
    }

    public boolean isBooleanA() {
        return booleanA;
    }

    public void setBooleanA(boolean booleanA) {
        this.booleanA = booleanA;
    }

    public boolean isBooleanB() {
        return booleanB;
    }

    public void setBooleanB(boolean booleanB) {
        this.booleanB = booleanB;
    }

    public boolean isBooleanC() {
        return booleanC;
    }

    public void setBooleanC(boolean booleanC) {
        this.booleanC = booleanC;
    }

    public boolean isBooleanD() {
        return booleanD;
    }

    public void setBooleanD(boolean booleanD) {
        this.booleanD = booleanD;
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
}
