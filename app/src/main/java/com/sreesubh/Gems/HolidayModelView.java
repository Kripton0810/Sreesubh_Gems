package com.sreesubh.Gems;

public class HolidayModelView {
    String name,date,day;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public HolidayModelView(String name, String date, String day) {
        this.name = name;
        this.date = date;
        this.day = day;
    }
}
