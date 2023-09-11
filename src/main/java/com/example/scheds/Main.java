package com.example.scheds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Objects;
import com.example.scheds.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Main menu");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        try {
//            Runtime.getRuntime().exec("python3 "+"script.py");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        try (BufferedReader br = new BufferedReader(new FileReader("course_info.txt"))) {
            String line;
            Course currentCourse = new Course();
            String section, subType, instructor, day;
            LocalTime time1, time2;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Instructor Name:")) {
                    instructor = line.split(":")[1].trim();
                    line = br.readLine().trim(); //next line
                    if (line.startsWith("Course Name:")) {
                        currentCourse = new Course();
                        currentCourse.courseCode = line.split(":")[1].trim();
                        currentCourse.courseName= line.split(":",3)[2].trim();
                        currentCourse.lectures = new LinkedList<>();
                        currentCourse.tutorials = new LinkedList<>();
                        for (Course course : globals.courses) {
                            if (Objects.equals(course.courseCode, currentCourse.courseCode)) {
                                currentCourse = course;
                                globals.courses.remove(course);
                                break;
                            }
                        }
                        globals.courses.add(currentCourse);
                        line = br.readLine().trim(); //next line
                        if (line.startsWith("Section:")) {
                            String[] sectionParts = line.split("\\|");
                            section = sectionParts[0].split(":")[1].trim();
                            subType = sectionParts[2].split(":")[1].trim();

                            line = br.readLine().trim();//next line
                            if (line.startsWith("Schedule:")) {
                                int cnt = 0;
                                while (!line.trim().equals("---------")) {
                                    cnt++;
                                    line = br.readLine().trim();//next line
                                    if (line.equals("No schedule") || line.equals("---------")) {
                                        break;
                                    }
                                    String[] parts = line.split(" ");

//                                    int c=0;
//                                    for (String s:parts){System.out.print(c+++" ");System.out.println(s);}
                                    String p0 = (parts[0].length() == 4 ? '0' + parts[0] : parts[0]);
                                    String p3 = (parts[3].length() == 4 ? '0' + parts[3] : parts[3]);

                                    time1 = LocalTime.parse(p0);
                                    time2 = LocalTime.parse(p3);
                                    if (parts[1].trim().equals("PM") && parts[0].length() == 4) {
                                        time1 = time1.plusHours(12);
                                    }
                                    if (parts[4].trim().equals("PM") && parts[3].length() == 4) {
                                        time2 = time2.plusHours(12);
                                    }
                                    line = br.readLine().trim();//next line
                                    day = line;

                                    //processing

                                    if (subType.trim().equals("Lecture")) {
                                        if (cnt == 1) {
                                            Lecture lecture = new Lecture();
                                            lecture.day = day;
                                            lecture.lectureNumber = section;
                                            lecture.courseCode = currentCourse.courseCode;
                                            lecture.courseName= currentCourse.courseName;
                                            lecture.instructor = instructor;
                                            lecture.startTime = time1;
                                            lecture.endTime = time2;
                                            currentCourse.lectures.add(lecture);
                                        } else if (cnt > 1) {
                                            Tutorial tutorial = new Tutorial();
                                            tutorial.tutorialNumber = section + ((char) ('Z' - cnt + 1));
                                            tutorial.courseCode= currentCourse.courseCode;
                                            tutorial.day = day;
                                            tutorial.startTime = time1;
                                            tutorial.endTime = time2;
                                            tutorial.instructor = instructor;
                                            currentCourse.tutorials.add(tutorial);
                                        }
                                    } else if(subType.trim().equals("Tutorial")){
                                        Tutorial tutorial = new Tutorial();
                                        tutorial.tutorialNumber = section;
                                        tutorial.courseCode= currentCourse.courseCode;
                                        tutorial.day = day;
                                        tutorial.startTime = time1;
                                        tutorial.endTime = time2;
                                        tutorial.instructor = instructor;
                                        currentCourse.tutorials.add(tutorial);
                                    }
                                    else{
                                        Lab lab=new Lab();
                                        lab.labNumber = section;
                                        lab.courseCode= currentCourse.courseCode;
                                        lab.day = day;
                                        lab.startTime = time1;
                                        lab.endTime = time2;
                                        lab.instructor = instructor;
                                        currentCourse.labs.add(lab);
                                    }
                                }
                            }
                        }
                    }

                }


            }
            globals.courses.removeIf(course -> course.lectures.isEmpty());
//            for (Course course : globals.courses) {
//                System.out.println("Course: " + course.courseCode);
//                System.out.println("Lectures:");
//                for (Lecture lecture : course.lectures) {
//                    System.out.println(lecture.lectureNumber + ": " + lecture.day + ", " +
//                            lecture.startTime + " - " + lecture.endTime + ", " + lecture.instructor);
//                }
//                System.out.println("Tutorials:");
//                for (Tutorial tutorial : course.tutorials) {
//                    System.out.println(tutorial.tutorialNumber + ": " + tutorial.day + ", " +
//                            tutorial.startTime + " - " + tutorial.endTime + ", " + tutorial.instructor);
//                }
//                System.out.println();
//            }

            launch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}