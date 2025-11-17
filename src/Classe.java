import java.util.*;

public class Classe {
    private int id;
    private String nom;
    private String niveau;
    private List<Etudiant> etudiants = new ArrayList<>();
    private List<Professeur> professeurs = new ArrayList<>();
    private EmploiDuTemps emploiDuTemps;

    public Classe(int id, String nom, String niveau) {
        this.id = id;
        this.nom = nom;
        this.niveau = niveau;
    }

    public void addEtudiant(Etudiant e) {
        etudiants.add(e);
        e.setClasse(this);
    }

    public void addProfesseur(Professeur p) {
        professeurs.add(p);
    }

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }
    public List<Professeur> getProfesseurs() {
        return professeurs;
    }
    public EmploiDuTemps getEmploiDuTemps() {
        return emploiDuTemps;
    }
}
