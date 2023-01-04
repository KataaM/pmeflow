# Pmeflow

Ce dépôt contient l'application Pmeflow.

La convention de nommage des enregistrements est la suivante:


	{global; nom}-{admin; service}-{api;ui;admin}-{nom du service}


Exemple:

*   global-service-api-redis
*   referentiel-service-api-formulaire
*   pmeflow-service-ui-dashboard

---
## Installation des dépendances pour le back-end

### Pré-requis
Avoir installé [Docker-Desktop](https://www.docker.com/products/docker-desktop/) et [WSL 2](https://wslstorestorage.blob.core.windows.net/wslblob/wsl_update_x64.msi) (Docker Desktop vous demande généralement d'installer WSL 2)

### Récupérer les images docker pour les services RabbitMQ et RediSearch
#### Pull de l'image docker RabbitMQ
Pour l'image RabbitMQ il faut copier-coller la commande ci-dessous dans un terminal PowerShell
>    `docker rm -f pmeflow-rabbitmq; docker run -d --name="pmeflow-rabbitmq" "-eRABBITMQ_DEFAULT_USER=admin" "-eRABBITMQ_DEFAULT_PASS=123AZErty." -p 16000:15672 -p 16001:5672 rabbitmq:3-management`

#### Pull de l'image docker RediSearch
Pour l'image RediSearch il faut copier-coller la commande ci-dessous dans un terminal PowerShell **EN REMPLACANT LE PATH EN GRAS PAR LE VOTRE JUSQU'AU DOSSIER _data du projet git pmeflow**
>    `docker rm -f pmeflow-redis; docker run -d --name pmeflow-redis -p 6380:6379 -v ` **C:\votre\chemin\vers\le\dossier\_data**`:/data redislabs/redisearch:latest`


---
## Procédure de debug dans Eclipse 

### Exemple pour grc-core

#### Exécution du code

*   sélectionner le menu Run,
*   cliquer sur "Run configurations"
*   créer une nouvelle Gradle Config pour chaque projet runnable
    
      - name: grc-core - quarkusDev
      - ajouter deux tasks : 'clean' puis 'quarkusDev'
      - workspace: ${workspace_loc:/pmeflow-core}

#### Débogage du code

*   sélectionner le menu Debug,
*   cliquer sur "Debug configurations"
*   créer une nouvelle remote Java Application pour chaque projet runnable
    
     - name: grc-core - debug
     - host: localhost
     - port: 5005 (ou le port affiché par Quarkus dans la console)

---

## Dépendances

Sans objet


## Variable SYSTEME

*   **redis.uri**: adresse uri du service REDIS, ex: tcp://<ip>:6380


## Utilisateur nécessaire

Sans objet


## Profil accepté par défaut

Sans objet


## Rôles disponibles pour le(s) service(s)

Sans objet


## Fonctions d'administration

Sans objet
     

## Fonctions batch

Sans objet


## Storage
* Service RediSearch : pmeflow-redis

---
## **VueJS**

### *Installation de l'IDE*
0. Installer [Node.js](https://nodejs.org/en/download/)
1. Installer [VSCODE](https://code.visualstudio.com/download)
2. Ouvrir le projet "private-ui" dans VsCode
3. Installer les extensions : 
      - Babel JavaScript (mgmcdermott.vscode-language-babel)
      - ESLint (dbaeumer.vscode-eslint)
      - Vue Language Features (Vue.volar)
4. Ouvrir un terminal et se déplacer dans le projet private-ui
5. Lancer les commandes dans l'ordre

>    `npm install`

>    `npm run serve`

6. **Le projet est lancé ici** : http://localhost:8080/

### *Compréhension du projet*

Le départ du projet est le App.vue ainsi que le main.js

Dans App.vue on retrouve template que vous avez normalement vu lors du turoriel VueJS.

 
>   `<v-app class="pmeflow">` définie le nom de notre app

>   `<v-overlay>` définie l'overlay de notre app ici pour le loading : https://vuetifyjs.com/en/components/overlays/

>   `<menu-principal />` définie est un composent qui définie la bar de navigation latéral de notre page 
    
>   `<topbar />` définie la bar de navigation au top de notre page

>   `<v-navigation-drawer` ... définie la nav bar vertical sur la gauche de notre page 

A savoir que nous utilisons le framework vuetify https://vuetifyjs.com/en/
Qui nous sert à créer les menus horizontaux / verticaux et tout autre petit composant.

**Une autre chose importante** est le **`router.js`** qui permet de créer un lien entre nos pages et donc nos views.
Chaque views est composé de composents.    

7. Run the docker container

Création de l'image docker :

>   `docker build -t katam/private-ui .`

Vérification que l'image a bien été crée :

>   `docker images`

lancement de l'image docker sur le port 8080 de la machine (remplacer le premier port si vous voulez que ça run sur un autre port) :

>   `docker run -p 8080:8080 -d katam/private-ui`

On pourra aussi imaginer de lancer toutes les applications en même temps avec le docker compose :

> `docker-compose up`