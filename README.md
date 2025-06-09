# Wordrawid: Jeu de plateau solo et multi et mini-jeux

## Présentation Mode Solo

Le mode solo inclue des mini-jeux intégrés. Le joueur avance sur un plateau de 5x5 cases et effectue des actions spécifiques en fonction des cases.
Il devine des mots à partir d'images et joue des mini-jeux pour débloquer les cases.

## Fonctionnalités principales

* Plateau de jeu 5x5 avec cases numérotées et masquées.
* Avancement sur le plateau en lançant un dé virtuel.
* Actions variées sur les cases : avancer, reculer, mini-jeux, révéler une case et ne rien faire.
* Mini-jeux :

    * **Balloon** : souffler dans le micro pour gonfler un ballon.
    * **Compass** : orienter le téléphone dans plusieurs sens aléatoire.
* Deviner le mot associé à l’image pour remporter la partie.

## Architecture technique

### 1. **Navigation**

* `AppNavGraph.kt` : gère la navigation entre les écrans (`MenuScreen`, `SoloScreen`, mini-jeux, écran de victoire).
* `Routes.kt` : contient les routes de navigation pour chaque écran.

### 2. **Écrans principaux**

* `MenuScreen.kt` : écran d'accueil avec navigation vers les modes.
* `SoloScreen.kt` : écran principal du mode solo, avec plateau, mini-jeux et zone de saisie du mot.
* `WinScreen.kt` : écran final de victoire.

### 3. **Composants du plateau (Solo)**

* `BoardGrid.kt` : composant d'affichage des cases du plateau (masquées ou révélées).
* `Player.kt` : composant d’affichage et d’animation de la position du joueur.
* `BoardRules.kt` et `CaseAction.kt` : logique des actions associées aux cases.
* `SoloScreenUI.kt` : L'interface graphique du plateau et des composants du jeu.

### 4. **Mini-jeux**

* `BalloonGameScreen.kt` : mini-jeu utilisant le micro pour gonfler un ballon.
* `CompassGameScreen.kt` : mini-jeu utilisant l’accéléromètre pour orienter le téléphone.

### 5. **Utilitaires**

* `DiceUtils.kt` : fonctions et composants liés au dé virtuel.
* `ImagesEtMots.kt` et `ImageUtils.kt` : gestion des images et des mots à deviner.
* `SoloScreenUtils.kt` : logique de jeu (actions, mini-jeux, mouvements).
* `SoloViewModel.kt` : `ViewModel` qui gère l’état du jeu et les différents changements.

### 6. **Gestion des autorisations**

* Permissions audio pour `BalloonGameScreen.kt`.
* Gestion de l’orientation (portrait) dans le mini-jeu boussole et sur le plateau.

## Développement et contribution

### Structure

* Chaque fonctionnalité (écran, mini-jeu, composants) est dans son propre fichier.
* La navigation est centralisée (`AppNavGraph.kt`).
* `SoloViewModel` gère l’état et les règles du plateau.

### Étendre le projet

* **Ajouter un mini-jeu** :

    1. Créer un nouvel écran mini-jeu avec `@Composable`.
    2. Ajouter une action correspondante dans `CaseAction.kt`.
    3. Ajouter l'action dans `handleAction` dans `SoloScreenUtils.kt`.
    4. Ajouter un `composable` dans `AppNavGraph.kt` et la route dans `Routes.kt`.
* **Ajouter des images/mots** :
    * Ajouter l'image dans /res/drawable
    * Étendre la liste `imagesEtMots` dans `ImagesEtMots.kt`.

## Utilisation pour les joueurs

1. Lancer le jeu, choisir **Solo**.
2. Lancer le dé pour avancer sur le plateau.
3. Suivre les actions des cases : mini-jeux ou révélations.
4. Deviner le mot en utilisant la zone de saisie.
5. Terminer la partie en devinant correctement le mot.

## Présentation Mode Multi 

On ne mentionnera que les fonctionnalités qui ont été implémentées à ce jour.

Le mode multijoueur permet à plusieurs joueurs de rejoindre une partie partagée via un code de lobby, de se synchroniser en temps réel et de jouer ensemble autour des mêmes images et mots à deviner.

