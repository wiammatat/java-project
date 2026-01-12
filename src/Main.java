import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        // Création d'une classe et des personnes
        Classe classe1 = new Classe(1, "IIR3", "3ème année");
        Professeur prof1 = new Professeur(1, "Ammor", "Fatimezzahra", new Date());
        Etudiant e1 = new Etudiant(1, "Wiam", "Matat", new Date());
        classe1.addEtudiant(e1);

        // UPCASTING
        Personne p1 = e1;
        Personne p2 = prof1;
        p1.afficher();
        p2.afficher();

        // Downcasting
        Personne p22 = new Professeur(1, "wiwi", "matat", new Date());
        Personne p55 = new Professeur(2, "wiam", "matat", new Date());
        Personne p99 =new Etudiant(1,"wii","maa",new Date());
        if (p22 instanceof Professeur prof2) {
            prof2.afficher();
        }
        // Stream
        List<Personne> personnes = List.of(
                new Etudiant(1, "Wiam", "Matat", new Date()),
                new Professeur(2, "Ammor", "Fatimezzahra", new Date()),
                new Etudiant(3, "Ali", "mat", new Date())
        );
        List<Etudiant>etudiants = List.of(
                new Etudiant(9, "Wiam", "Matat", new Date()));
        etudiants.stream()
                .filter(e->e.getNom().startsWith("M"))
                .distinct()
                .map(Etudiant::getPrenom )
                .forEach(prenom -> System.out.println(prenom));



        long nbEtudiants = personnes.stream()
                .filter(p -> p instanceof Etudiant)
                .count();

        System.out.println("Nombre d'étudiants : " + nbEtudiants);

        // ecriture
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("notes.txt", true))) {
            bw.write("Nouvelle note : 18");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // lecture
        try (BufferedReader br = new BufferedReader(new FileReader("notes.txt"))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                System.out.println(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        //THREADS

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> System.out.println("Notif wiwi absent"));
        service.submit(() -> System.out.println("Notif wiam "));
        service.shutdown();

        ExecutorService service2 = Executors.newFixedThreadPool(3);
        service.submit(() -> System.out.println("Notif wiam "));
    }
}