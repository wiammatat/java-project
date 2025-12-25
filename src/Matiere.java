class Matiere {
    private int id;
    private String nom;
    private int coefficient;

    public Matiere(int id, String nom, int coefficient) {
        this.id = id;
        this.nom = nom;
        this.coefficient = coefficient;
    }

    public String getNom() { return nom; }
    public int getCoefficient() { return coefficient; }
}