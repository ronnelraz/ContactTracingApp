package com.razo.contacttracingapp.GetterSetter;

public class gs_blocklist {

    public String id,name,type,company,plate,ban_type,start,end,remark;


    public gs_blocklist(String id, String name, String type, String company, String plate, String ban_type, String start, String end, String remark) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.company = company;
        this.plate = plate;
        this.ban_type = ban_type;
        this.start = start;
        this.end = end;
        this.remark = remark;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBan_type() {
        return ban_type;
    }

    public void setBan_type(String ban_type) {
        this.ban_type = ban_type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
