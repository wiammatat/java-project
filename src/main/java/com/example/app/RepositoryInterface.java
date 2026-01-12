package com.example.app;

import com.example.app.model.Course;
import com.example.app.model.Enrollment;
import com.example.app.model.Professor;
import com.example.app.model.Student;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface RepositoryInterface {
    Student createStudent(String first, String last, String email);
    Course createCourse(String code, String name);
    Enrollment createEnrollment(int studentId, int courseId);
    Professor createProfessor(String first, String last, String email);

    boolean assignGrade(int enrollmentId, double grade);
    boolean hasEnrollment(int studentId, int courseId);
    boolean deleteEnrollment(int enrollmentId);
    boolean deleteProfessor(int professorId);

    Optional<Student> findStudentById(int id);
    Optional<Course> findCourseById(int id);
    Optional<Professor> findProfessorById(int id);

    List<Student> listStudents();
    List<Course> listCourses();
    List<Enrollment> listEnrollments();
    List<Professor> listProfessors();

    void save() throws IOException;
}
