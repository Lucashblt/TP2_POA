# TP2 - Question 3 : Architecture Microservices

**Groupe:** [Lucas Hublart - Tom Douaud - Mathieu Docher]

## 🚀 Démarrage 

1. **Compiler** : `compile.bat`
2. **Lancer tous les services** : `start-all-services.bat`
3. **Tester** : `test-services.bat`

### Architecture technique

- **Langage** : Java (sans framework lourd pour simplifier)
- **Communication** : API REST (HTTP)
- **Serveur web** : HttpServer de Java (com.sun.net.httpserver)
- **Format de données** : JSON
- **Stockage** : En mémoire (HashMap)

### Fonctionnalités

#### **1. Service Utilisateur (UserService - Port 8081)**
- Créer un utilisateur
- Lister tous les utilisateurs
- Récupérer un utilisateur par ID

#### **2. Service Produit (ProductService - Port 8082)**
- Créer un produit
- Lister tous les produits
- Récupérer un produit par ID

#### **3. Service Commande (OrderService - Port 8083)**
- Créer une commande (appelle UserService et ProductService)
- Lister toutes les commandes avec détails enrichis
- Récupérer une commande par ID avec détails complets

## Instructions d'exécution

### Prérequis
- **Java JDK 11 ou supérieur** installé
- Variable d'environnement `JAVA_HOME` configurée

### Compilation

Ouvrez un terminal PowerShell dans le dossier du projet et exécutez :

```powershell
.\compile.bat
```

Cela compile tous les microservices.

### Lancement des services

Lancez chaque service dans un terminal séparé dans l'ordre suivant ou executer la commande :

#### Lancer tous les services depuis le même terminal :

```powershell
.\start-all-service.bat
```

### ou lancer chaque service dans un terminal séparé : 

#### Terminal 1 - Service Utilisateur :
```powershell
.\run-user-service.bat
```

#### Terminal 2 - Service Produit :
```powershell
.\run-product-service.bat
```

#### Terminal 3 - Service Commande :
```powershell
.\run-order-service.bat
```

### Test des services

Une fois tous les services lancés, exécutez le script de test dans un nouveau terminal :

```powershell
.\test-services.bat
```

Ce script va :
1. Créer des utilisateurs
2. Créer des produits
3. Créer des commandes
4. Afficher tous les résultats