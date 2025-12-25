import java.util.*;

public class Etudiant implements Personne {
    private int id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private Classe classe;
    private List<Note> notes = new ArrayList<>();


    public Etudiant(int id, String nom, String prenom, Date dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }


    public void ajouterNote(Note n) {
        notes.add(n);
    }


    public List<Note> getNotes() {
        return notes;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }
    //pour  l'interface
    @Override
    public void afficher() {
        System.out.println("Étudiant: " + nom + " " + prenom);
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
