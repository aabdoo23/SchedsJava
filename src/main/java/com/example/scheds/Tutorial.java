package com.example.scheds;

import java.time.LocalTime;

public class Tutorial {
    String tutorialNumber;
    String courseCode;
    String day;
    LocalTime startTime,endTime;
    String instructor;

    @Override
    public String toString() {
        return "Tutorial" +
                "\n, tutorialNumber=" + tutorialNumber +
                "\n, day=" + day +
                "\n, startTime=" + startTime +
                "\n, endTime=" + endTime +
                "\n, instructor=" + instructor +
                '\n';
    }
}
