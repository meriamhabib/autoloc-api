# Atelier 4 : Inversion de Contrôle, Injection de Dépendances et Couche Service

---

## A. Modes d'injection comparés

| Critère | Injection par constructeur (recommandée) | Injection par attribut (@Autowired sur champ) |
| :--- | :--- | :--- |
| **Champ inal possible ?** | **Oui** : les dépendances sont assignées dès l'instanciation, garantissant l'immuabilité du bean. | **Non** : les champs ne peuvent pas être inal car Spring les injecte après coup par réflexion. |
| **Dépendance visible dans la signature ?** | **Oui** : le constructeur expose clairement le contrat et toutes les dépendances requises pour faire fonctionner la classe. | **Non** : dépendances masquées à l'extérieur ; impossible de savoir ce dont la classe a besoin sans lire son code interne. |
| **Utilisation hors conteneur (avec 
ew) sans dépendance 
ull ?** | **Oui** : le compilateur impose de fournir les dépendances lors du 
ew (très pratique pour les tests unitaires purs). | **Non** : l'instanciation par 
ew MyService() laisse les champs à 
ull, provoquant un NullPointerException à l'exécution. |
| **Signalé par SonarQube for IDE ?** | **Non** : conforme aux meilleures pratiques Clean Code et Spring. | **Oui** : règle **java:S6813** (*Field dependency injection should not be used*). |

---

## B. Couche Service : Dépendances et Modes d'injection

Tous les services respectent la convention d'architecture AutoLoc : **injection par constructeur**, champs private final, dépendance typée par **interface**, et classe annotée avec @Service et @RequiredArgsConstructor.

