package com.example.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    private final int id;
    private final int studentId;
    private final int courseId;
    private final List<Double> grades = new ArrayList<>();

    public Enrollment(int studentId, int courseId) {
        this.id = ID_GEN.getAndIncrement();
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public List<Double> getGrades() { return grades; }
    public void addGrade(double g) { grades.add(g); }

    @Override
    public String toString() {
        return "Enrollment{" + id + ", s=" + studentId + ", c=" + courseId + ", grades=" + grades + '}';
    }

    public static void setNextId(int next) {
        if (next < 1) next = 1;
        ID_GEN.set(next);
    }
}
