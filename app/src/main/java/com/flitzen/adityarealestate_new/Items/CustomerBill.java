package com.flitzen.adityarealestate_new.Items;

import java.io.Serializable;

public class CustomerBill implements Serializable {
    public String getBill_id() {
        return Bill_id;
    }

    public void setBill_id(String bill_id) {
        Bill_id = bill_id;
    }

    public String getProperties_id() {
        return Properties_id;
    }

    public void setProperties_id(String properties_id) {
        Properties_id = properties_id;
    }

    public String getBill_month() {
        return Bill_month;
    }

    public void setBill_month(String bill_month) {
        Bill_month = bill_month;
    }

    public String getBill_Rs() {
        return Bill_Rs;
    }

    public void setBill_Rs(String bill_Rs) {
        Bill_Rs = bill_Rs;
    }

    public String getOther_Notes() {
        return Other_Notes;
    }

    public void setOther_Notes(String other_Notes) {
        Other_Notes = other_Notes;
    }

    public String getBill_photo() {
        return Bill_photo;
    }

    public void setBill_photo(String bill_photo) {
        Bill_photo = bill_photo;
    }

    public String getCreate_date() {
        return Create_date;
    }

    public void setCreate_date(String create_date) {
        Create_date = create_date;
    }

    public String getCreate_time() {
        return Create_time;
    }

    public void setCreate_time(String create_time) {
        Create_time = create_time;
    }

    private String Bill_id;
    private String Properties_id;
    private String Bill_month;
    private String Bill_Rs;
    private String Other_Notes;
    private String Bill_photo;
    private String Create_date;
    private String Create_time;
}