| Service | Dépendances injectées | Mode | Justification |
| :--- | :--- | :--- | :--- |
| ContratServiceImpl | IContratRepository | Constructeur | Centralise la logique métier des contrats, refuse un identifiant à la création, valide un montant non négatif et charge l'existant avant modification pour protéger la cascade des paiements. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Service en lecture seule (indById, indAll). Les écritures (création, mise à jour, suppression) sont déléguées au cycle de vie du Contrat parent. |
| AgenceServiceImpl | IAgenceRepository | Constructeur | Gestion du parc d'agences, vérification de la présence obligatoire du nom de l'agence, découplage fort via interface. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Gestion des employés, vérification des attributs d'identification (nom, prénom, rôle/poste non vides), immuabilité garantie. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Gestion du catalogue de véhicules, contrôle de validité sur le tarif journalier (refus des montants négatifs). |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Gestion du référentiel d'équipements, contrôle de la présence du libellé obligatoire. |
| ClientServiceImpl | IClientRepository | Constructeur | Gestion de la clientèle, validation des données d'identification obligatoires (nom et e-mail non vides). |
| ReservationServiceImpl | IReservationRepository | Constructeur | Gestion des réservations, validation de la cohérence temporelle (refus d'une date de fin antérieure à la date de début). |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Suivi des opérations de maintenance, contrôle de cohérence chronologique (dateFin >= dateDebut). |

---

## C. Diagnostic des messages d'erreur du conteneur Spring (Étape 3)

### Message 1 : Dépendance introuvable
`	ext
APPLICATION FAILED TO START
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService' that could not be found.
`
- **Exception levée** : NoSuchBeanDefinitionException (souvent encapsulée dans UnsatisfiedDependencyException).
- **Bean concerné** : IContratService.
- **Deux causes probables côté ContratServiceImpl** :
  1. *Oubli de l'annotation de stéréotype* : la classe ContratServiceImpl n'a pas été annotée avec @Service (ou @Component), elle n'est donc pas détectée lors du scan de composants.
  2. *Package hors du scan* : la classe est déclarée dans un package qui ne descend pas du package racine annoté @SpringBootApplication (	n.esprit.autoloc), par exemple 	n.esprit.service.
- **Correction** : Ajouter l'annotation @Service sur ContratServiceImpl et s'assurer qu'elle se trouve bien dans le package 	n.esprit.autoloc.service.

### Message 2 : Ambiguïté entre plusieurs beans candidats
`	ext
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.NotificationController
required a single bean, but 2 were found:
 - emailNotificateur
 - smsNotificateur
`
- **Exception levée** : NoUniqueBeanDefinitionException.
- **Bean concerné** : Dépendance de type INotificateur injectée dans NotificationController.
- **Deux propositions de correction** :
  1. *Option 1 : Utilisation de @Primary* : Poser l'annotation @Primary sur la classe candidate préférée par défaut (ex. @Component @Primary public class EmailNotificateur implements INotificateur).
  2. *Option 2 : Utilisation de @Qualifier* : Spécifier explicitement le bean cible au point d'injection dans le contrôleur (ex. public NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur)).

### Message 3 : Dépendance circulaire
`	ext
The dependencies of some of the beans in the application context form a cycle:
 clientServiceImpl -> reservationServiceImpl -> clientServiceImpl
`
- **Exception levée** : BeanCurrentlyInCreationException.
- **Pourquoi l'injection par constructeur rend ce cycle impossible à contourner ?**
  Pour instancier ClientServiceImpl, Spring doit d'abord résoudre et lui injecter ReservationServiceImpl. Mais pour instancier ReservationServiceImpl, Spring a besoin d'une instance déjà prête de ClientServiceImpl. Aucun constructeur ne pouvant aboutir avant l'autre, le conteneur est bloqué dès la phase d'instanciation.
- **Piste de conception pour le supprimer** :
  Appliquer le principe de responsabilité unique (SRP) : extraire la responsabilité croisée commune dans un troisième service intermédiaire (ex. ReservationValidationService), ou réorganiser le flux métier afin que la dépendance devienne unidirectionnelle (ReservationServiceImpl dépend de IClientService, sans dépendance réciproque).

---

## D. Anomalies SonarQube for IDE relevées et corrigées

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
| :--- | :--- | :--- |
| **Injection par attribut (@Autowired sur champ)** | **java:S6813** : L'injection directe sur les champs via @Autowired rend les dépendances opaques, empêche l'immuabilité (champs inal) et complique l'instanciation des classes en test unitaire sans conteneur. | Remplacement par l'injection par constructeur avec Lombok @RequiredArgsConstructor et champs déclarés private final. |
| **Exception générique (RuntimeException)** | **java:S112** : La levée d'exceptions génériques (RuntimeException, Exception) empêche un traitement granulaire des erreurs et dégrade la clarté du domaine métier. | Remplacement par une exception métier explicite et ciblée : ResourceNotFoundException. |
| **Imports wildcard (import jakarta.persistence.*;)** | **java:S2208** : Les imports avec joker polluent l'espace de noms et augmentent le risque de collision de noms de classes lors des mises à jour. | Remplacement systématique par des imports explicites de chaque type et annotation JPA. |

---

## E. Questions de compréhension

### 1. Où est le 
ew de ContratServiceImpl et qui l'exécute ?
Il n'y a aucun appel 
ew ContratServiceImpl(...) dans le code source écrit par le développeur. C'est le conteneur Spring IoC (ApplicationContext / BeanFactory) qui instancie la classe par introspection/réflexion lors du démarrage de l'application, après détection de l'annotation @Service.

### 2. Pourquoi un contrôleur dépendra-t-il de IContratService et non de ContratServiceImpl ?
Pour respecter le principe d'Inversion des Dépendances (DIP des principes SOLID) et favoriser le couplage faible. Le contrôleur s'appuie uniquement sur le contrat défini par l'interface, ce qui permet de remplacer ou enrichir l'implémentation (mocks de test, implémentation transactionnelle, proxys AOP) sans impacter le contrôleur.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.
Un service singleton n'existe qu'en un seul exemplaire partagé entre tous les threads de l'application (requêtes HTTP concurrentes). S'il stocke un état modifiable (par exemple private Contrat contratEnCours;), des requêtes simultanées de plusieurs utilisateurs écraseront simultanément cet attribut (*race condition*).
*Exemple de bug* : L'utilisateur A commence à modifier son contrat #1, mais l'utilisateur B lance simultanément la modification du contrat #2 ; le service sauvegarde alors par erreur les données de l'utilisateur B dans le contrat de l'utilisateur A.

### 4. Pourquoi update charge-t-il l'entité existante avant d'appeler save au lieu de lui passer directement l'objet reçu ?
Passer directement l'objet reçu écraserait toutes les colonnes de la table, y compris celles qui n'ont pas été renseignées dans la requête (qui deviendraient alors 
ull). De plus, pour les associations gérées avec orphanRemoval = true (comme la liste des paiements associés à un contrat), cela viderait et supprimerait définitivement les paiements orphelins en base. Charger l'entité existante permet de ne modifier de manière contrôlée que les champs autorisés.

### 5. Différences @Component / @Bean, @Primary / @Qualifier, et IPaiementService en lecture seule :
- **@Component vs @Bean** : @Component est une annotation de classe destinée au scan automatique de nos propres composants. @Bean est une annotation de méthode dans une classe @Configuration, utilisée pour instancier manuellement et configurer des objets dont on ne possède pas le code source (bibliothèques externes, frameworks).
- **@Primary vs @Qualifier** : @Primary définit au niveau de la classe l'implémentation choisie par défaut lorsqu'il y a ambiguïté. @Qualifier("nomDuBean") s'utilise au point d'injection pour forcer explicitement le choix d'un bean particulier.
- **IPaiementService sans création ni suppression** : Dans le modèle du domaine AutoLoc, le cycle de vie du paiement est entièrement dépendant de son contrat (composition). Un paiement ne peut exister ou être modifié que par l'intermédiaire de son Contrat. IPaiementService est donc volontairement restreint à la lecture (indById, indAll).