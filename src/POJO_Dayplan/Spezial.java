package POJO_Dayplan;

import org.joda.time.DateTime;

public class Spezial {
    private String description = "";
    public DateTime date;
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String price = "";

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String name = "";

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}