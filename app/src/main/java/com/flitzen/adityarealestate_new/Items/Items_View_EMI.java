package com.flitzen.adityarealestate_new.Items;

import java.io.Serializable;

/**
 * Created by Jolly Rathod on 11/15/2018.
 */

public class Items_View_EMI implements Serializable {

    private String emi_id;
    private String emi_date;
    private String emi_amount;
    private String emi_remark;
    private String emi_type;

    public String getEmi_type() {
        return emi_type;
    }

    public void setEmi_type(String emi_type) {
        this.emi_type = emi_type;
    }

    public String getEmi_remark() {
        return emi_remark;
    }

    public void setEmi_remark(String emi_remark) {
        this.emi_remark = emi_remark;
    }

    public void setEmi_id(String emi_id) {
        this.emi_id = emi_id;
    }

    public void setEmi_date(String emi_date) {
        this.emi_date = emi_date;
    }

    public void setEmi_amount(String emi_amount) {
        this.emi_amount = emi_amount;
    }

    public String getEmi_id() {
        return emi_id;
    }

    public String getEmi_date() {
        return emi_date;
    }

    public String getEmi_amount() {
        return emi_amount;
    }
}
