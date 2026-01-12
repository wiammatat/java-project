-- Script de création des tables pour le système de gestion d'école
-- À exécuter dans PostgreSQL

-- Connexion à la base school en tant que school_user
-- psql -h localhost -U school_user -d school

-- Création des tables principales

-- Table des étudiants
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255)
);

-- Table des professeurs
CREATE TABLE IF NOT EXISTS professors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255)
);

-- Table des cours
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    professor_id INTEGER REFERENCES professors(id) ON DELETE SET NULL
);

-- Table des inscriptions avec notes sous forme de tableau
CREATE TABLE IF NOT EXISTS enrollments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    grades numeric[] DEFAULT ARRAY[]::numeric[]
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);
CREATE INDEX IF NOT EXISTS idx_professors_email ON professors(email);
CREATE INDEX IF NOT EXISTS idx_courses_code ON courses(code);

-- Contraintes uniques
ALTER TABLE students ADD CONSTRAINT IF NOT EXISTS unique_student_email UNIQUE (email);
ALTER TABLE professors ADD CONSTRAINT IF NOT EXISTS unique_professor_email UNIQUE (email);
ALTER TABLE courses ADD CONSTRAINT IF NOT EXISTS unique_course_code UNIQUE (code);
ALTER TABLE enrollments ADD CONSTRAINT IF NOT EXISTS unique_enrollment UNIQUE (student_id, course_id);

-- Données de test (optionnel)
INSERT INTO professors (first_name, last_name, email) VALUES
    ('Jean', 'Dupont', 'j.dupont@ecole.fr'),
    ('Marie', 'Martin', 'm.martin@ecole.fr')
ON CONFLICT (email) DO NOTHING;

INSERT INTO students (first_name, last_name, email) VALUES
    ('Alice', 'Bernard', 'alice.bernard@etudiant.fr'),
    ('Bob', 'Durand', 'bob.durand@etudiant.fr'),
    ('Claire', 'Moreau', 'claire.moreau@etudiant.fr')
ON CONFLICT (email) DO NOTHING;

INSERT INTO courses (code, name, professor_id) VALUES
    ('MATH101', 'Mathématiques niveau 1', 1),
    ('INFO102', 'Informatique niveau 2', 2),
    ('PHYS103', 'Physique générale', 1)
ON CONFLICT (code) DO NOTHING;

INSERT INTO enrollments (student_id, course_id, grades) VALUES
    (1, 1, ARRAY[15.5, 17.0]),
    (1, 2, ARRAY[18.5]),
    (2, 1, ARRAY[14.0, 16.5]),
    (3, 3, ARRAY[19.0])
ON CONFLICT (student_id, course_id) DO NOTHING;

-- Affichage des données créées
SELECT 'Étudiants:' as type;
SELECT id, first_name, last_name, email FROM students;

SELECT 'Professeurs:' as type;
SELECT id, first_name, last_name, email FROM professors;

SELECT 'Cours:' as type;
SELECT c.id, c.code, c.name, p.first_name || ' ' || p.last_name as professeur
FROM courses c
LEFT JOIN professors p ON c.professor_id = p.id;

SELECT 'Inscriptions:' as type;
SELECT e.id, s.first_name || ' ' || s.last_name as etudiant,
       c.code || ' - ' || c.name as cours,
       e.grades as notes
FROM enrollments e
JOIN students s ON e.student_id = s.id
JOIN courses c ON e.course_id = c.id;
