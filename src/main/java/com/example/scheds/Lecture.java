package com.example.scheds;

import java.time.LocalTime;

public class Lecture {
    public String courseCode;
    String courseName;
    String lectureNumber;

    String day;
    LocalTime startTime,endTime;
    String instructor;


    @Override
    public String toString() {
        return "Lecture" +
                "\n, courseCode=" + courseCode +
                "\n, courseName=" + courseName +
                "\n, lectureCode=" + lectureNumber +
                "\n, day=" + day +
                "\n, startTime=" + startTime +
                "\n, endTime=" + endTime +
                "\n, instructor=" + instructor +
                '\n';
    }
}