### Fonctionnalités principales

- Création d’une partie avec un pseudo.
- Rejoint d’une partie avec un pseudo et un code de lobby.
- Communication en temps réel entre les joueurs (via STOMP/WebSocket).
- Notifications visuelles (snackbar et notifications système) pour les événements importants (nouveau joueur, début de partie).
- Gestion de l’état du jeu (lobby, démarrage, affichage des images de la partie).
- Affichage de l’image à deviner une fois la partie lancée.

### Écrans principaux

| Fichier                   | Rôle                                                         |
|----------------------------|--------------------------------------------------------------|
| **MultiScreen.kt**        | Choix entre créer ou rejoindre une partie.                   |
| **CreateGameScreen.kt**   | Interface de création de partie avec pseudo.                 |
| **JoinGameScreen.kt**     | Interface pour rejoindre une partie existante.               |
| **LobbyScreen.kt**        | Salle d’attente des joueurs avant de lancer la partie.       |
| **GameScreen.kt**         | Affiche l’image partagée à deviner en cours de partie.       |

### Fonctionnement général

1. **Création de partie**  
   L’utilisateur saisit un pseudo, puis envoie une requête HTTP (POST) à l’API `/api/lobby/create` pour créer un lobby.  
   La réponse contient l’ID du joueur, le code de la partie et l’ID de la partie.  
   Le joueur est alors automatiquement connecté au serveur WebSocket pour écouter les événements du lobby.

2. **Rejoindre une partie**  
   Un joueur peut rejoindre une partie existante en entrant son pseudo et le code de la partie.  
   Une requête HTTP (POST) est envoyée à l’API `/api/lobby/join`.  
   La réponse contient la liste des joueurs existants, puis le joueur est abonné aux événements WebSocket pour le lobby.

3. **Lobby**  
   Dans le lobby, les joueurs reçoivent en temps réel :
   - Les nouveaux joueurs qui rejoignent.
   - La notification de démarrage de la partie par l'admin.  
   L’admin a un bouton pour lancer la partie, ce qui envoie une requête HTTP `/api/lobby/start` et notifie tous les joueurs via WebSocket.

4. **Démarrage de la partie**  
   Lorsqu’une partie est démarrée, un message WebSocket est envoyé à tous les joueurs (type `START`).  
   Chaque joueur reçoit les informations du jeu, notamment l’URL de l’image à deviner.  
   L’image est téléchargée localement puis affichée dans l’**écran GameScreen**.

### Gestion des communications en temps réel

L’application utilise la bibliothèque **ua.naiksoftware.stomp** pour les WebSockets STOMP.  
Le gestionnaire central de la communication est `StompClientManager.kt`, qui :
- Initialise la connexion à l’URL WebSocket (`ws://10.0.2.2:8080/ws?playerId={id}`).
- S’abonne aux événements du lobby (`/topic/lobby/{joinCode}`) et du jeu (`/topic/game/{gameId}`).
- Gère l’arrivée des nouveaux joueurs, le lancement de la partie, et la réception des données du jeu.
- Télécharge les images de la partie en arrière-plan puis navigue automatiquement vers l’écran de jeu.

### Architecture

- **StompClientManager.kt** :  
  - Gère toute la communication en temps réel.  
  - Télécharge l’image finale (`downloadImage`) et déclenche la navigation automatique vers `GameScreen.kt`.

- **CreateGameRequest.kt, JoinGameRequest.kt, StartGameRequest.kt** :  
  - Définissent les requêtes HTTP pour créer, rejoindre et démarrer la partie.

- **LobbyMessage.kt, GameMessage.kt** :  
  - Modèles des messages WebSocket pour les communications.

- **Session.kt** :  
  - Représente l’état global de la partie : joueurs, plateau, etc.

### Notifications

- **Snackbars** :  
  - Affichent des messages contextuels (ex: “Partie créée”, “Joueur a rejoint”).

- **Notifications système** :  
  - Pour les événements majeurs (ex: un joueur rejoint, début de la partie).

