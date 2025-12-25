public class Absence {

    private int id;
    private Etudiant etudiant;
    private Date date;
    private String raison;

    public Absence(int id, Etudiant etudiant, Date date, String raison) {
        this.id = id;
        this.etudiant = etudiant;
        this.date = date;
        this.raison = raison;
    }

    public Etudiant getEtudiant() { return etudiant; }
    public Date getDate() { return date; }
    public String getRaison() { return raison; }
}