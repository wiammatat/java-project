package com.example.app;

import com.example.app.model.Course;
import com.example.app.model.Enrollment;
import com.example.app.model.Professor;
import com.example.app.model.Student;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFX extends Application {

    private final RepositoryInterface repo = RepositoryFactory.createRepository();
    private final ObservableList<Student> students = FXCollections.observableArrayList(repo.listStudents());
    private final ObservableList<Course> courses = FXCollections.observableArrayList(repo.listCourses());
    private final ObservableList<Enrollment> enrollments = FXCollections.observableArrayList(repo.listEnrollments());
    private final ObservableList<Professor> professors = FXCollections.observableArrayList(repo.listProfessors());

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        Tab t1 = new Tab("Étudiants", studentsPane()); t1.setClosable(false);
        Tab t2 = new Tab("Cours", coursesPane()); t2.setClosable(false);
        Tab t3 = new Tab("Inscriptions", enrollmentsPane()); t3.setClosable(false);
        Tab t4 = new Tab("Professeurs", professorsPane()); t4.setClosable(false);
        tabs.getTabs().addAll(t1, t2, t3, t4);

        MenuBar mb = new MenuBar();
        Menu mFile = new Menu("Fichier");
        MenuItem miSave = new MenuItem("Sauvegarder"); miSave.setOnAction(e -> doSave());
        MenuItem miLoad = new MenuItem("Charger"); miLoad.setOnAction(e -> doLoad());
        MenuItem miExit = new MenuItem("Quitter"); miExit.setOnAction(e -> { doSave(); stage.close(); });
        mFile.getItems().addAll(miSave, miLoad, new SeparatorMenuItem(), miExit);
        mb.getMenus().add(mFile);

        // petite aide pour le flux
        Label hint = new Label("Flux attendu: 1) Ajouter étudiant -> 2) Ajouter cours -> 3) Inscrire étudiant au cours");
        hint.setPadding(new Insets(6));

        VBox root = new VBox(mb, hint, tabs);
        Scene scene = new Scene(root, 900, 600);
        String storageType = (repo instanceof RepositoryJdbc) ? "PostgreSQL" : "Local";
        stage.setTitle("Système gestion école - " + storageType);
        stage.setScene(scene);
        stage.show();
    }

    private VBox studentsPane() {
        TableView<Student> table = new TableView<>(students);
        TableColumn<Student,Integer> cId = new TableColumn<>("ID"); cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Student,String> cFirst = new TableColumn<>("Prénom"); cFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Student,String> cLast = new TableColumn<>("Nom"); cLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Student,String> cEmail = new TableColumn<>("Email"); cEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        table.getColumns().addAll(cId,cFirst,cLast,cEmail);
        table.setPrefHeight(420);

        TextField tfFirst = new TextField(); tfFirst.setPromptText("Prénom");
        TextField tfLast = new TextField(); tfLast.setPromptText("Nom");
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");
        Button btnAdd = new Button("Ajouter");
        btnAdd.setOnAction(e -> {
            if (tfFirst.getText().isEmpty() || tfLast.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Prénom et nom sont requis.");
                return;
            }
            try {
                Student s = repo.createStudent(tfFirst.getText(), tfLast.getText(), tfEmail.getText());
                students.add(s);
                tfFirst.clear(); tfLast.clear(); tfEmail.clear();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur création étudiant", ex.getMessage());
            }
        });

        HBox form = new HBox(8, tfFirst, tfLast, tfEmail, btnAdd);
        form.setPadding(new Insets(8));
        VBox v = new VBox(8, table, form);
        v.setPadding(new Insets(8));
        return v;
    }

    private VBox coursesPane() {
        TableView<Course> table = new TableView<>(courses);
        TableColumn<Course,Integer> cId = new TableColumn<>("ID"); cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Course,String> cCode = new TableColumn<>("Code"); cCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        TableColumn<Course,String> cName = new TableColumn<>("Nom"); cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Course,String> cProf = new TableColumn<>("Professeur");
        cProf.setCellValueFactory(cell -> {
            Course course = cell.getValue();
            Integer pid = course.getProfessorId();
            String name = "";
            if (pid != null) name = repo.findProfessorById(pid).map(Professor::toString).orElse("");
            return new SimpleStringProperty(name);
        });
        table.getColumns().addAll(cId,cCode,cName,cProf);
        table.setPrefHeight(420);

        TextField tfCode = new TextField(); tfCode.setPromptText("Code");
        TextField tfName = new TextField(); tfName.setPromptText("Nom du cours");
        Button btnAdd = new Button("Ajouter");
        btnAdd.setOnAction(e -> {
            if (tfCode.getText().isEmpty() || tfName.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Code et nom du cours sont requis.");
                return;
            }
            try {
                Course c = repo.createCourse(tfCode.getText(), tfName.getText());
                courses.add(c);
                tfCode.clear(); tfName.clear();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur création cours", ex.getMessage());
            }
        });

        ComboBox<Professor> cbProf = new ComboBox<>(professors);
        cbProf.setPromptText("Sélectionner prof");
        Button btnAssignProf = new Button("Assigner prof au cours");
        btnAssignProf.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            Professor p = cbProf.getValue();
            if (selected == null || p == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Sélectionnez un cours et un professeur.");
                return;
            }
            selected.setProfessorId(p.getId());
            table.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Assignation", "Professeur assigné au cours.");
        });

        HBox form = new HBox(8, tfCode, tfName, btnAdd, new Label("Prof:"), cbProf, btnAssignProf);
        form.setPadding(new Insets(8));
        VBox v = new VBox(8, table, form);
        v.setPadding(new Insets(8));
        return v;
    }

    private VBox enrollmentsPane() {
        TableView<Enrollment> table = new TableView<>(enrollments);
        TableColumn<Enrollment,Integer> cId = new TableColumn<>("ID"); cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Enrollment,String> cStudent = new TableColumn<>("Étudiant");
        cStudent.setCellValueFactory(cell -> {
            Enrollment en = cell.getValue();
            return new SimpleStringProperty(repo.findStudentById(en.getStudentId()).map(Student::toString).orElse("?"));
        });
        TableColumn<Enrollment,String> cCourse = new TableColumn<>("Cours");
        cCourse.setCellValueFactory(cell -> {
            Enrollment en = cell.getValue();
            return new SimpleStringProperty(repo.findCourseById(en.getCourseId()).map(Course::toString).orElse("?"));
        });
        TableColumn<Enrollment,String> cGrades = new TableColumn<>("Notes");
        cGrades.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGrades().toString()));

        table.getColumns().addAll(cId,cStudent,cCourse,cGrades);
        table.setPrefHeight(350);

        ComboBox<Student> cbStudents = new ComboBox<>(students);
        ComboBox<Course> cbCourses = new ComboBox<>(courses);
        Button btnEnroll = new Button("Inscrire");
        btnEnroll.setDisable(true);

        cbStudents.valueProperty().addListener((obs, oldV, newV) -> btnEnroll.setDisable(cbStudents.getValue()==null || cbCourses.getValue()==null));
        cbCourses.valueProperty().addListener((obs, oldV, newV) -> btnEnroll.setDisable(cbStudents.getValue()==null || cbCourses.getValue()==null));

        btnEnroll.setOnAction(e -> {
            Student s = cbStudents.getValue(); Course c = cbCourses.getValue();
            if (s == null || c == null) return;
            try {
                if (repo.hasEnrollment(s.getId(), c.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Déjà inscrit", "L'étudiant est déjà inscrit à ce cours.");
                    return;
                }
                Enrollment en = repo.createEnrollment(s.getId(), c.getId());
                enrollments.add(en);
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur inscription", ex.getMessage());
            }
        });

        TextField tfGrade = new TextField(); tfGrade.setPromptText("Note numérique");
        Button btnAddGrade = new Button("Ajouter note");
        btnAddGrade.setOnAction(e -> {
            Enrollment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Sélectionnez une inscription pour ajouter une note.");
                return;
            }
            try {
                double g = Double.parseDouble(tfGrade.getText());
                repo.assignGrade(sel.getId(), g);
                enrollments.setAll(repo.listEnrollments());
                tfGrade.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Valeur invalide", "Entrez un nombre pour la note.");
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur note", ex.getMessage());
            }
        });

        Button btnDeleteEnrollment = new Button("Supprimer inscription");
        btnDeleteEnrollment.setOnAction(e -> {
            Enrollment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Sélectionnez une inscription à supprimer.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer l'inscription sélectionnée ?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmer suppression");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    boolean ok = repo.deleteEnrollment(sel.getId());
                    if (ok) {
                        enrollments.setAll(repo.listEnrollments());
                        showAlert(Alert.AlertType.INFORMATION, "Supprimé", "Inscription supprimée.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'inscription.");
                    }
                }
            });
        });

        HBox form = new HBox(8, cbStudents, cbCourses, btnEnroll, tfGrade, btnAddGrade, btnDeleteEnrollment);
        form.setPadding(new Insets(8));
        VBox v = new VBox(8, table, form);
        v.setPadding(new Insets(8));
        return v;
    }

    private VBox professorsPane() {
        TableView<Professor> table = new TableView<>(professors);
        TableColumn<Professor,Integer> cId = new TableColumn<>("ID"); cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Professor,String> cFirst = new TableColumn<>("Prénom"); cFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Professor,String> cLast = new TableColumn<>("Nom"); cLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Professor,String> cEmail = new TableColumn<>("Email"); cEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        table.getColumns().addAll(cId,cFirst,cLast,cEmail);

        TextField tfFirst = new TextField(); tfFirst.setPromptText("Prénom");
        TextField tfLast = new TextField(); tfLast.setPromptText("Nom");
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");
        Button btnAdd = new Button("Ajouter");
        btnAdd.setOnAction(e -> {
            if (tfFirst.getText().isEmpty() || tfLast.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Prénom et nom sont requis.");
                return;
            }
            try {
                Professor p = repo.createProfessor(tfFirst.getText(), tfLast.getText(), tfEmail.getText());
                professors.add(p);
                tfFirst.clear(); tfLast.clear(); tfEmail.clear();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur création professeur", ex.getMessage());
            }
        });

        Button btnDelete = new Button("Supprimer prof");
        btnDelete.setOnAction(e -> {
            Professor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise", "Sélectionnez un professeur à supprimer.");
                return;
            }
            repo.deleteProfessor(sel.getId());
            professors.setAll(repo.listProfessors());
            courses.setAll(repo.listCourses());
            showAlert(Alert.AlertType.INFORMATION, "Supprimé", "Professeur supprimé.");
        });

        HBox form = new HBox(8, tfFirst, tfLast, tfEmail, btnAdd, btnDelete);
        form.setPadding(new Insets(8));
        VBox v = new VBox(8, table, form);
        v.setPadding(new Insets(8));
        return v;
    }

    private void doSave() {
        try { repo.save(); showAlert(Alert.AlertType.INFORMATION, "Sauvegarde", "Données sauvegardées."); }
        catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Échec sauvegarde", ex.getMessage()); }
    }

    private void doLoad() {
        if (repo instanceof Repository) {
            Repository r = Repository.load();
            students.setAll(r.listStudents());
            courses.setAll(r.listCourses());
            enrollments.setAll(r.listEnrollments());
            professors.setAll(r.listProfessors());
            showAlert(Alert.AlertType.INFORMATION, "Chargement", "Données chargées depuis le fichier local.");
        } else {
            // Pour PostgreSQL, recharger depuis la base
            students.setAll(repo.listStudents());
            courses.setAll(repo.listCourses());
            enrollments.setAll(repo.listEnrollments());
            professors.setAll(repo.listProfessors());
            showAlert(Alert.AlertType.INFORMATION, "Chargement", "Données rechargées depuis PostgreSQL.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String text) {
        Alert a = new Alert(type, text, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
