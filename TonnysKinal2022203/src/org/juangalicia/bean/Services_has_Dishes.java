package org.juangalicia.bean;

public class Services_has_Dishes {
    private int Services_codeService;
    private int codeDish;
    private int codeService;

    public Services_has_Dishes() {
    }

    public Services_has_Dishes(int Services_codeService, int codeDish, int codeService) {
        this.Services_codeService = Services_codeService;
        this.codeDish = codeDish;
        this.codeService = codeService;
    }

    public int getServices_codeService() {
        return Services_codeService;
    }

    public void setServices_codeService(int Services_codeService) {
        this.Services_codeService = Services_codeService;
    }

    public int getCodeDish() {
        return codeDish;
    }

    public void setCodeDish(int codeDish) {
        this.codeDish = codeDish;
    }

    public int getCodeService() {
        return codeService;
    }

    public void setCodeService(int codeService) {
        this.codeService = codeService;
    }
    
}
