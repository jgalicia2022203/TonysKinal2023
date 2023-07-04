package org.juangalicia.bean;

import java.sql.Time;
import java.util.Date;

public class Services_has_Employees {
    private int Services_codeService;
    private int codeService;
    private int codeEmployee;
    private Date dateEvent;
    private Time hourEvent;
    private String placeEvent;

    public Services_has_Employees() {
    }

    public Services_has_Employees(int Services_codeService, int codeService, int codeEmployee, Date dateEvent, Time hourEvent, String placeEvent) {
        this.Services_codeService = Services_codeService;
        this.codeService = codeService;
        this.codeEmployee = codeEmployee;
        this.dateEvent = dateEvent;
        this.hourEvent = hourEvent;
        this.placeEvent = placeEvent;
    }

    public int getServices_codeService() {
        return Services_codeService;
    }

    public void setServices_codeService(int Services_codeService) {
        this.Services_codeService = Services_codeService;
    }

    public int getCodeService() {
        return codeService;
    }

    public void setCodeService(int codeService) {
        this.codeService = codeService;
    }

    public int getCodeEmployee() {
        return codeEmployee;
    }

    public void setCodeEmployee(int codeEmployee) {
        this.codeEmployee = codeEmployee;
    }

    public Date getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    public Time getHourEvent() {
        return hourEvent;
    }

    public void setHourEvent(Time hourEvent) {
        this.hourEvent = hourEvent;
    }

    public String getPlaceEvent() {
        return placeEvent;
    }

    public void setPlaceEvent(String placeEvent) {
        this.placeEvent = placeEvent;
    }
    
}
