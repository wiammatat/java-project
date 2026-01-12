package com.example.app;

import com.example.app.model.Course;
import com.example.app.model.Enrollment;
import com.example.app.model.Professor;
import com.example.app.model.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Repository implements Serializable, RepositoryInterface {
    private static final long serialVersionUID = 1L;
    private final List<Student> students = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final List<Professor> professors = new ArrayList<>();

    private static final String DATA_FILE = "data.ser";
    private static final String BACKUP_FILE = "data.ser.bak";

    public synchronized Student createStudent(String first, String last, String email) {
        // basic duplicate check by email (if provided)
        if (email != null && !email.trim().isEmpty()) {
            String em = email.trim().toLowerCase();
            boolean dup = students.stream().anyMatch(s -> s.getEmail()!=null && s.getEmail().trim().toLowerCase().equals(em));
            if (dup) throw new IllegalArgumentException("Un étudiant avec cet email existe déjà.");
        }
        Student s = new Student(first, last, email);
        students.add(s);
        return s;
    }

    public synchronized Course createCourse(String code, String name) {
        if (code == null || code.trim().isEmpty()) throw new IllegalArgumentException("Code requis");
        String ccode = code.trim().toUpperCase();
        boolean dup = courses.stream().anyMatch(c -> c.getCode()!=null && c.getCode().trim().toUpperCase().equals(ccode));
        if (dup) throw new IllegalArgumentException("Un cours avec ce code existe déjà.");
        Course c = new Course(ccode, name);
        courses.add(c);
        return c;
    }

    public synchronized Enrollment createEnrollment(int studentId, int courseId) {
        if (hasEnrollment(studentId, courseId)) throw new IllegalArgumentException("Inscription déjà existante");
        Enrollment e = new Enrollment(studentId, courseId);
        enrollments.add(e);
        return e;
    }

    public synchronized Professor createProfessor(String first, String last, String email) {
        // duplicate check by email
        if (email != null && !email.trim().isEmpty()) {
            String em = email.trim().toLowerCase();
            boolean dup = professors.stream().anyMatch(p -> p.getEmail()!=null && p.getEmail().trim().toLowerCase().equals(em));
            if (dup) throw new IllegalArgumentException("Un professeur avec cet email existe déjà.");
        }
        Professor p = new Professor(first, last, email);
        professors.add(p);
        return p;
    }

    public synchronized boolean assignGrade(int enrollmentId, double grade) {
        if (grade < 0 || grade > 100) throw new IllegalArgumentException("Note hors bornes (0-100)");
        for (Enrollment e : enrollments) {
            if (e.getId() == enrollmentId) {
                e.addGrade(grade);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasEnrollment(int studentId, int courseId) {
        return enrollments.stream().anyMatch(e -> e.getStudentId() == studentId && e.getCourseId() == courseId);
    }

    public synchronized boolean deleteEnrollment(int enrollmentId) {
        return enrollments.removeIf(e -> e.getId() == enrollmentId);
    }

    public synchronized boolean deleteProfessor(int professorId) {
        // optionally unassign courses taught by this professor
        courses.forEach(c -> { if (c.getProfessorId()!=null && c.getProfessorId()==professorId) c.setProfessorId(null); });
        return professors.removeIf(p -> p.getId() == professorId);
    }

    public synchronized Optional<Student> findStudentById(int id) {
        return students.stream().filter(s -> s.getId() == id).findFirst();
    }

    public synchronized Optional<Course> findCourseById(int id) {
        return courses.stream().filter(c -> c.getId() == id).findFirst();
    }

    public synchronized Optional<Professor> findProfessorById(int id) {
        return professors.stream().filter(p -> p.getId() == id).findFirst();
    }

    public synchronized List<Student> listStudents() { return new ArrayList<>(students); }
    public synchronized List<Course> listCourses() { return new ArrayList<>(courses); }
    public synchronized List<Enrollment> listEnrollments() { return new ArrayList<>(enrollments); }
    public synchronized List<Professor> listProfessors() { return new ArrayList<>(professors); }

    public synchronized void save() throws IOException {
        // write atomic: write to temp file then move
        Path data = Path.of(DATA_FILE);
        Path tmp = Path.of(DATA_FILE + ".tmp");
        // backup current
        try {
            if (Files.exists(data)) Files.copy(data, Path.of(BACKUP_FILE), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.err.println("Impossible de créer la sauvegarde : " + ex.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(tmp))) {
            oos.writeObject(this);
            oos.flush();
        }
        // atomic move
        Files.move(tmp, data, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public static Repository load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return new Repository();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof Repository) {
                Repository r = (Repository) obj;
                // create a fresh repository and copy lists if present (backward compatibility)
                Repository res = new Repository();
                if (r.students != null) res.students.addAll(r.students);
                if (r.courses != null) res.courses.addAll(r.courses);
                if (r.enrollments != null) res.enrollments.addAll(r.enrollments);
                if (r.professors != null) res.professors.addAll(r.professors);

                int maxS = (r.students!=null? r.students.stream().mapToInt(Student::getId).max().orElse(0) : 0);
                int maxC = (r.courses!=null? r.courses.stream().mapToInt(Course::getId).max().orElse(0) : 0);
                int maxE = (r.enrollments!=null? r.enrollments.stream().mapToInt(Enrollment::getId).max().orElse(0) : 0);
                int maxP = (r.professors!=null? r.professors.stream().mapToInt(Professor::getId).max().orElse(0) : 0);
                com.example.app.model.Student.setNextId(maxS + 1);
                com.example.app.model.Course.setNextId(maxC + 1);
                com.example.app.model.Enrollment.setNextId(maxE + 1);
                com.example.app.model.Professor.setNextId(maxP + 1);
                return res;
            }
        } catch (Exception ex) {
            System.err.println("Échec chargement : " + ex.getMessage());
        }
        return new Repository();
    }
}
