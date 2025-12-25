import java.util.Date;

public class Note {
    private int id;
    private Matiere matiere;
    private double valeur;
    private Etudiant etudiant;
    private Date date;

    public Note(int id, Matiere matiere, double valeur, Etudiant etudiant, Date date) {
        this.id = id;
        this.matiere = matiere;
        this.valeur = valeur;
        this.etudiant = etudiant;
        this.date = date;
    }

    public double getValeur() { return valeur; }
    public Matiere getMatiere() { return matiere; }
    public Etudiant getEtudiant() { return etudiant; }
}
