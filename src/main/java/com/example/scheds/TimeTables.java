package com.example.scheds;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static com.example.scheds.globals.selectedCourses;

public class TimeTables implements Initializable {
    public AnchorPane root;
    public TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LinkedList<LinkedList<Lecture>> allPossibleLectures = new LinkedList<>();
        LinkedList<LinkedList<Tutorial>> allPossibleTutorials = new LinkedList<>();
        LinkedList<LinkedList<Lab>> allPossibleLabs = new LinkedList<>();


        tabPane.getTabs().clear();
        globals.generateAllPossibleTimetables(selectedCourses, new LinkedList<>(), new LinkedList<>(),new LinkedList<>(), allPossibleLectures, allPossibleTutorials,allPossibleLabs);

        for (int i = 0; i < allPossibleLectures.size(); i++) {
            Tab tab = new Tab("Timetable " + (i + 1));
            TableView<Slot> tableView = createTableView(allPossibleLectures.get(i), allPossibleTutorials.get(i),allPossibleLabs.get(i));
            tab.setContent(tableView);
            tabPane.getTabs().add(tab);
        }

    }

    private TableView<Slot> createTableView(LinkedList<Lecture> lectures, LinkedList<Tutorial> tutorials,LinkedList<Lab>labs) {
        TableView<Slot> tableView = new TableView<>();
        tableView.setPadding(new Insets(10));

        TableColumn<Slot, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());

        ObservableList<TableColumn<Slot, ?>> columns = tableView.getColumns();
        columns.add(timeColumn);

        for (String day : new String[]{"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"}) {
            TableColumn<Slot, String> dayColumn = new TableColumn<>(day);
            dayColumn.setCellValueFactory(cellData -> cellData.getValue().getDayProperty(day));

            columns.add(dayColumn);
        }

        for (int i = 0; i < 7; i++) {
            Slot slot = new Slot();
            slot.setTime(getTimeString(i));

            tableView.getItems().add(slot);
        }

        for (Lecture lecture : lectures) {
            Slot slot = tableView.getItems().get(getSlotIndex(lecture.startTime));
            slot.setDayValue(lecture.day, "Lecture: " + lecture.courseCode + ", "+lecture.lectureNumber + ", " + lecture.instructor.split(" ")[0].trim());
        }

        for (Tutorial tutorial : tutorials) {
            Slot slot = tableView.getItems().get(getSlotIndex(tutorial.startTime));
            slot.setDayValue(tutorial.day, "Tutorial: " + tutorial.tutorialNumber +", "+ tutorial.courseCode+ ", " + tutorial.instructor.split(" ")[0].trim());
        }
        for (Lab tutorial : labs) {
            Slot slot = tableView.getItems().get(getSlotIndex(tutorial.startTime));
            slot.setDayValue(tutorial.day, "Lab: " + tutorial.labNumber +", "+ tutorial.courseCode+ ", " + tutorial.instructor.split(" ")[0].trim());
        }

        return tableView;
    }

    private int getSlotIndex(LocalTime time) {
        int startHour = 8;
        int startMinute = 30;
        int slotMinutes = 119;

        int hourDiff = time.getHour() - startHour;
        int minuteDiff = time.getMinute() - startMinute;

        return (hourDiff * 60 + minuteDiff) / slotMinutes;
    }

    private String getTimeString(int slotIndex) {
        int startHour = 8;
        int startMinute = 30;
        int slotMinutes = 120;
        int totalMinutes = slotIndex * slotMinutes;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return String.format("%02d:%02d - %02d:%02d", (startHour + hours)%24, (startMinute + minutes)%60,
                startHour + hours + 2 , (startMinute + minutes+59)%60 );
    }
}
