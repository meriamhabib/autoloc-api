# Atelier 5 : Spring MVC et SpringDoc — Exposition REST, Tests Postman et Documentation Swagger

---

## A. Endpoints de l'API REST

Le préfixe commun /api est configuré globalement dans pplication.properties via server.servlet.context-path=/api. Les contrôleurs déclarent donc leurs routes sans ce préfixe.

| Contrôleur | Verbe et route | Service appelé | Succès | Codes d'erreur | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ContratController | GET /api/contrats | contratService.findAll() | 200 OK | — | Lister tous les contrats |
| ContratController | GET /api/contrats/{id} | contratService.findById(id) | 200 OK | 404 Not Found | Consulter un contrat |
| ContratController | POST /api/contrats | contratService.create(contrat) | 201 Created | 400 Bad Request | Créer un contrat |
| ContratController | PUT /api/contrats/{id} | contratService.update(id, contrat) | 200 OK | 400, 404 | Modifier un contrat |
| ContratController | DELETE /api/contrats/{id} | contratService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer un contrat |
| PaiementController | GET /api/paiements | paiementService.findAll() | 200 OK | — | Lister tous les paiements |
| PaiementController | GET /api/paiements/{id} | paiementService.findById(id) | 200 OK | 404 Not Found | Consulter un paiement |
| PaiementController | POST /api/paiements | — *(Non exposé)* | — | 405 Method Not Allowed | Écriture interdite |
| AgenceController | GET /api/agences | genceService.findAll() | 200 OK | — | Lister toutes les agences |
| AgenceController | GET /api/agences/{id} | genceService.findById(id) | 200 OK | 404 Not Found | Consulter une agence |
| AgenceController | POST /api/agences | genceService.create(agence) | 201 Created | 400 Bad Request | Créer une agence |
| AgenceController | PUT /api/agences/{id} | genceService.update(id, agence) | 200 OK | 400, 404 | Modifier une agence |
| AgenceController | DELETE /api/agences/{id} | genceService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer une agence |
| ClientController | GET /api/clients | clientService.findAll() | 200 OK | — | Lister tous les clients |
| ClientController | GET /api/clients/{id} | clientService.findById(id) | 200 OK | 404 Not Found | Consulter un client |
| ClientController | POST /api/clients | clientService.create(client) | 201 Created | 400 Bad Request | Créer un client |
| ClientController | PUT /api/clients/{id} | clientService.update(id, client) | 200 OK | 400, 404 | Modifier un client |
| ClientController | DELETE /api/clients/{id} | clientService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer un client |
| VehiculeController | GET /api/vehicules | ehiculeService.findAll() | 200 OK | — | Lister tous les véhicules |
| VehiculeController | GET /api/vehicules/{id} | ehiculeService.findById(id) | 200 OK | 404 Not Found | Consulter un véhicule |
| VehiculeController | POST /api/vehicules | ehiculeService.create(vehicule) | 201 Created | 400 Bad Request | Créer un véhicule |
| VehiculeController | PUT /api/vehicules/{id} | ehiculeService.update(id, vehicule) | 200 OK | 400, 404 | Modifier un véhicule |
| VehiculeController | DELETE /api/vehicules/{id} | ehiculeService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer un véhicule |
| EmployeController | GET /api/employes | employeService.findAll() | 200 OK | — | Lister tous les employés |
| EmployeController | GET /api/employes/{id} | employeService.findById(id) | 200 OK | 404 Not Found | Consulter un employé |
| EmployeController | POST /api/employes | employeService.create(employe) | 201 Created | 400 Bad Request | Créer un employé |
| EmployeController | PUT /api/employes/{id} | employeService.update(id, employe) | 200 OK | 400, 404 | Modifier un employé |
| EmployeController | DELETE /api/employes/{id} | employeService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer un employé |
| EquipementController | GET /api/equipements | equipementService.findAll() | 200 OK | — | Lister les équipements |
| EquipementController | GET /api/equipements/{id} | equipementService.findById(id) | 200 OK | 404 Not Found | Consulter un équipement |
| EquipementController | POST /api/equipements | equipementService.create(equipement) | 201 Created | 400 Bad Request | Créer un équipement |
| EquipementController | PUT /api/equipements/{id} | equipementService.update(id, equipement) | 200 OK | 400, 404 | Modifier un équipement |
| EquipementController | DELETE /api/equipements/{id} | equipementService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer un équipement |
| ReservationController | GET /api/reservations | eservationService.findAll() | 200 OK | — | Lister les réservations |
| ReservationController | GET /api/reservations/{id} | eservationService.findById(id) | 200 OK | 404 Not Found | Consulter une réservation |
| ReservationController | POST /api/reservations | eservationService.create(reservation) | 201 Created | 400 Bad Request | Créer une réservation |
| ReservationController | PUT /api/reservations/{id} | eservationService.update(id, reservation) | 200 OK | 400, 404 | Modifier une réservation |
| ReservationController | DELETE /api/reservations/{id} | eservationService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer une réservation |
| MaintenanceController | GET /api/maintenances | maintenanceService.findAll() | 200 OK | — | Lister les maintenances |
| MaintenanceController | GET /api/maintenances/{id} | maintenanceService.findById(id) | 200 OK | 404 Not Found | Consulter une maintenance |
| MaintenanceController | POST /api/maintenances | maintenanceService.create(maintenance) | 201 Created | 400 Bad Request | Créer une maintenance |
| MaintenanceController | PUT /api/maintenances/{id} | maintenanceService.update(id, maintenance) | 200 OK | 400, 404 | Modifier une maintenance |
| MaintenanceController | DELETE /api/maintenances/{id} | maintenanceService.deleteById(id) | 204 No Content| 404 Not Found | Supprimer une maintenance |

