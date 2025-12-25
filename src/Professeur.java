import java.util.*;

public class Professeur implements Personne {
    private int id;
    private String nom;
    private String prenom;
    private Date dateEmbauche;
    private List<Matiere> matieres = new ArrayList<>();
    private List<Classe> classes = new ArrayList<>();
    private List<Cours> cours = new ArrayList<>();

    public Professeur(int id, String nom, String prenom, Date dateEmbauche) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateEmbauche = dateEmbauche;
    }

    public void ajouterClasse(Classe c) {
        classes.add(c);
    }

    public void ajouterCours(Cours c) {
        cours.add(c);
    }

    public List<Classe> getClasses() {
        return classes;
    }

    public List<Cours> getCours() {
        return cours;
    }

    // ---------------------- MÉTHODES EXISTANTES AVEC STREAMS ----------------------

    // Afficher toutes les matières du professeur
    public void afficherMatieres() {
        matieres.stream()
                .forEach(m -> System.out.println(m.getNom()));
    }

    // Compter le nombre de matières
    public long nombreMatieres() {
        return matieres.stream().count();
    }

    /**
     * Récupère et affiche les cours d'une matière spécifique pour ce professeur.
     * @param nomMatiere Le nom de la matière à rechercher (ex : "Java")
     * @return Une liste des cours correspondant à cette matière
     */
    public List<Cours> getCoursParMatiere(String nomMatiere) {


        return List.of();
    }}