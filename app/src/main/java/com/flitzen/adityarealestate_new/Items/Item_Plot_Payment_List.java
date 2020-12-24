package com.flitzen.adityarealestate_new.Items;

import java.io.Serializable;

public class Item_Plot_Payment_List implements Serializable {

    String  id;
    String amount;
    String remarks;
    String customer_id;
    String customer_name;
    String payment_date;
    String payment_time;
    String payment_attachment;
    String next_payment_date;
    String fileType;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getNext_payment_date() {
        return next_payment_date;
    }

    public void setNext_payment_date(String next_payment_date) {
        this.next_payment_date = next_payment_date;
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

    public String getPayment_attachment() {
        return payment_attachment;
    }

    public void setPayment_attachment(String payment_attachment) {
        this.payment_attachment = payment_attachment;
    }
}
