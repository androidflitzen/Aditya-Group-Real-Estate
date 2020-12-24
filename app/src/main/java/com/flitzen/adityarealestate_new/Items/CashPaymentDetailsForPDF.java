package com.flitzen.adityarealestate_new.Items;

import java.io.Serializable;

public class CashPaymentDetailsForPDF implements Serializable {
    String siteName;
    String address;
    String size;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}