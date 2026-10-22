# Atelier 3 : Couche Repository Spring Data JPA

## 1. Choix des interfaces Repository

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, indAll renvoie une List, saveAndFlush disponible. |
| IPaiementRepository | JpaRepository<Paiement, Long> | CRUD complet, manipulation directe en List. Même si la règle métier stipule que la création d'un paiement passe par le Contrat (agrégation), ce repository permet la lecture directe et les requêtes spécifiques sur les paiements. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD complet, retour des collections sous forme de List, tri et pagination indispensables pour le catalogue et la gestion de la flotte de véhicules. |
| IClientRepository | JpaRepository<Client, Long> | CRUD complet, gestion optimisée en List pour la recherche de clients, pagination native pour les listes volumineuses. |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD complet, manipulation directe des réservations sous forme de List, support de la pagination pour l'historique et le suivi des réservations. |
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, manipulation aisée des agences en List, opérations de persistence et de flush immédiat. |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, manipulation sous forme de List, support de pagination et tri selon le rôle ou l'agence de rattachement. |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, renvoi d'une List d'équipements pour faciliter l'affectation multiple aux véhicules. |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, manipulation des opérations de maintenance sous forme de List, pagination pour le journal d'entretien. |

---

## 2. Anomalies SonarQube for IDE (SonarLint)

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
| :--- | :--- | :--- |
| **Imports wildcard (import jakarta.persistence.*;)** | **java:S2208** : L'utilisation de jokers (*) dans les imports est une mauvaise pratique. Elle masque l'origine exacte des classes utilisées, pollue l'espace de noms et peut introduire des conflits d'ambiguïté lors de mises à jour de dépendances. | Remplacement de tous les imports wildcard jakarta.persistence.* par des imports explicites et ciblés (Entity, Table, Id, GeneratedValue, Column, OneToMany, etc.). |
| **Utilisation de @Data de Lombok sur les entités JPA** | **java:S6856** : L'annotation @Data génère automatiquement equals(), hashCode() et 	oString(). Sur des entités JPA comportant des relations bidirectionnelles (@OneToMany / @ManyToOne), cela entraîne des risques majeurs de récursion infinie (StackOverflowError) et force le chargement prématuré de collections en Lazy Loading. | Respect scrupuleux des conventions Clean Code : abandon de @Data au profit d'annotations ciblées (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor). |
| **Convention de nommage des interfaces avec préfixe I (IVehiculeRepository, etc.)** | **java:S114** : SonarQube recommande que les noms d'interfaces ne soient pas préfixés par 'I' (règle standard Java). Cependant, la convention d'architecture imposée par le projet AutoLoc exige explicitement le préfixe I pour distinguer les interfaces de leurs éventuelles implémentations. | Anomalie analysée, justifiée et documentée. La convention d'équipe AutoLoc est conservée conformément au cahier des charges de l'Atelier. |
| **Problème d'encodage et configuration ddl-auto dans pplication.properties** | **java:S3822 / Build failure** : Présence de caractères corrompus avec encodage non UTF-8 provoquant l'échec du build Maven (MalformedInputException), et spring.jpa.hibernate.ddl-auto=create au lieu de update. | Correction de l'encodage du fichier en UTF-8 propre et bascule de la valeur ddl-auto sur update conformément aux prérequis de l'atelier. |