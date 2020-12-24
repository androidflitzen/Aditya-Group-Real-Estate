package com.flitzen.adityarealestate_new.Items;

import java.io.Serializable;

public class RentDetailsForPDF implements Serializable {
    String propertyName;
    String hiredSince;
    String address;
    String rent;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getHiredSince() {
        return hiredSince;
    }

    public void setHiredSince(String hiredSince) {
        this.hiredSince = hiredSince;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }
}