---

## B. Annotations Jackson et Sérialisation JSON

### 1. Tableau des associations et annotations

| Association | Annotation retenue | Justification |
| :--- | :--- | :--- |
| Contrat ↔ Paiement | @JsonManagedReference("contrat-paiements") côté Contrat<br>@JsonBackReference("contrat-paiements") côté Paiement | Véritable relation de composition. Les paiements font partie intégrante du JSON du contrat et sont créés en cascade (CascadeType.ALL). La paire d'annotations brise la boucle infinie tout en permettant la sérialisation imbriquée. |
| Agence ↔ Vehicule | @JsonIgnore sur Agence.vehicules | Évite le chargement et la sérialisation récursive de tous les véhicules lors de la lecture d'une agence. Les véhicules désignent leur agence propriétaire via gence. |
| Agence ↔ Employe | @JsonIgnore sur Agence.employes | Brise la récursion infinie entre l'agence et ses employés. |
| Client ↔ Reservation | @JsonIgnore sur Client.reservations | Empêche la boucle infinie tout en conservant la référence client sur chaque réservation. |
| Vehicule ↔ Maintenance | @JsonIgnore sur Vehicule.maintenances | Évite l'alourdissement du JSON du catalogue de véhicules par l'historique complet des pannes et maintenances. |
| Vehicule ↔ Reservation | @JsonIgnore sur Vehicule.reservations | Évite d'embarquer tout le carnet de réservations lors de la consultation d'un véhicule. |
| Equipement ↔ Vehicule | @JsonIgnore sur Equipement.vehicules | La relation many-to-many est visualisée depuis le véhicule (ehicule.equipements) ; le côté inverse est ignoré. |

