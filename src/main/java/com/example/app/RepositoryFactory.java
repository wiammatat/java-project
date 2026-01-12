package com.example.app;

import java.io.IOException;
import java.sql.SQLException;

public class RepositoryFactory {

    public static RepositoryInterface createRepository() {
        try {
            // Essayer d'abord PostgreSQL
            RepositoryJdbc jdbcRepo = new RepositoryJdbc();
            System.out.println("✓ Utilisation de PostgreSQL");
            return jdbcRepo;
        } catch (SQLException | IOException e) {
            System.out.println("⚠ PostgreSQL non disponible, utilisation du stockage local");
            System.out.println("Raison: " + e.getMessage());
            return Repository.load();
        }
    }
}
