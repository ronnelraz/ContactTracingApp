package com.example.contacttracingapp.GetterSetter;

public class gs_scanned_all {

    public String type,name,temp,date,company;

    public gs_scanned_all(String type, String name, String temp, String date, String company) {
        this.type = type;
        this.name = name;
        this.temp = temp;
        this.date = date;
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
