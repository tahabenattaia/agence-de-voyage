CREATE DATABASE travel_db;
USE travel_db;

CREATE TABLE voyage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reference VARCHAR(50) NOT NULL,
    prix_par_personne INT NOT NULL,
    destination VARCHAR(100) NOT NULL,
    descriptif TEXT,
    date_depart DATE NOT NULL,
    date_retour DATE NOT NULL,
    type_voyage ENUM('ORGANISE', 'PERSONNALISE') NOT NULL
);

CREATE TABLE voyage_organise (
    id BIGINT PRIMARY KEY,
    nb_place_maxi INT NOT NULL,
    date_validite DATE NOT NULL,
    FOREIGN KEY (id) REFERENCES voyage(id)
);

CREATE TABLE voyage_personnalise (
    id BIGINT PRIMARY KEY,
    preference TEXT,
    FOREIGN KEY (id) REFERENCES voyage(id)
);

CREATE TABLE client (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code_cli VARCHAR(50) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    type_client ENUM('ENTREPRISE', 'PARTICULIER') NOT NULL
);

CREATE TABLE entreprise (
    id BIGINT PRIMARY KEY,
    matricule_fiscale VARCHAR(50) NOT NULL,
    registre_commerce VARCHAR(50) NOT NULL,
    FOREIGN KEY (id) REFERENCES client(id)
);

CREATE TABLE particulier (
    id BIGINT PRIMARY KEY,
    cin VARCHAR(20) NOT NULL,
    FOREIGN KEY (id) REFERENCES client(id)
);

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_reservation DATE NOT NULL,
    nb_place INT NOT NULL,
    id_voyage BIGINT,
    id_client BIGINT,
    FOREIGN KEY (id_voyage) REFERENCES voyage(id),
    FOREIGN KEY (id_client) REFERENCES client(id)
);

CREATE TABLE itineraire (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_voyage BIGINT,
    FOREIGN KEY (id_voyage) REFERENCES voyage(id)
);

CREATE TABLE jour (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    jour INT NOT NULL,
    description TEXT,
    id_itineraire BIGINT,
    FOREIGN KEY (id_itineraire) REFERENCES itineraire(id)
);

