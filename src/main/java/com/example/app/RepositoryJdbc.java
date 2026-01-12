package com.example.app;

import com.example.app.model.Course;
import com.example.app.model.Enrollment;
import com.example.app.model.Professor;
import com.example.app.model.Student;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RepositoryJdbc implements RepositoryInterface {

    private final String url;
    private final String user;
    private final String password;

    public RepositoryJdbc() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new IOException("Fichier db.properties non trouvé dans resources");
            }
            props.load(is);
        }

        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        // Test de connexion
        try (Connection conn = getConnection()) {
            System.out.println("Connexion PostgreSQL établie : " + conn.getMetaData().getDatabaseProductName());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public synchronized Student createStudent(String first, String last, String email) {
        // Vérification doublons par email
        if (email != null && !email.trim().isEmpty()) {
            String em = email.trim().toLowerCase();
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM students WHERE LOWER(email) = ?")) {
                ps.setString(1, em);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("Un étudiant avec cet email existe déjà.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la vérification d'email", e);
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO students (first_name, last_name, email) VALUES (?, ?, ?) RETURNING id")) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new StudentWithId(id, first, last, email);
            }
            throw new RuntimeException("Échec création étudiant");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création étudiant", e);
        }
    }

    @Override
    public synchronized Course createCourse(String code, String name) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code requis");
        }
        String ccode = code.trim().toUpperCase();

        // Vérification doublons
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM courses WHERE UPPER(code) = ?")) {
            ps.setString(1, ccode);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException("Un cours avec ce code existe déjà.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de code", e);
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO courses (code, name) VALUES (?, ?) RETURNING id")) {
            ps.setString(1, ccode);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new CourseWithId(id, ccode, name);
            }
            throw new RuntimeException("Échec création cours");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création cours", e);
        }
    }

    @Override
    public synchronized Enrollment createEnrollment(int studentId, int courseId) {
        if (hasEnrollment(studentId, courseId)) {
            throw new IllegalArgumentException("Inscription déjà existante");
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?) RETURNING id")) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new EnrollmentWithId(id, studentId, courseId);
            }
            throw new RuntimeException("Échec création inscription");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création inscription", e);
        }
    }

    @Override
    public synchronized Professor createProfessor(String first, String last, String email) {
        // Vérification doublons par email
        if (email != null && !email.trim().isEmpty()) {
            String em = email.trim().toLowerCase();
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM professors WHERE LOWER(email) = ?")) {
                ps.setString(1, em);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("Un professeur avec cet email existe déjà.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la vérification d'email", e);
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO professors (first_name, last_name, email) VALUES (?, ?, ?) RETURNING id")) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new ProfessorWithId(id, first, last, email);
            }
            throw new RuntimeException("Échec création professeur");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création professeur", e);
        }
    }

    @Override
    public synchronized boolean assignGrade(int enrollmentId, double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Note hors bornes (0-100)");
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE enrollments SET grades = array_append(grades, ?) WHERE id = ?")) {
            ps.setDouble(1, grade);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ajout note", e);
        }
    }

    @Override
    public synchronized boolean hasEnrollment(int studentId, int courseId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?")) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur vérification inscription", e);
        }
    }

    @Override
    public synchronized boolean deleteEnrollment(int enrollmentId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE id = ?")) {
            ps.setInt(1, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression inscription", e);
        }
    }

    @Override
    public synchronized boolean deleteProfessor(int professorId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Détacher le professeur des cours
                try (PreparedStatement ps = conn.prepareStatement("UPDATE courses SET professor_id = NULL WHERE professor_id = ?")) {
                    ps.setInt(1, professorId);
                    ps.executeUpdate();
                }

                // Supprimer le professeur
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM professors WHERE id = ?")) {
                    ps.setInt(1, professorId);
                    int deleted = ps.executeUpdate();
                    conn.commit();
                    return deleted > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression professeur", e);
        }
    }

    @Override
    public synchronized Optional<Student> findStudentById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new StudentWithId(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche étudiant", e);
        }
    }

    @Override
    public synchronized Optional<Course> findCourseById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM courses WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CourseWithId course = new CourseWithId(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name")
                );
                Object profId = rs.getObject("professor_id");
                if (profId != null) {
                    course.setProfessorId((Integer) profId);
                }
                return Optional.of(course);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche cours", e);
        }
    }

    @Override
    public synchronized Optional<Professor> findProfessorById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM professors WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new ProfessorWithId(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche professeur", e);
        }
    }

    @Override
    public synchronized List<Student> listStudents() {
        List<Student> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new StudentWithId(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste étudiants", e);
        }
        return result;
    }

    @Override
    public synchronized List<Course> listCourses() {
        List<Course> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM courses ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CourseWithId course = new CourseWithId(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name")
                );
                Object profId = rs.getObject("professor_id");
                if (profId != null) {
                    course.setProfessorId((Integer) profId);
                }
                result.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste cours", e);
        }
        return result;
    }

    @Override
    public synchronized List<Enrollment> listEnrollments() {
        List<Enrollment> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM enrollments ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EnrollmentWithId enrollment = new EnrollmentWithId(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id")
                );

                // Charger les notes depuis le tableau PostgreSQL
                Array gradesArray = rs.getArray("grades");
                if (gradesArray != null) {
                    Object[] grades = (Object[]) gradesArray.getArray();
                    for (Object grade : grades) {
                        if (grade != null) {
                            // PostgreSQL renvoie BigDecimal, on le convertit en Double
                            if (grade instanceof java.math.BigDecimal) {
                                enrollment.addGrade(((java.math.BigDecimal) grade).doubleValue());
                            } else if (grade instanceof Double) {
                                enrollment.addGrade((Double) grade);
                            } else if (grade instanceof Number) {
                                enrollment.addGrade(((Number) grade).doubleValue());
                            }
                        }
                    }
                }

                result.add(enrollment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste inscriptions", e);
        }
        return result;
    }

    @Override
    public synchronized List<Professor> listProfessors() {
        List<Professor> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM professors ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new ProfessorWithId(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste professeurs", e);
        }
        return result;
    }

    @Override
    public void save() throws IOException {
        // Pour JDBC, les données sont automatiquement sauvegardées à chaque opération
        System.out.println("Données PostgreSQL automatiquement persistées.");
    }

    // Classes internes pour créer des objets avec ID spécifique (sans utiliser AtomicInteger)
    private static class StudentWithId extends Student {
        public StudentWithId(int id, String firstName, String lastName, String email) {
            super(firstName, lastName, email);
            // Utiliser la réflexion pour définir l'ID
            try {
                var field = Student.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(this, id);
            } catch (Exception e) {
                throw new RuntimeException("Erreur définition ID étudiant", e);
            }
        }
    }

    private static class CourseWithId extends Course {
        public CourseWithId(int id, String code, String name) {
            super(code, name);
            try {
                var field = Course.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(this, id);
            } catch (Exception e) {
                throw new RuntimeException("Erreur définition ID cours", e);
            }
        }
    }

    private static class EnrollmentWithId extends Enrollment {
        public EnrollmentWithId(int id, int studentId, int courseId) {
            super(studentId, courseId);
            try {
                var field = Enrollment.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(this, id);
            } catch (Exception e) {
                throw new RuntimeException("Erreur définition ID inscription", e);
            }
        }
    }

    private static class ProfessorWithId extends Professor {
        public ProfessorWithId(int id, String firstName, String lastName, String email) {
            super(firstName, lastName, email);
            try {
                var field = Professor.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(this, id);
            } catch (Exception e) {
                throw new RuntimeException("Erreur définition ID professeur", e);
            }
        }
    }
}