### 2. Différence entre @JsonBackReference et @JsonIgnore
- **@JsonBackReference** : Fonctionne obligatoirement en binôme avec @JsonManagedReference. Elle indique à Jackson que ce champ est la référence arrière d'une relation bidirectionnelle. Le champ est omis lors de la sérialisation, mais lors de la désérialisation d'un corps imbriqué (ex. création d'un contrat avec sa liste de paiements), Jackson reconstitue automatiquement le lien bidirectionnel (paiement.setContrat(parent)).
- **@JsonIgnore** : Ignore inconditionnellement le champ annoté lors de la sérialisation et de la désérialisation JSON. Elle est utilisée pour les listes côté inverse afin d'éviter la récursion sans chercher à maintenir de lien parent-enfant imbriqué dans le JSON.

---

## C. Rôles complémentaires de Postman et Swagger UI

| Outil | Rôle principal | Exemple concret dans l'Atelier 5 |
| :--- | :--- | :--- |
| **Postman** | Outil de test, d'automatisation et de validation d'API. Permet de rejouer des scénarios, de manipuler des variables d'environnement ({{contratId}}) et d'exécuter des assertions automatiques (pm.test()). | **Scénario de cascade** : POST d'un contrat avec 2 paiements imbriqués, mémorisation automatique de l'ID généré dans une variable, puis vérification via GET /api/paiements que les 2 paiements ont bien été persistés en base. |
| **Swagger UI (SpringDoc)** | Interface web interactive de documentation vivante du contrat d'API (OpenAPI 3). Synchronisée avec le code source sans écriture manuelle de documentation externe. | **Exploration et test rapide** : Découverte des schémas JSON attendus pour chaque entité, consultation des codes de retour documentés (200, 201, 400, 404), et exécution directe via le bouton *Try it out*. |

---

## D. Anomalies SonarQube for IDE relevées et corrigées

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
| :--- | :--- | :--- |
| **Injection par champ avec @Autowired** | **java:S6813** : L'injection par attribut masque les dépendances de la classe et empêche l'immuabilité (inal). | Injection par constructeur systématique avec Lombok @RequiredArgsConstructor et champs déclarés private final I...Service. |
| **Imports inutilisés ou wildcard dans les contrôleurs** | **java:S2208 / java:S1128** : Les imports inutilisés encombrent le code et les wildcards nuisent à la lisibilité. | Nettoyage et utilisation d'imports ciblés dans tous les contrôleurs. |
| **Exposition directe des entités de domaine (JPA Entities dans les contrôleurs)** | **java:S4684** : L'exposition directe des entités JPA dans la couche web couple fortement l'API à la base de données et expose des attributs internes. | Identifié et documenté : le couplage provisoire sera résolu au Chapitre 10 avec la mise en place des DTO (*Data Transfer Objects*). |

---

## E. Questions de compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP entrantes sont interceptées par le point d'entrée unique de Spring MVC : la **DispatcherServlet** (*Front Controller pattern*). Elle consulte ensuite le composant **HandlerMapping** (notamment RequestMappingHandlerMapping), qui maintient une table de correspondance entre les verbes/URLs HTTP et les méthodes des contrôleurs annotées avec @RequestMapping, @GetMapping, @PostMapping, etc.

### 2. Pourquoi ContratController dépend-il de IContratService et non de ContratRepository ni de ContratServiceImpl ?
- **Pas de ContratRepository** : Pour respecter l'architecture en couches. Le contrôleur gère uniquement le protocole HTTP et délègue toute la logique métier, validations et règles de gestion à la couche Service.
- **Pas de ContratServiceImpl** : Pour respecter le principe d'inversion des dépendances (DIP de SOLID) et le couplage faible. Dépendre de l'interface IContratService permet de remplacer l'implémentation (mock pour les tests, proxys de sécurité ou de transaction) sans toucher au contrôleur.

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
- **POST → 201 Created** : La norme HTTP stipule que la création réussie d'une nouvelle ressource doit être signifiée par le statut 201, souvent accompagnée de la ressource créée avec son identifiant généré.
- **DELETE → 204 No Content** : La ressource a été supprimée avec succès et il n'y a plus aucun contenu à retourner dans le corps de la réponse.
- **Ressource absente → 404 Not Found (et non 500 Internal Server Error)** : Une absence d'enregistrement pour un identifiant demandé est une situation métier normale due à une requête cliente sur une ressource inexistante (erreur client 4xx), et non un plantage imprévu du serveur (erreur serveur 5xx). GlobalExceptionHandler intercepte ResourceNotFoundException et renvoie proprement un 404.

### 4. Pourquoi PaiementController n'expose-t-il que des GET ? Quelle réponse reçoit un POST /api/paiements ?
- Dans le domaine métier AutoLoc, le paiement est lié par composition au contrat : un paiement ne peut être émis ou géré qu'au sein d'un contrat de location. Toute écriture passe obligatoirement par le Contrat. PaiementController n'expose donc que la consultation (indAll, indById).
- Une requête POST /api/paiements reçoit le code **405 Method Not Allowed**, car aucune méthode du contrôleur n'est mappée sur le verbe POST pour cette route.

### 5. Risques de l'exposition directe des entités JPA et différence Contrat.paiements vs Agence.vehicules :
- **Risques de l'exposition directe** : Sur-exposition de champs sensibles ou techniques (password, versions, etc.), vulnérabilité aux attaques de sur-assignation (*Mass Assignment*), problèmes de boucle de sérialisation JSON (StackOverflowError), et requêtes N+1 imprévues déclenchées par la sérialisation des collections paresseuses (Lazy Loading).
- **Pourquoi Contrat.paiements avec @JsonManagedReference et Agence.vehicules avec @JsonIgnore ?** :
  - Contrat ↔ Paiement est une relation de **composition forte** : lorsqu'on consulte un contrat, on souhaite voir le détail de ses paiements dans le JSON. @JsonManagedReference permet d'inclure cette liste sans boucler sur le contrat parent.
  - Agence ↔ Vehicule est une relation d'**agrégation simple** : une agence gère des centaines de véhicules. Embarquer toute la liste des véhicules dans chaque consultation d'agence alourdirait inutilement la réponse. Le véhicule référence déjà son agence propriétaire ; la collection inverse sur l'agence est donc ignorée via @JsonIgnore.