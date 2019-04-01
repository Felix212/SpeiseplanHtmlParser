
package POJO_Week;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plan {

    @SerializedName("Monday")
    @Expose
    private Monday monday;
    @SerializedName("Tuesday")
    @Expose
    private Tuesday tuesday;
    @SerializedName("Wednesday")
    @Expose
    private Wednesday wednesday;
    @SerializedName("Thursday")
    @Expose
    private Thursday thursday;
    @SerializedName("Friday")
    @Expose
    private Friday friday;

    public Monday getMonday() {
        return monday;
    }

    public void setMonday(Monday monday) {
        this.monday = monday;
    }

    public Tuesday getTuesday() {
        return tuesday;
    }

    public void setTuesday(Tuesday tuesday) {
        this.tuesday = tuesday;
    }

    public Wednesday getWednesday() {
        return wednesday;
    }

    public void setWednesday(Wednesday wednesday) {
        this.wednesday = wednesday;
    }

    public Thursday getThursday() {
        return thursday;
    }

    public void setThursday(Thursday thursday) {
        this.thursday = thursday;
    }

    public Friday getFriday() {
        return friday;
    }

    public void setFriday(Friday friday) {
        this.friday = friday;
    }

}
