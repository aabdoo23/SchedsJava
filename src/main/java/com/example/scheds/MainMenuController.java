package com.example.scheds;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.example.scheds.globals.selectedCourses;

public class MainMenuController implements Initializable {
    public TextField searchBarTF;
    public ListView<String> coursesList;
    public Button selectCourseBTN;
    public ListView<String> selectedCoursesList;
    public Button generateBTN;
    public Button clearBTN;
    public AnchorPane root;
    public Button removeSelectedBTN;
    public Button customSelectBTN;
    public CheckBox less1CB;
    public CheckBox gap2CB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedCourses=new LinkedList<>();
        globals.makeList(globals.courses,coursesList);
        ObservableList<String> courses = globals.makeObsList(globals.courses);
        searchBarTF.textProperty().addListener((observable, oldValue, newValue) -> {
            String filter = newValue.toLowerCase();
            coursesList.setItems(courses.filtered(course -> course.toLowerCase().contains(filter)));
        });
    }
    public void clickCB1(){
        globals.not_only_1_any_day=less1CB.isSelected();
    }
    public void clickCB2(){
        globals.no_two_hour_gap=gap2CB.isSelected();
    }
    public void selectCourse(){
        for (Course course: globals.courses){
            if(Objects.equals(course.toString(),coursesList.getSelectionModel().getSelectedItem())){
                if (!globals.isSelectedCourse(course)) selectedCourses.add(course);
                else globals.showErrorAlert("Course selected before");
            }
        }
        globals.makeList(selectedCourses,selectedCoursesList);
    }

    public void clearSelected(){
        selectedCourses.clear();
        globals.makeList(selectedCourses,selectedCoursesList);

    }
    public void customSelect() throws IOException {
        if(coursesList.getSelectionModel().getSelectedItem()==null){
            globals.showErrorAlert("Please pick a course");
            return;
        }
        for (Course course:globals.courses){
            if (Objects.equals(course.toString(), coursesList.getSelectionModel().getSelectedItem())){
                globals.customCourse=course;
                break;
            }
        }
        globals.openNewForm("customSelect.fxml",globals.customCourse.courseCode+" custom selection");

        globals.makeList(selectedCourses,selectedCoursesList);

    }
    public void remove(){
        for (Course course:selectedCourses) {
            if (Objects.equals(course.toString(), selectedCoursesList.getSelectionModel().getSelectedItem())){
                selectedCourses.remove(course);
                globals.makeList(selectedCourses,selectedCoursesList);
                break;
            }
        }
    }
    public void generate() throws IOException {
        globals.openNewForm("timeTables.fxml","Time tables");

    }
}
