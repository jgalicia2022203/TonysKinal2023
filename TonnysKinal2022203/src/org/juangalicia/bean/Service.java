 package org.juangalicia.bean;

import java.sql.Time;
 import java.util.Date;

 public class Service {
    private int codeService;
    private Date dateService;
    private String typeService;
    private Time hourService;
    private String placeService;
    private String phoneContact;
    private int codeCompany;

    public Service() {
    }

    public Service(int codeService, Date dateService, String typeService, Time hourService, String placeService, String phoneContact, int codeCompany) {
        this.codeService = codeService;
        this.dateService = dateService;
        this.typeService = typeService;
        this.hourService = hourService;
        this.placeService = placeService;
        this.phoneContact = phoneContact;
        this.codeCompany = codeCompany;
    }

    public int getCodeService() {
        return codeService;
    }

    public void setCodeService(int codeService) {
        this.codeService = codeService;
    }

    public Date getDateService() {
        return dateService;
    }

    public void setDateService(Date dateService) {
        this.dateService = dateService;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public Time getHourService() {
        return hourService;
    }

    public void setHourService(Time hourService) {
        this.hourService = hourService;
    }

    public String getPlaceService() {
        return placeService;
    }

    public void setPlaceService(String placeService) {
        this.placeService = placeService;
    }

    public String getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(String phoneContact) {
        this.phoneContact = phoneContact;
    }

    public int getCodeCompany() {
        return codeCompany;
    }

    public void setCodeCompany(int codeCompany) {
        this.codeCompany = codeCompany;
    }

    @Override
    public String toString() {
        return codeService + " │ " + typeService;
    }
    
    
 }
