import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Main {
    static void main(String[] args) {

        // Création d'une classe et des personnes
        Classe classe1 = new Classe(1, "IIR3", "3ème année");
        Professeur prof1 = new Professeur(1, "Ammor", "Fatimezzahra", new Date());
        Etudiant e1 = new Etudiant(1, "Wiam", "Matat", new Date());
        classe1.addEtudiant(e1);


        // Upcasting

        Personne p1 = e1;
        Personne p2 = prof1;
        p1.afficher();
        p2.afficher();


        Personne p22 = new Professeur(1, "wiwi", "matat", new Date());


        if (p2 instanceof Professeur prof2) {
            prof2.afficher();

            List<Personne> personnes = List.of(
                    new Etudiant(1, "Wiam", "Matat", new java.util.Date()),
                    new Professeur(2, "Ammor", "Fatimezzahra", new java.util.Date()),
                    new Etudiant(3, "Ali", "Bensalem", new java.util.Date())
            );


            long nbEtudiants = personnes.stream()
                    .filter(p -> p instanceof Etudiant)
                    .map(p -> (Etudiant) p)
                    .distinct()
                    .limit(5)
                    .count();

            System.out.println("Nombre d'étudiants (max 5) : " + nbEtudiants);


            try (BufferedWriter bw = new BufferedWriter(new FileWriter("notes.txt", true))) {
                bw.write("Nouvelle note: 18");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedWriter bw =


                try {
                    BufferedReader br = new BufferedReader(new FileReader("notes.txt")); // IN
                    String ligne;
                    while ((ligne = br.readLine()) != null) {
                        System.out.println(ligne);
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // ExecutorService pour notifs

                ExecutorService service = Executors.newFixedThreadPool(2);
                service.submit(() -> System.out.println("Notif wiwi absent "));
                service.submit(() -> System.out.println("Notif wiam absent "));
                service.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
                                  }





