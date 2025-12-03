# Projet Microservices – Spring Boot, Eureka, Gateway, Service Client & Service Voiture

Ce projet illustre une architecture microservices basée sur :

- Un **serveur de découverte** : Eureka Server  
- Une **passerelle d’API** : Spring Cloud Gateway  
- Un microservice **SERVICE-CLIENT** (gestion des clients)  
- Un microservice **SERVICE-CAR** (gestion des voitures + appel au service client)

Ce README décrit, **étape par étape**, la mise en place et le lancement de l’ensemble.

---

## 1. Prérequis

Avant de commencer, vérifier que vous disposez de :

- Java 17 (ou version compatible avec votre Spring Boot)
- Maven ou Gradle
- MySQL en cours d’exécution (port 3306)
- Un IDE (IntelliJ, Eclipse, VS Code…)
- Accès à Spring Initializr : https://start.spring.io

---

## 2. Vue d’ensemble de l’architecture

- **Eureka Server**  
  - Rôle : registre de services  
  - Port : 8761

- **SERVICE-CLIENT**  
  - Rôle : gestion des clients (CRUD simple) + enregistrement dans Eureka  
  - Base de données : clientservicedb  
  - Port : 8081

- **SERVICE-CAR**  
  - Rôle : gestion des voitures + récupération des infos du client via le service client  
  - Base de données : carservicedb  
  - Port : 8082

- **Gateway**  
  - Rôle : point d’entrée unique pour les microservices, routage dynamique via Eureka  
  - Port : 8888

Accès typiques :

- Eureka : http://localhost:8761  
- SERVICE-CLIENT via Gateway : http://localhost:8888/SERVICE-CLIENT/api/client  
- SERVICE-CAR via Gateway : http://localhost:8888/SERVICE-CAR/api/car  

---

## 3. Mise en place du Eureka Server

### 3.1. Création du projet

1. Aller sur Spring Initializr.  
2. Créer un projet avec notamment :
   - Group : `com.example`
   - Artifact / Name : `eureka-server`
   - Packaging : Jar
   - Java : 17
   - Dépendance : **Eureka Server**
3. Générer et importer le projet dans l’IDE.

### 3.2. Configuration de l’application

1. Configurer le fichier `application.yml` pour :
   - Fixer le port du serveur à 8761.  
   - Désactiver l’enregistrement du client Eureka pour ce projet (le serveur ne s’enregistre pas lui-même).  
   - Désactiver la récupération du registre (le serveur est lui-même le registre).  
   - Réduire la verbosité des logs d’Eureka.

### 3.3. Classe principale

1. Vérifier que la classe principale (par exemple `EurekaServerApplication`) :
   - Est annotée avec `@SpringBootApplication`.  
   - Est annotée avec `@EnableEurekaServer` pour activer les fonctionnalités de serveur Eureka.

### 3.4. Démarrage et vérification

1. Lancer l’application.  
2. Ouvrir un navigateur à l’adresse : http://localhost:8761  
3. Vérifier l’affichage du tableau de bord Eureka.  
   - Au départ, il est normal de voir “No instances available”.




<img width="1919" height="1016" alt="image" src="https://github.com/user-attachments/assets/0adf73e6-feec-4695-ac43-93b102434273" />



---

## 4. Microservice SERVICE-CLIENT

Le microservice **SERVICE-CLIENT** gère les clients (nom, âge, etc.) et expose des endpoints REST.

### 4.1. Création du projet

1. Créer un projet Spring Boot via Spring Initializr avec notamment :
   - Group : `com.example`
   - Artifact / Name : `client-service`
   - Dépendances : Spring Web, Spring Data JPA, MySQL Driver, Eureka Discovery Client, Lombok



<img width="609" height="954" alt="image" src="https://github.com/user-attachments/assets/27468ed6-f7df-480a-8c0b-77cc264080af" />

### 4.2. Configuration de la base de données et d’Eureka

Dans le fichier `application.yml` :

1. Définir :
   - Le port de l’application à 8081.  
   - Le nom de l’application à `SERVICE-CLIENT` (nom qui apparaîtra dans Eureka).  
2. Configurer la data source MySQL :
   - URL pointant vers une base `clientservicedb` (création automatique si nécessaire).  
   - Identifiants MySQL (user, mot de passe).  
3. Configurer JPA :
   - Stratégie de création/mise à jour du schéma (par exemple mise à jour automatique).  
   - Affichage des requêtes SQL dans la console.  
