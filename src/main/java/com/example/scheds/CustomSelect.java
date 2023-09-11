package com.example.scheds;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.*;

import static com.example.scheds.globals.customCourse;

public class CustomSelect implements Initializable {

    public RadioButton TARB ;
    public AnchorPane TAAP ;
    public Label courseLBL;
    public RadioButton lecRB;
    public RadioButton profRB;
    public AnchorPane lecAP;
    public ListView<String> lecList;
    public Button lecConfirmBTN;
    public ListView<String> profList;
    public TextField profSearchTF;
    public Button profConfirmBTN;
    public AnchorPane profAP;
    public ListView<String> TAList;
    public TextField TASearchTF;
    public Button TAConfirmBTN;
    public RadioButton LabRB;
    public ListView<String> LabList;
    public TextField LabSearchTF;
    public Button LabConfirmBTN;
    public AnchorPane LabAP;
    LinkedList<String>TAl,profL,lecL=new LinkedList<>(),labTAl=new LinkedList<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lecRB.setSelected(true);
        ToggleGroup toggleGroup=new ToggleGroup();
        TARB.setToggleGroup(toggleGroup);
        LabRB.setToggleGroup(toggleGroup);
        profRB.setToggleGroup(toggleGroup);
        lecRB.setToggleGroup(toggleGroup);
        TAAP.setDisable(true);
        LabAP.setDisable(true);
        profAP.setDisable(true);
        TARB.setOnAction(e -> {
            TAAP.setDisable(!TARB.isSelected());
            lecAP.setDisable(!lecRB.isSelected());
            profAP.setDisable(!profRB.isSelected());
            LabAP.setDisable(!LabRB.isSelected());
        });
        lecRB.setOnAction(e -> {
            TAAP.setDisable(!TARB.isSelected());
            lecAP.setDisable(!lecRB.isSelected());
            profAP.setDisable(!profRB.isSelected());
            LabAP.setDisable(!LabRB.isSelected());

        });
        profRB.setOnAction(e -> {
            TAAP.setDisable(!TARB.isSelected());
            lecAP.setDisable(!lecRB.isSelected());
            profAP.setDisable(!profRB.isSelected());
            LabAP.setDisable(!LabRB.isSelected());

        });
        LabRB.setOnAction(e -> {
            TAAP.setDisable(!TARB.isSelected());
            lecAP.setDisable(!lecRB.isSelected());
            profAP.setDisable(!profRB.isSelected());
            LabAP.setDisable(!LabRB.isSelected());
        });

        courseLBL.setText(customCourse.courseCode);
        Set<String>TAs=new HashSet<>(),profs=new HashSet<>(),labTAs=new HashSet<>();
        for (Lecture lecture: customCourse.lectures){
            profs.add(lecture.instructor);
        }
        for (Tutorial lecture: customCourse.tutorials){
            TAs.add(lecture.instructor);
        }
        for (Lab lab:customCourse.labs){
            labTAs.add(lab.instructor);
        }
        TAl=new LinkedList<>(TAs);
        profL=new LinkedList<>(profs);
        labTAl=new LinkedList<>(labTAs);
        for (Lecture lecture: customCourse.lectures){
            lecL.add(String.valueOf(lecture.lectureNumber));
        }
        globals.makeList(TAl,TAList);
        globals.makeList(profL,profList);
        globals.makeList(labTAl,LabList);
        globals.makeList(lecL,lecList);
        ObservableList<String> tata = globals.makeObsList(TAl);
        TASearchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            String filter = newValue.toLowerCase();
            TAList.setItems(tata.filtered(course -> course.toLowerCase().contains(filter)));
        });
        ObservableList<String> profprof = globals.makeObsList(profL);
        profSearchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            String filter = newValue.toLowerCase();
            profList.setItems(profprof.filtered(course -> course.toLowerCase().contains(filter)));
        });
        ObservableList<String> lablab = globals.makeObsList(labTAl);
        LabSearchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            String filter = newValue.toLowerCase();
            LabList.setItems(lablab.filtered(course -> course.toLowerCase().contains(filter)));
        });

    }

    public void confirmLec(){
        Lecture lecture=customCourse.lectures.get(lecList.getSelectionModel().getSelectedIndex());
        LinkedList<Tutorial>tuts=new LinkedList<>();
        for (Tutorial tutorial:customCourse.tutorials){
            if (tutorial.tutorialNumber.startsWith(String.valueOf(lecture.lectureNumber))){
                tuts.add(tutorial);
            }
        }
        Course course=customCourse;
        course.lectures=new LinkedList<>();
        course.lectures.add(lecture);
        course.tutorials=tuts;
        course.courseCode=course.courseCode+" (lec "+lecList.getSelectionModel().getSelectedItem()+')';
        globals.selectedCourses.add(course);
        globals.showConfirmationAlert("Custom course added");

    }
    public void confirmProf(){
        LinkedList<Lecture>lecs=new LinkedList<>();
        LinkedList<Tutorial>tuts=new LinkedList<>();
        String profName=profList.getSelectionModel().getSelectedItem();
        for (Lecture lecture: customCourse.lectures){
            if (Objects.equals(lecture.instructor, profName)){
                lecs.add(lecture);
                for (Tutorial tutorial:customCourse.tutorials){
                    if (tutorial.tutorialNumber.startsWith(String.valueOf(lecture.lectureNumber))){
                        tuts.add(tutorial);
                    }
                }
            }
        }
        Course course=new Course();
        course.lectures=lecs;
        course.tutorials=tuts;
        course.courseCode=customCourse.courseCode+" (prof "+profList.getSelectionModel().getSelectedItem()+')';
        globals.selectedCourses.add(course);
        globals.showConfirmationAlert("Custom course added");

    }
    public void confirmTA(){
        String profName=TAList.getSelectionModel().getSelectedItem();
        LinkedList<Tutorial>tuts=new LinkedList<>();
        LinkedList<Lecture>lecs=new LinkedList<>();
        for (Tutorial tutorial: customCourse.tutorials){
            if (Objects.equals(tutorial.instructor, profName)){
                tuts.add(tutorial);
                for (Lecture lecture:customCourse.lectures){
                    if (tutorial.tutorialNumber.startsWith(String.valueOf(lecture.lectureNumber))){
                        lecs.add(lecture);
                    }
                }
            }
        }
        Course course=new Course();
        course.courseCode=customCourse.courseCode+" (TA "+TAList.getSelectionModel().getSelectedItem()+')';
        course.lectures=lecs;
        course.tutorials=tuts;
        globals.selectedCourses.add(course);
        globals.showConfirmationAlert("Custom course added");

    }
    public void confirmLab(){
        String profName=LabList.getSelectionModel().getSelectedItem();
        LinkedList<Lab>tuts=new LinkedList<>();
        LinkedList<Lecture>lecs=new LinkedList<>();
        for (Lab tutorial: customCourse.labs){
            if (Objects.equals(tutorial.instructor, profName)){
                tuts.add(tutorial);
                for (Lecture lecture:customCourse.lectures){
                    if (tutorial.labNumber.startsWith(String.valueOf(lecture.lectureNumber))){
                        lecs.add(lecture);
                    }
                }
            }
        }
        Course course=new Course();
        course.courseCode=customCourse.courseCode+" (Lab TA "+LabList.getSelectionModel().getSelectedItem()+')';
        course.courseName= customCourse.courseName;
        course.lectures=lecs;
        course.labs=tuts;
        globals.selectedCourses.add(course);
        globals.showConfirmationAlert("Custom course added");

    }
}
