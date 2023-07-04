package org.juangalicia.bean;

import java.util.Date;

public class Budget {

    private int codeBudget;
    private Date dateRequest;
    private double amountBudget;
    private int codeCompany;

    public Budget() {
    }

    public Budget(int codeBudget, Date dateRequest, double amountBudget, int codeCompany) {
        this.codeBudget = codeBudget;
        this.dateRequest = dateRequest;
        this.amountBudget = amountBudget;
        this.codeCompany = codeCompany;
    }

    public int getCodeBudget() {
        return codeBudget;
    }

    public void setCodeBudget(int codeBudget) {
        this.codeBudget = codeBudget;
    }

    public Date getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(Date dateRequest) {
        this.dateRequest = dateRequest;
    }

    public double getAmountBudget() {
        return amountBudget;
    }

    public void setAmountBudget(double amountBudget) {
        this.amountBudget = amountBudget;
    }

    public int getCodeCompany() {
        return codeCompany;
    }

    public void setCodeCompany(int codeCompany) {
        this.codeCompany = codeCompany;
    }

}
