package com.example.scheds;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;



public class globals {
    public static LinkedList<Course> selectedCourses = new LinkedList<>();
    public static boolean no_two_hour_gap = false, not_only_1_any_day = false;
    public static LinkedList<Course> courses = new LinkedList<>();
    public static Course customCourse;

    public static void generateAllPossibleTimetables(LinkedList<Course> courses, LinkedList<Lecture> selectedLectures,
                                                     LinkedList<Tutorial> selectedTutorials,
                                                     LinkedList<Lab> selectedLabs,
                                                     LinkedList<LinkedList<Lecture>> allPossibleLectures,
                                                     LinkedList<LinkedList<Tutorial>> allPossibleTutorials,
                                                     LinkedList<LinkedList<Lab>> allPossibleLabs) {
        if (selectedLectures.size() == courses.size()) {
            if ((not_only_1_any_day && notSingleLectureOrTutorialDay(selectedLectures, selectedTutorials)) &&
                    (no_two_hour_gap && !hasTimeGapsGreaterThanMinutes(selectedLectures, selectedTutorials, 125))) {
                allPossibleLectures.add(new LinkedList<>(selectedLectures));
                allPossibleTutorials.add(new LinkedList<>(selectedTutorials));
                allPossibleLabs.add(new LinkedList<>(selectedLabs));
            } else if ((not_only_1_any_day && notSingleLectureOrTutorialDay(selectedLectures, selectedTutorials)) && !no_two_hour_gap) {
                allPossibleLectures.add(new LinkedList<>(selectedLectures));
                allPossibleTutorials.add(new LinkedList<>(selectedTutorials));
                allPossibleLabs.add(new LinkedList<>(selectedLabs));

            } else if (!not_only_1_any_day &&
                    (no_two_hour_gap && !hasTimeGapsGreaterThanMinutes(selectedLectures, selectedTutorials, 125))) {
                allPossibleLectures.add(new LinkedList<>(selectedLectures));
                allPossibleTutorials.add(new LinkedList<>(selectedTutorials));
                allPossibleLabs.add(new LinkedList<>(selectedLabs));

            } else if (!not_only_1_any_day && !no_two_hour_gap) {
                allPossibleLectures.add(new LinkedList<>(selectedLectures));
                allPossibleTutorials.add(new LinkedList<>(selectedTutorials));
                allPossibleLabs.add(new LinkedList<>(selectedLabs));

            }
            return;
        }

        Course currentCourse = courses.get(selectedLectures.size());
        LinkedList<Lecture> lectures = currentCourse.lectures;
        LinkedList<Tutorial> tutorials = currentCourse.tutorials;
        LinkedList<Lab> labs = currentCourse.labs;
        if (labs.isEmpty()) {
            Lab newLab=new Lab();
            for (Lecture lecture : lectures) {
                LinkedList<Tutorial> matchingTutorials = findAllMatchingTutorials(tutorials, lecture.lectureNumber);

                for (Tutorial tutorial : matchingTutorials) {
                    if (!hasTimeConflict(selectedLectures, selectedTutorials, selectedLabs, lecture, tutorial, newLab)) {
                        selectedLectures.addLast(lecture);
                        selectedTutorials.addLast(tutorial);

                        generateAllPossibleTimetables(courses, selectedLectures, selectedTutorials, selectedLabs,
                                allPossibleLectures, allPossibleTutorials, allPossibleLabs);

                        selectedLectures.removeLast();
                        selectedTutorials.removeLast();
                    }
                }
            }
        }
        else if(tutorials.isEmpty()){
            Tutorial newTutorial=new Tutorial();
            for (Lecture lecture : lectures) {
                LinkedList<Lab> matchingLabs = findAllMatchingLabs(labs, lecture.lectureNumber);

                for (Lab lab : matchingLabs) {
                    if (!hasTimeConflict(selectedLectures, selectedTutorials, selectedLabs, lecture, newTutorial, lab)) {
                        selectedLectures.addLast(lecture);
                        selectedLabs.addLast(lab);

                        generateAllPossibleTimetables(courses, selectedLectures, selectedTutorials, selectedLabs,
                                allPossibleLectures, allPossibleTutorials, allPossibleLabs);

                        selectedLectures.removeLast();
                        selectedLabs.removeLast();
                    }
                }
            }
        }else {
            for (Lecture lecture : lectures) {
                LinkedList<Tutorial> matchingTutorials = findAllMatchingTutorials(tutorials, lecture.lectureNumber);

                for (Tutorial tutorial : matchingTutorials) {
                    LinkedList<Lab> matchingLabs = findAllMatchingLabs(labs, lecture.lectureNumber);
                    for (Lab lab : matchingLabs) {
                        if (!hasTimeConflict(selectedLectures, selectedTutorials, selectedLabs, lecture, tutorial, lab)) {
                            selectedLectures.addLast(lecture);
                            selectedTutorials.addLast(tutorial);
                            selectedLabs.addLast(lab);

                            generateAllPossibleTimetables(courses, selectedLectures, selectedTutorials, selectedLabs,
                                    allPossibleLectures, allPossibleTutorials, allPossibleLabs);

                            selectedLectures.removeLast();
                            selectedTutorials.removeLast();
                            selectedLabs.removeLast();
                        }
                    }

                }
            }
        }
    }


