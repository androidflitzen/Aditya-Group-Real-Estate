package com.flitzen.adityarealestate_new.Items;

import java.util.ArrayList;

public class Item_Property_Detail {

    String id;
    String property_name;
    String address;
    String rent;
    String hired_since;

    ArrayList<Item_Plot_Payment> arrayListPayment = new ArrayList<>();

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

    public ArrayList<Item_Plot_Payment> getArrayListPayment() {
        return arrayListPayment;
    }

    public void setArrayListPayment(ArrayList<Item_Plot_Payment> arrayListPayment) {
        this.arrayListPayment = arrayListPayment;
    }

    public static class Item_Plot_Payment {
        String id;
        String amount;
        String remarks;
        String payment_date;
        String payment_time;
        String payment_attachment;
        String file_type;

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getPayment_attachment() {
            return payment_attachment;
        }

        public void setPayment_attachment(String payment_attachment) {
            this.payment_attachment = payment_attachment;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getPayment_date() {
            return payment_date;
        }

        public void setPayment_date(String payment_date) {
            this.payment_date = payment_date;
        }

        public String getPayment_time() {
            return payment_time;
        }

        public void setPayment_time(String payment_time) {
            this.payment_time = payment_time;
        }
    }
}
