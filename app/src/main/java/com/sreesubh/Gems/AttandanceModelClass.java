package com.sreesubh.Gems;

public class AttandanceModelClass {
    String status, ltime,lotime, date,ot,latein,earlyout,worktime;

    public String getStatus() {
        return status;
    }

    public AttandanceModelClass(String status, String ltime, String lotime, String date, String ot, String latein, String earlyout, String worktime) {
        this.status = status;
        this.ltime = ltime;
        this.lotime = lotime;
        this.date = date;
        this.ot = ot;
        this.latein = latein;
        this.earlyout = earlyout;
        this.worktime = worktime;
    }

    public String getOt() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot = ot;
    }

    public String getLatein() {
        return latein;
    }

    public void setLatein(String latein) {
        this.latein = latein;
    }

    public String getEarlyout() {
        return earlyout;
    }

    public void setEarlyout(String earlyout) {
        this.earlyout = earlyout;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLtime() {
        return ltime;
    }

    public void setLtime(String ltime) {
        this.ltime = ltime;
    }

    public String getLotime() {
        return lotime;
    }

    public void setLotime(String lotime) {
        this.lotime = lotime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
