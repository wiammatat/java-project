import java.util.Date;

public class Main {
    public static void main(String[] args) {

        Classe classe1 = new Classe(1, "IIR3", "3ème année");

        Professeur prof1 = new Professeur(1, "Ammor", "Fatimezzahra", new Date());
        Etudiant e1 = new Etudiant(1, "Wiam", "Matat", new Date());
        classe1.addEtudiant(e1);

        //upcasting
        Personne p1 = e1;
        Personne p2 = prof1;
        p1.afficher();
        p2.afficher();

        //downcasting
        if (p2 instanceof Professeur) {
            Professeur prof2 = (Professeur) p2;
            System.out.println("Professeur  : " + prof2.getNom() +  prof2.getPrenom());
        }
    }
}




