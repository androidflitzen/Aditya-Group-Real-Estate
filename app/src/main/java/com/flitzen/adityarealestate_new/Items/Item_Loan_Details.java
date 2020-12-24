package com.flitzen.adityarealestate_new.Items;

import java.util.ArrayList;

public class Item_Loan_Details {

    String id;
    String Applicantion_Number;
    String Loan_Amount;
    String Approved_Amount;
    String Interest_Rate;
    String Loan_Status;
    String Loan_Type;
    String Monthly_EMI;
    String Date_Applied;

    ArrayList<Item_Loan_EMI> arrayListEMI = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantion_Number() {
        return Applicantion_Number;
    }

    public void setApplicantion_Number(String applicantion_Number) {
        Applicantion_Number = applicantion_Number;
    }

    public String getLoan_Amount() {
        return Loan_Amount;
    }

    public void setLoan_Amount(String loan_Amount) {
        Loan_Amount = loan_Amount;
    }

    public String getApproved_Amount() {
        return Approved_Amount;
    }

    public void setApproved_Amount(String approved_Amount) {
        Approved_Amount = approved_Amount;
    }

    public String getInterest_Rate() {
        return Interest_Rate;
    }

    public void setInterest_Rate(String interest_Rate) {
        Interest_Rate = interest_Rate;
    }

    public String getLoan_Status() {
        return Loan_Status;
    }

    public void setLoan_Status(String loan_Status) {
        Loan_Status = loan_Status;
    }

    public String getLoan_Type() {
        return Loan_Type;
    }

    public void setLoan_Type(String loan_Type) {
        Loan_Type = loan_Type;
    }

    public String getMonthly_EMI() {
        return Monthly_EMI;
    }

    public void setMonthly_EMI(String monthly_EMI) {
        Monthly_EMI = monthly_EMI;
    }

    public String getDate_Applied() {
        return Date_Applied;
    }

    public void setDate_Applied(String date_Applied) {
        Date_Applied = date_Applied;
    }

    public ArrayList<Item_Loan_EMI> getArrayListEMI() {
        return arrayListEMI;
    }

    public void setArrayListEMI(ArrayList<Item_Loan_EMI> arrayListEMI) {
        this.arrayListEMI = arrayListEMI;
    }

    public static class Item_Loan_EMI {
        String id;
        String EMI_Amount;
        String Loan_Remarks;
        String customer_id;
        String customer_name;
        String EMI_Date;
        String EMI_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEMI_Amount() {
            return EMI_Amount;
        }

        public void setEMI_Amount(String EMI_Amount) {
            this.EMI_Amount = EMI_Amount;
        }

        public String getLoan_Remarks() {
            return Loan_Remarks;
        }

        public void setLoan_Remarks(String loan_Remarks) {
            Loan_Remarks = loan_Remarks;
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

        public String getEMI_Date() {
            return EMI_Date;
        }

        public void setEMI_Date(String EMI_Date) {
            this.EMI_Date = EMI_Date;
        }

        public String getEMI_time() {
            return EMI_time;
        }

        public void setEMI_time(String EMI_time) {
            this.EMI_time = EMI_time;
        }
    }

}
