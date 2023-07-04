package org.juangalicia.bean;

public class TypeEmployee {
    private int codeTypeEmployee;
    private String descript;

    public TypeEmployee() {
    }

    public TypeEmployee(int codeTypeEmployee, String descript) {
        this.codeTypeEmployee = codeTypeEmployee;
        this.descript = descript;
    }

    public int getCodeTypeEmployee() {
        return codeTypeEmployee;
    }

    public void setCodeTypeEmployee(int codeTypeEmployee) {
        this.codeTypeEmployee = codeTypeEmployee;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    @Override
    public String toString() {
        return codeTypeEmployee + " │ " + descript;
    }

}
