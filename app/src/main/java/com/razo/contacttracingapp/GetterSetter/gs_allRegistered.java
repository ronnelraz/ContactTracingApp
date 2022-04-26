package com.razo.contacttracingapp.GetterSetter;

public class gs_allRegistered {


   public String type,cn,plate,name,gender,dob,age,address,contact,lname,fname,img,id,province,city,brgy,vaccinated;

    public gs_allRegistered(String type, String cn, String plate, String name, String gender, String dob, String age, String address, String contact, String lname, String fname, String img, String id, String province, String city, String brgy, String vaccinated) {
        this.type = type;
        this.cn = cn;
        this.plate = plate;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.age = age;
        this.address = address;
        this.contact = contact;
        this.lname = lname;
        this.fname = fname;
        this.img = img;
        this.id = id;
        this.province = province;
        this.city = city;
        this.brgy = brgy;
        this.vaccinated = vaccinated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBrgy() {
        return brgy;
    }

    public void setBrgy(String brgy) {
        this.brgy = brgy;
    }

    public String getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(String vaccinated) {
        this.vaccinated = vaccinated;
    }
}
