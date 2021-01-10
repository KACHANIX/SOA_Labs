package Entities;


public class OrganizationModel {
    public String name;
    public Double turnover;
    public Long y;


    public Double x;
    public String street;
    public Short type;
    public Long employees;

    public OrganizationModel(String name, Double turnover, Long y, Double x, String street, Short type, Long employees) {
        this.name = name;
        this.turnover = turnover;
        this.y = y;
        this.x = x;
        this.street = street;
        this.type = type;
        this.employees = employees;
    }
}
