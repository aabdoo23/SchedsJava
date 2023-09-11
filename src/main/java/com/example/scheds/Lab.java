package com.example.scheds;

import java.time.LocalTime;

public class Lab {

    String labNumber;
    String courseCode;
    String day;
    LocalTime startTime,endTime;
    String instructor;

    @Override
    public String toString() {
        return "Tutorial" +
                "\n, tutorialNumber=" + labNumber +
                "\n, courseCode= "+ courseCode+
                "\n, day=" + day +
                "\n, startTime=" + startTime +
                "\n, endTime=" + endTime +
                "\n, instructor=" + instructor +
                '\n';
    }
}