    private static boolean notSingleLectureOrTutorialDay(LinkedList<Lecture> lectures, LinkedList<Tutorial> tutorials) {
        Map<String, Integer> lectureCountByDay = new HashMap<>();

        for (Lecture lecture : lectures) {
            String day = lecture.day;
            lectureCountByDay.put(day, lectureCountByDay.getOrDefault(day, 0) + 1);
        }
        for (Tutorial lecture : tutorials) {
            String day = lecture.day;
            lectureCountByDay.put(day, lectureCountByDay.getOrDefault(day, 0) + 1);
        }
        for (int count : lectureCountByDay.values()) {
            if (count == 1) {
                return false;
            }
        }

        return true;
    }

    private static LinkedList<Tutorial> findAllMatchingTutorials(LinkedList<Tutorial> tutorials, String lectureNumber) {
        LinkedList<Tutorial> matchingTutorials = new LinkedList<>();
        for (Tutorial tutorial : tutorials) {
            String tutorialNumber = tutorial.tutorialNumber.substring(0, 2);
            if (tutorialNumber.equals(lectureNumber)) {
                matchingTutorials.add(tutorial);
            }
        }
        return matchingTutorials;
    }

    private static LinkedList<Lab> findAllMatchingLabs(LinkedList<Lab> labs, String lectureNumber) {
        LinkedList<Lab> matchingLabs = new LinkedList<>();
        for (Lab lab : labs) {
            String labNumber = lab.labNumber.substring(0, 2);
            if (labNumber.equals(lectureNumber)) {
                matchingLabs.add(lab);
            }
        }
        return matchingLabs;
    }
//    private static boolean hasTimeConflict(LinkedList<Lecture> selectedLectures, LinkedList<Tutorial> selectedTutorials,LinkedList<Lab> selectedLabs, Lecture newLecture, Tutorial newTutorial) {
//        for (Lecture lecture : selectedLectures) {
//            if ((lecture.day.equals(newLecture.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newLecture.startTime, newLecture.endTime))
//                    ||(lecture.day.equals(newTutorial.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newTutorial.startTime, newTutorial.endTime))) {
//                return true;
//            }
//        }
//
//        for (Tutorial tutorial : selectedTutorials) {
//            if ((tutorial.day.equals(newLecture.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newLecture.startTime, newLecture.endTime))
//                    || (tutorial.day.equals(newTutorial.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newTutorial.startTime, newTutorial.endTime))){
//                return true;
//            }
//        }
//        for (Lab lab : selectedLabs) {
//            if ((lab.day.equals(newLecture.day) && hasTimeOverlap(lab.startTime, lab.endTime, newLecture.startTime, newLecture.endTime))
//                    || (lab.day.equals(newTutorial.day) && hasTimeOverlap(lab.startTime, lab.endTime, newTutorial.startTime, newTutorial.endTime))) {
//                return true;
//            }
//        }
//        return false; // No time conflict
//    }

//    private static boolean hasTimeConflict(LinkedList<Lecture> selectedLectures, LinkedList<Tutorial> selectedTutorials,
//                                           LinkedList<Lab> selectedLabs, Lecture newLecture, Lab newLab) {
//        for (Lecture lecture : selectedLectures) {
//            if ((lecture.day.equals(newLecture.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newLecture.startTime, newLecture.endTime))
//                    || (lecture.day.equals(newLab.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newLab.startTime, newLab.endTime))) {
//                return true;
//            }
//        }
//
//        for (Tutorial tutorial : selectedTutorials) {
//            if ((tutorial.day.equals(newLecture.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newLecture.startTime, newLecture.endTime))
//                    || (tutorial.day.equals(newLab.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newLab.startTime, newLab.endTime))) {
//                return true;
//            }
//        }
//
//        for (Lab lab : selectedLabs) {
//            if ((lab.day.equals(newLecture.day) && hasTimeOverlap(lab.startTime, lab.endTime, newLecture.startTime, newLecture.endTime))
//                    || (lab.day.equals(newLab.day) && hasTimeOverlap(lab.startTime, lab.endTime, newLab.startTime, newLab.endTime))) {
//                return true;
//            }
//        }
//
//        return false; // No time conflict
//    }

