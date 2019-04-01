
package POJO_Week;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wednesday {

    @SerializedName("FunkBeschreibung")
    @Expose
    private String funkBeschreibung;
    @SerializedName("FunkPreis")
    @Expose
    private String funkPreis;
    @SerializedName("LunchBeschreibung")
    @Expose
    private String lunchBeschreibung;
    @SerializedName("LunchPreis")
    @Expose
    private String lunchPreis;
    @SerializedName("GourmetBeschreibung")
    @Expose
    private String gourmetBeschreibung;
    @SerializedName("GourmetPreis")
    @Expose
    private String gourmetPreis;

    public String getFunkBeschreibung() {
        return funkBeschreibung;
    }

    public void setFunkBeschreibung(String funkBeschreibung) {
        this.funkBeschreibung = funkBeschreibung;
    }

    public String getFunkPreis() {
        return funkPreis;
    }

    public void setFunkPreis(String funkPreis) {
        this.funkPreis = funkPreis;
    }

    public String getLunchBeschreibung() {
        return lunchBeschreibung;
    }

    public void setLunchBeschreibung(String lunchBeschreibung) {
        this.lunchBeschreibung = lunchBeschreibung;
    }

    public String getLunchPreis() {
        return lunchPreis;
    }

    public void setLunchPreis(String lunchPreis) {
        this.lunchPreis = lunchPreis;
    }

    public String getGourmetBeschreibung() {
        return gourmetBeschreibung;
    }

    public void setGourmetBeschreibung(String gourmetBeschreibung) {
        this.gourmetBeschreibung = gourmetBeschreibung;
    }

    public String getGourmetPreis() {
        return gourmetPreis;
    }

    public void setGourmetPreis(String gourmetPreis) {
        this.gourmetPreis = gourmetPreis;
    }

}
