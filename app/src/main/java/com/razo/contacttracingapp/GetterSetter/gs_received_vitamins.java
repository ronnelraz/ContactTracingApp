package com.razo.contacttracingapp.GetterSetter;

public class gs_received_vitamins {
    public String name,qty,date;

    public gs_received_vitamins(String name, String qty, String date) {
        this.name = name;
        this.qty = qty;
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
