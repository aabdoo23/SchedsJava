package com.example.scheds;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Slot {
    private final String[] days = {"Saturday","Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private final String[] dayValues = new String[6];

    private String time;

    public Slot() {
        this.time = "";
    }
    public void setTime(String time){
        this.time=time;
    }

    public void setDayValue(String day, String value) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day)) {
                dayValues[i] = value;
            }
        }
    }

    public
    StringProperty getDayProperty(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(day)) {
                return new SimpleStringProperty(dayValues[i]);
            }
        }
        return new SimpleStringProperty("");
    }

    public StringProperty timeProperty() {
        return new SimpleStringProperty(time);
    }
}
