package com.flitzen.adityarealestate_new.Items;

/**
 * Created by Jolly Rathod on 11/15/2018.
 */

public class Items_Loan_Reports {
    public int getrMonth() {
        return rMonth;
    }

    public void setrMonth(int rMonth) {
        this.rMonth = rMonth;
    }

    public double getrEMI() {
        return rEMI;
    }

    public void setrEMI(double rEMI) {
        this.rEMI = rEMI;
    }

    public double getrInterest() {
        return rInterest;
    }

    public void setrInterest(double rInterest) {
        this.rInterest = rInterest;
    }

    public double getrPrincipal() {
        return rPrincipal;
    }

    public void setrPrincipal(double rPrincipal) {
        this.rPrincipal = rPrincipal;
    }

    public double getrAmont() {
        return rAmont;
    }

    public void setrAmont(double rAmont) {
        this.rAmont = rAmont;
    }

    public double getrTotalAmount() {
        return rTotalAmount;
    }

    public void setrTotalAmount(double rTotalAmount) {
        this.rTotalAmount = rTotalAmount;
    }

    int rMonth;
    double rEMI,rInterest,rPrincipal,rAmont,rTotalAmount;
}
