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
//pour les dakchi de l'interface

    @Override
    public void afficher() {
        System.out.println("Professeur: " + nom + " " + prenom);
    }

    @Override public int getId() {
        return id;
    }
    @Override public String getNom() {
        return nom;
    }
    @Override public String getPrenom() {
        return prenom;
    }
}
