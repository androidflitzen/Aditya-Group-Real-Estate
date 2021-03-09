package com.flitzen.adityarealestate_new.Items;

public class Item_Property_List_New {

    String id;
    String key;
    String property_name;
    String address;
    String is_hired;
    String customer_id;
    String customer_name;
    String contact_no;
    String rent;
    String hired_since;
    boolean checkDateIsGone;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isCheckDateIsGone() {
        return checkDateIsGone;
    }

    public void setCheckDateIsGone(boolean checkDateIsGone) {
        this.checkDateIsGone = checkDateIsGone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty_name() {
        return property_name;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIs_hired() {
        return is_hired;
    }

    public void setIs_hired(String is_hired) {
        this.is_hired = is_hired;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getHired_since() {
        return hired_since;
    }

    public void setHired_since(String hired_since) {
        this.hired_since = hired_since;
    }
}
