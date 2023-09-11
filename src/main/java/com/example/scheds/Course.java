package com.example.scheds;

import java.util.LinkedList;

public class Course {
    String courseCode,courseName;
    LinkedList<Lecture>lectures=new LinkedList<>();
    LinkedList<Tutorial>tutorials=new LinkedList<>();
    LinkedList<Lab>labs=new LinkedList<>();


    @Override
    public String toString() {
        return courseCode +": "+ courseName+ '\n';
    }
}
