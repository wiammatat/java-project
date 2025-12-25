public class EmploiDuTemps {

    private int id;
    private Classe classe;
    private List<Cours> listeCours = new ArrayList<>();

    public void ajouterCours(Cours c) { listeCours.add(c); }
    public List<Cours> getCours() { return listeCours; }
}