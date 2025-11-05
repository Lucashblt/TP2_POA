# Architecture Microservices - Documentation Technique

## 📋 Vue d'ensemble

Ce projet implémente une architecture microservices simplifiée pour un système de gestion de commandes e-commerce.

## 🏗️ Architecture du système

```
                    ┌──────────────┐
                    │   Client     │
                    │ (Navigateur) │
                    └──────┬───────┘
                           │
                           │ HTTP/REST
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ Microservice  │  │ Microservice  │  │ Microservice  │
│  UTILISATEUR  │  │   PRODUIT     │  │   COMMANDE    │
│               │  │               │  │               │
│  Port: 8081   │  │  Port: 8082   │  │  Port: 8083   │
│               │  │               │  │               │
│  ┌─────────┐  │  │  ┌─────────┐  │  │  ┌─────────┐  │
│  │   DB    │  │  │  │   DB    │  │  │  │   DB    │  │
│  │ Users   │  │  │  │Products │  │  │  │ Orders  │  │
│  └─────────┘  │  │  └─────────┘  │  │  └─────────┘  │
└───────────────┘  └───────────────┘  └───────┬───────┘
                                               │
                           Communication API   │
                    ┌──────────────────────────┘
                    │
         Appelle les autres microservices
         pour obtenir les données nécessaires
```

## 🎯 Les 3 Microservices

### 1. UserService (Port 8081)
**Responsabilité** : Gestion des utilisateurs

**Endpoints** :
- `GET /users` - Liste tous les utilisateurs
- `GET /users/{id}` - Récupère un utilisateur par ID
- `POST /users` - Crée un nouvel utilisateur

**Données** :
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "email": "jean.dupont@email.com"
}
```

### 2. ProductService (Port 8082)
**Responsabilité** : Gestion des produits

**Endpoints** :
- `GET /products` - Liste tous les produits
- `GET /products/{id}` - Récupère un produit par ID
- `POST /products` - Crée un nouveau produit

**Données** :
```json
{
  "id": 1,
  "nom": "Ordinateur portable",
  "prix": 999.99,
  "stock": 10
}
```

### 3. OrderService (Port 8083)
**Responsabilité** : Gestion des commandes + Communication inter-services

**Endpoints** :
- `GET /orders` - Liste toutes les commandes (enrichies)
- `GET /orders/{id}` - Récupère une commande par ID (enrichie)
- `POST /orders` - Crée une nouvelle commande

**Données enrichies** :
```json
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "status": "Confirmée",
  "user": {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean.dupont@email.com"
  },
  "product": {
    "id": 1,
    "nom": "Ordinateur portable",
    "prix": 999.99,
    "stock": 10
  }
}
```

## 🔄 Communication Inter-Services

Le service OrderService communique avec les autres services :

```
1. Client → OrderService (POST /orders)
   Body: {"userId":"1","productId":"1","quantity":"2"}

2. OrderService → UserService (GET /users/1)
   Vérifie que l'utilisateur existe

3. OrderService → ProductService (GET /products/1)
   Vérifie que le produit existe

4. OrderService crée la commande en mémoire

5. OrderService enrichit la réponse avec les détails complets

6. OrderService → Client (Response 201 Created)
   Body: Commande enrichie avec user et product complets
```

## 💡 Concepts Microservices Démontrés

### ✅ Indépendance
- Chaque service fonctionne sur son propre port
- Déploiement indépendant possible
- Scalabilité granulaire

### ✅ Communication REST
- API HTTP/JSON standardisée
- Indépendant du langage de programmation
- Facile à tester et documenter

### ✅ Faible Couplage
- Les services ne se connaissent que par leurs API
- Pas de dépendances directes au code
- Changements isolés

### ✅ Responsabilité Unique
- Chaque service gère un domaine métier
- Base de données isolée (HashMap en mémoire)
- Code modulaire et maintenable

### ✅ Enrichissement de Données
- OrderService agrège les données
- Pas de duplication d'informations
- Données toujours à jour

## 🛠️ Technologies Utilisées

- **Java 11+** : Langage de programmation
- **HttpServer** : Serveur HTTP natif Java (com.sun.net.httpserver)
- **JSON** : Format d'échange de données
- **HashMap** : Stockage en mémoire
- **HttpURLConnection** : Client HTTP pour communication inter-services

## 📊 Structure du Projet

```
Q3/
├── src/
│   ├── User.java              # Modèle Utilisateur
│   ├── Product.java           # Modèle Produit
│   ├── Order.java             # Modèle Commande
│   ├── UserService.java       # Microservice Utilisateur
│   ├── ProductService.java    # Microservice Produit
│   └── OrderService.java      # Microservice Commande
├── compile.bat                # Script de compilation
├── run-user-service.bat       # Lancer UserService
├── run-product-service.bat    # Lancer ProductService
├── run-order-service.bat      # Lancer OrderService
├── start-all-services.bat     # Lancer tous les services
├── test-services.bat          # Script de test automatisé
└── README.md                  # Documentation principale
```

## 🎓 Avantages de cette Architecture

1. **Scalabilité** : Chaque service peut être scalé indépendamment
2. **Résilience** : Une panne d'un service n'affecte pas les autres
3. **Déploiement** : Mise à jour d'un service sans redéployer tout
4. **Technologie** : Chaque service peut utiliser une techno différente
5. **Équipes** : Équipes autonomes par service
6. **Maintenance** : Code plus petit, plus facile à comprendre

## ⚠️ Inconvénients

1. **Complexité** : Plus de services à gérer
2. **Réseau** : Latence des appels HTTP
3. **Cohérence** : Pas de transactions ACID entre services
4. **Tests** : Tests d'intégration plus complexes
5. **Déploiement** : Infrastructure plus complexe

## 🚀 Pour aller plus loin

Dans une application réelle, on ajouterait :
- **API Gateway** : Point d'entrée unique
- **Service Discovery** : Registre de services (Eureka, Consul)
- **Load Balancing** : Distribution des requêtes
- **Circuit Breaker** : Gestion des pannes (Hystrix, Resilience4j)
- **Base de données** : PostgreSQL, MongoDB par service
- **Containerisation** : Docker
- **Orchestration** : Kubernetes
- **Monitoring** : ELK Stack, Prometheus, Grafana
- **Authentification** : OAuth2, JWT

## 📚 Références

- [Martin Fowler - Microservices](https://martinfowler.com/articles/microservices.html)
- [SOA vs Microservices](https://nexworld.fr/soa-versus-microservices-quelles-differences/)