    private static boolean hasTimeConflict(LinkedList<Lecture> selectedLectures, LinkedList<Tutorial> selectedTutorials,
                                           LinkedList<Lab> selectedLabs, Lecture newLecture, Tutorial newTutorial, Lab newLab) {
        for (Lecture lecture : selectedLectures) {
            if ((lecture.day.equals(newLecture.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newLecture.startTime, newLecture.endTime))
                    || (lecture.day.equals(newTutorial.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newTutorial.startTime, newTutorial.endTime))
                    || (lecture.day.equals(newLab.day) && hasTimeOverlap(lecture.startTime, lecture.endTime, newLab.startTime, newLab.endTime))) {
                return true;
            }
        }

        for (Tutorial tutorial : selectedTutorials) {
            if ((tutorial.day.equals(newLecture.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newLecture.startTime, newLecture.endTime))
                    || (tutorial.day.equals(newTutorial.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newTutorial.startTime, newTutorial.endTime))
                    || (tutorial.day.equals(newLab.day) && hasTimeOverlap(tutorial.startTime, tutorial.endTime, newLab.startTime, newLab.endTime))) {
                return true;
            }
        }

        for (Lab lab : selectedLabs) {
            if ((lab.day.equals(newLecture.day) && hasTimeOverlap(lab.startTime, lab.endTime, newLecture.startTime, newLecture.endTime))
                    || (lab.day.equals(newTutorial.day) && hasTimeOverlap(lab.startTime, lab.endTime, newTutorial.startTime, newTutorial.endTime))
                    || (lab.day.equals(newLab.day) && hasTimeOverlap(lab.startTime, lab.endTime, newLab.startTime, newLab.endTime))) {
                return true;
            }
        }

        return false; // No time conflict
    }

    private static boolean hasTimeOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return !(endTime1.isBefore(startTime2) || endTime2.isBefore(startTime1));
    }

    public static boolean isSelectedCourse(Course course) {
        for (Course course1 : selectedCourses) {
            if (course1 == course) {
                return true;
            }
        }
        return false;
    }

    private static int calculateTimeGap(LocalTime startTime, LocalTime endTime) {
        return (int) ChronoUnit.MINUTES.between(startTime, endTime);
    }

    public static boolean hasTimeGapsGreaterThanMinutes(LinkedList<Lecture> lectures, LinkedList<Tutorial> tutorials, int minutes) {

        Map<String, LinkedList<Pair<LocalTime, LocalTime>>> lecturesByDay = new HashMap<>();

        // Group lectures by day
        for (Lecture lecture : lectures) {
            String day = lecture.day;
            lecturesByDay.putIfAbsent(day, new LinkedList<>());
            lecturesByDay.get(day).add(new Pair<>(lecture.startTime, lecture.endTime));
        }
        for (Tutorial lecture : tutorials) {
            String day = lecture.day;
            lecturesByDay.putIfAbsent(day, new LinkedList<>());
            lecturesByDay.get(day).add(new Pair<>(lecture.startTime, lecture.endTime));
        }

        // Check each day for gaps between lectures
        for (LinkedList<Pair<LocalTime, LocalTime>> dayLectures : lecturesByDay.values()) {
            // Sort lectures by start time
            dayLectures.sort(Comparator.comparing(Pair::getKey));

            int numLectures = dayLectures.size();
            if (numLectures == 0) {
                continue;
            }

            for (int i = 0; i < numLectures - 1; i++) {
                LocalTime lt2 = dayLectures.get(i).getValue();
                LocalTime ltt1 = dayLectures.get(i + 1).getKey();


                // Calculate the time gap between currentLecture and nextLecture
                int timeGapMinutes = calculateTimeGap(lt2, ltt1);

                if (timeGapMinutes > minutes) {
                    return true; // Time gap greater than the specified minutes found
                }
            }
        }

        // No time gaps greater than the specified minutes found
        return false;
    }

    public static void showErrorAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showConfirmationAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmed");
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static void openNewForm(String formName, String title) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(globals.class.getResource(formName)));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void makeList(LinkedList linkedList, ListView<String> list) {
        String[] strings = new String[linkedList.size()];
        int i = 0;
        for (Object ob : linkedList) {
            strings[i] = ob.toString();
            i++;
        }
        ObservableList<String> obs = FXCollections.observableArrayList(strings);
        list.setItems(obs);
    }

    public static ObservableList<String> makeObsList(LinkedList linkedList) {
        String[] strings = new String[linkedList.size()];
        int i = 0;
        for (Object ob : linkedList) {
            strings[i] = ob.toString();
            i++;
        }
        return FXCollections.observableArrayList(strings);
    }


}