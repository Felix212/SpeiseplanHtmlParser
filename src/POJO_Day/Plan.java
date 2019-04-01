
package POJO_Day;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plan {

    @SerializedName("SuppeBeschreibung")
    @Expose
    private String suppeBeschreibung;
    @SerializedName("SuppePreis")
    @Expose
    private String suppePreis;
    @SerializedName("VeggieBeschreibung")
    @Expose
    private String veggieBeschreibung;
    @SerializedName("VeggiePreis")
    @Expose
    private String veggiePreis;
    @SerializedName("DessertBeschreibung")
    @Expose
    private String dessertBeschreibung;
    @SerializedName("DessertPreis")
    @Expose
    private String dessertPreis;

    public String getSuppeBeschreibung() {
        return suppeBeschreibung;
    }

    public void setSuppeBeschreibung(String suppeBeschreibung) {
        this.suppeBeschreibung = suppeBeschreibung;
    }

    public String getSuppePreis() {
        return suppePreis;
    }

    public void setSuppePreis(String suppePreis) {
        this.suppePreis = suppePreis;
    }

    public String getVeggieBeschreibung() {
        return veggieBeschreibung;
    }

    public void setVeggieBeschreibung(String veggieBeschreibung) {
        this.veggieBeschreibung = veggieBeschreibung;
    }

    public String getVeggiePreis() {
        return veggiePreis;
    }

    public void setVeggiePreis(String veggiePreis) {
        this.veggiePreis = veggiePreis;
    }

    public String getDessertBeschreibung() {
        return dessertBeschreibung;
    }

    public void setDessertBeschreibung(String dessertBeschreibung) {
        this.dessertBeschreibung = dessertBeschreibung;
    }

    public String getDessertPreis() {
        return dessertPreis;
    }

    public void setDessertPreis(String dessertPreis) {
        this.dessertPreis = dessertPreis;
    }

}