4. Configurer Eureka :
   - URL du serveur Eureka (http://localhost:8761/eureka/).  
   - Utilisation de l’adresse IP pour l’enregistrement (option de confort).



<img width="1271" height="418" alt="image" src="https://github.com/user-attachments/assets/fb261093-c405-4267-b8e4-60c31bd1b8b2" />



### 4.3. Création de l’entité Client

1. Créer un package `com.example.client.entities`.  
2. Créer une classe `Client` :
   - Annotée comme entité JPA.  
   - Ayant une clé primaire de type `Long` auto-générée.  
   - Avec les attributs nécessaires (par exemple : id, nom, âge).  
   - Utiliser Lombok pour générer les getters/setters et constructeurs.

### 4.4. Création du repository JPA

1. Créer un package `com.example.client.repositories`.  
2. Créer une interface `ClientRepository` :
   - Étendre `JpaRepository` pour l’entité `Client` et la clé primaire `Long`.  
   - Optionnel : définir des méthodes de recherche personnalisées si besoin.

### 4.5. Création de la couche service

1. Créer un package `com.example.client.services`.  
2. Créer une classe `ClientService` :
   - Annotée en tant que service Spring.  
   - Injecter `ClientRepository`.  
   - Fournir des méthodes pour :
     - Récupérer tous les clients.  
     - Récupérer un client par identifiant (avec gestion d’erreur si non trouvé).  
     - Ajouter ou mettre à jour un client.

### 4.6. Création du contrôleur REST

1. Créer un package `com.example.client.controllers`.  
2. Créer une classe `ClientController` :
   - Annotée en tant que contrôleur REST.  
   - Définir un préfixe pour les endpoints (par exemple `api/client`).  
   - Exposer :
     - Un endpoint GET pour récupérer tous les clients.  
     - Un endpoint GET avec identifiant pour récupérer un client spécifique.  
     - Un endpoint POST pour ajouter un nouveau client.  
   - Utiliser `ResponseEntity` pour retourner les réponses et les codes HTTP appropriés.

### 4.7. Enregistrement auprès d’Eureka

1. Dans la classe principale (par exemple `ClientServiceApplication`) :
   - Annoter avec `@SpringBootApplication`.  
   - Annoter avec `@EnableEurekaClient` pour activer l’enregistrement dans Eureka.




<img width="607" height="904" alt="image" src="https://github.com/user-attachments/assets/240097f5-e19a-4978-9147-478c9ff0994d" />

---

## 5. Service Gateway (Spring Cloud Gateway)

La **Gateway** sert de point d’entrée unique, en se basant sur Eureka pour découvrir les services.



<img width="647" height="911" alt="image" src="https://github.com/user-attachments/assets/2afefbb9-04d4-4a3d-a32c-1202456a5c80" />

### 5.1. Dépendances

1. Créer un projet Spring Boot ou ajouter dans un projet existant :
   - Starter Web Spring Boot.  
   - Starter Spring Cloud Gateway.  
   - Starter Eureka Client.  
2. Configurer la gestion de version Spring Cloud (dependency management ou équivalent).

### 5.2. Configuration de l’application Gateway

Dans `application.yml` :

1. Définir :
   - Le port de la Gateway à 8888.  
   - Le nom de l’application (par exemple `Gateway`).  
2. Activer :
   - La découverte de services Spring Cloud.  
   - Le routage dynamique basé sur les services découverts via Eureka.  
   - L’utilisation de noms de services en minuscules pour les routes, si souhaité.  
3. Configurer Eureka :
   - Définir l’hôte (localhost).  
   - Définir l’URL du serveur Eureka.

### 5.3. Classe principale Gateway

1. Vérifier que la classe principale (par exemple `GatewayApplication`) :
   - Est annotée avec `@SpringBootApplication`.  
   - Déclare un bean permettant de créer des routes dynamiques à partir du Discovery Client (routage automatique vers les services enregistrés dans Eureka).

### 5.4. Démarrage et test

1. Démarrer la Gateway.  
2. Tester l’accès au service client via la Gateway, par exemple :
   - Récupérer tous les clients via :  
     `GET http://localhost:8888/SERVICE-CLIENT/api/client`

---

## 6. Microservice SERVICE-CAR (Service Voiture)

Le microservice **SERVICE-CAR** gère les voitures et récupère également les informations du client propriétaire via le microservice client, en passant par la Gateway.



<img width="616" height="958" alt="image" src="https://github.com/user-attachments/assets/d5d04b8d-dfd2-4962-bca6-e8e552970c39" />

### 6.1. Configuration de la base de données et d’Eureka

1. Créer un projet Spring Boot pour le service voiture, avec les dépendances nécessaires :
   - Spring Web, Spring Data JPA, MySQL Driver, Eureka Discovery Client, Lombok.  
2. Dans `application.yml` :
   - Définir le port de l’application à 8082.  
   - Donner au service le nom `SERVICE-CAR` (c’est le nom qui apparaîtra dans Eureka).  
   - Configurer la data source MySQL pour une base `carservicedb` (création automatique si nécessaire).  
   - Configurer JPA pour la création/mise à jour du schéma et l’affichage des requêtes SQL.  
   - Configurer Eureka avec l’URL du serveur et l’option d’utilisation de l’adresse IP.





<img width="1279" height="322" alt="image" src="https://github.com/user-attachments/assets/c950c1e2-dd99-4eff-b166-b9c9d8dae857" />

### 6.2. Entités et modèles

1. Créer une entité `Car` :
   - Dans un package dédié (par exemple `com.example.car.entities`).  
   - Avec un identifiant auto-généré.  
   - Avec des attributs décrivant la voiture (marque, modèle, plaque d’immatriculation, identifiant du client propriétaire…).  
   - Utiliser Lombok pour les getters/setters et constructeurs.
2. Créer une classe modèle `Client` (DTO) :
   - Dans un package de modèles (par exemple `com.example.car.models`).  
   - Cette classe représente les données du client reçues depuis le service client.  
   - Ce n’est pas une entité JPA, seulement un objet de transfert de données.

### 6.3. Repository

1. Créer un repository `CarRepository` :
   - Dans un package (par exemple `com.example.car.repositories`).  
   - Étendre `JpaRepository` pour l’entité `Car`.  
   - Ajouter éventuellement des méthodes personnalisées de recherche (par exemple par identifiant client).

### 6.4. Modèle de réponse CarResponse

1. Créer un modèle `CarResponse` :
   - Dans le package `com.example.car.models`.  
   - Contenant les informations de la voiture et un objet `Client` complet.  
   - Utiliser Lombok pour générer les méthodes utilitaires et éventuellement le pattern Builder afin de construire les réponses de manière lisible.

### 6.5. Configuration de la communication inter-services

1. Dans la classe principale du service voiture (par exemple `CarApplication`) :
   - Annoter avec `@SpringBootApplication`.  
   - Annoter avec `@EnableDiscoveryClient` pour l’enregistrement dans Eureka.  
2. Déclarer un bean de type `RestTemplate` :
   - Configurer des timeouts de connexion et de lecture pour éviter les blocages si un service distant est indisponible.  
   - Ce `RestTemplate` sera utilisé pour appeler le service client via la Gateway.

### 6.6. Création de la couche service (CarService)

1. Créer une classe `CarService` :
   - Dans un package (par exemple `com.example.car.services`).  
   - Annoter comme service Spring.  
   - Injecter `CarRepository` et le `RestTemplate`.  
2. Dans cette classe :
   - Définir une URL cible pour le service client via la Gateway, par exemple pointant vers `SERVICE-CLIENT/api/client`.  
   - Implémenter une méthode pour récupérer toutes les voitures :
     - Lire toutes les entités `Car` depuis la base.  
     - Pour chacune, appeler le service client via le `RestTemplate` pour récupérer les informations du client associé.  
     - Construire une liste de `CarResponse`.  
   - Implémenter une méthode pour récupérer une voiture par identifiant :
     - Chercher la voiture en base.  
     - Récupérer le client associé via le service client.  
     - Construire un objet `CarResponse`.  
   - Gérer les erreurs de communication avec le service client (par exemple en laissant le client à null si l’appel échoue, tout en retournant quand même les informations de la voiture).

### 6.7. Création du contrôleur REST pour SERVICE-CAR

1. Créer une classe `CarController` :
   - Dans un package (par exemple `com.example.car.controllers`).  
   - Annoter comme contrôleur REST.  
   - Définir un préfixe pour les endpoints (par exemple `api/car`).  
   - Exposer :
     - Un endpoint GET pour récupérer toutes les voitures avec leurs clients.  
     - Un endpoint GET avec identifiant pour récupérer une voiture spécifique avec son client.  
   - Utiliser `ResponseEntity` pour la gestion des réponses et des codes d’erreur (par exemple, code 404 si la voiture n’est pas trouvée).

### 6.8. Démarrage et vérification dans Eureka

1. Démarrer le service voiture (classe principale `CarApplication`).  
2. Accéder au tableau de bord Eureka : http://localhost:8761  
3. Vérifier que le service `SERVICE-CAR` apparaît dans la liste des applications enregistrées.



<img width="516" height="691" alt="image" src="https://github.com/user-attachments/assets/ba6e9e76-15fa-468c-924d-40cf056945ad" />

---

## 7. Ordre de démarrage global et tests

### 7.1. Ordre de démarrage recommandé

1. Démarrer MySQL.  
2. Démarrer **Eureka Server**.  
3. Démarrer **SERVICE-CLIENT**.  
4. Démarrer **SERVICE-CAR**.  
5. Démarrer la **Gateway**.

### 7.2. Vérifications dans Eureka

- Accéder à http://localhost:8761  
- Vérifier la présence de :
  - `SERVICE-CLIENT`  
  - `SERVICE-CAR`  
  - `Gateway` (si enregistré)


<img width="1919" height="1015" alt="image" src="https://github.com/user-attachments/assets/0529af9e-95bb-40b8-a320-85bb1229c52c" />



### 7.3. Tests des endpoints

#### 7.3.1. Tests directs (sans Gateway)

- SERVICE-CLIENT :
  - Récupérer tous les clients : GET sur le port 8081 avec le chemin des clients.  
  - Récupérer un client par identifiant : GET avec l’identifiant.  
  - Ajouter un client : POST avec un corps JSON décrivant le client.

- SERVICE-CAR :
  - Récupérer toutes les voitures : GET sur le port 8082 avec le chemin des voitures.  
  - Récupérer une voiture par identifiant : GET avec l’identifiant.

#### 7.3.2. Tests via la Gateway

- SERVICE-CLIENT via Gateway :  
  - GET sur `http://localhost:8888/SERVICE-CLIENT/api/client`  
- SERVICE-CAR via Gateway :  
  - GET sur `http://localhost:8888/SERVICE-CAR/api/car`  

Flux pour une requête via Gateway :

1. La requête arrive sur la Gateway (port 8888).  
2. La Gateway lit le nom du service dans l’URL (par exemple `SERVICE-CLIENT` ou `SERVICE-CAR`).  
3. Elle interroge Eureka pour connaître l’adresse du service cible.  
4. Elle transfère la requête au microservice approprié.  
5. Elle renvoie la réponse au client.

---

## 8. Problèmes fréquents

- **Service non visible dans Eureka :**
  - Vérifier le nom de l’application (`spring.application.name`).  
  - Vérifier l’URL de Eureka (`defaultZone`).  
  - Vérifier que Eureka est bien démarré.

- **Erreur de connexion MySQL :**
  - Vérifier l’URL, l’utilisateur et le mot de passe.  
  - Vérifier que MySQL tourne sur le port attendu.

- **Lombok ne génère rien :**
  - Activer l’annotation processing dans l’IDE.  
  - Vérifier la présence de la dépendance Lombok.

- **Appels inter-services qui échouent :**
  - Vérifier les URLs configurées (surtout celles de la Gateway).  
  - Vérifier que tous les services sont démarrés.  
  - Vérifier les ports et les noms des services.

---

## 9. Extensions possibles

- **Sécurité** : ajout de Spring Security et OAuth2 pour sécuriser les API.  
- **Résilience** : utilisation de Circuit Breaker (Resilience4j, Hystrix).  
- **Monitoring** : Spring Boot Actuator, Prometheus, Grafana.  
- **Traçabilité** : Spring Cloud Sleuth, Zipkin.  
- **Configuration centralisée** : Spring Cloud Config.  
- **Documentation API** : Swagger / OpenAPI.

---

## 10. Points clés à retenir

- Chaque microservice dispose de **sa propre base de données**.  
- La **communication** entre services se fait via des **API REST**.  
- Les services sont **découverts dynamiquement** via Eureka.  
- La **Gateway** joue le rôle de **point d’entrée unique**.  
- Les configurations sont externalisées dans des fichiers `application.yml`.  
- La **gestion des erreurs** et des timeouts est importante pour la résilience.  

Cette architecture constitue une base solide pour construire des systèmes distribués, évolutifs et maintenables.

