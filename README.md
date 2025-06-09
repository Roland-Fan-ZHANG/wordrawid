# Wordrawid: Jeu de plateau solo et mini-jeux

## Présentation Mode Solo

Wordrawid est un jeu mobile de plateau solo, incluant des mini-jeux intégrés. Le joueur avance sur un plateau de 5x5 cases et effectue des actions spécifiques en fonction des cases.
Il devine des mots à partir d'images et joue des mini-jeux pour débloquer les cases.

## Fonctionnalités principales

* Plateau de jeu 5x5 avec cases numérotées et masquées.
* Avancement sur le plateau en lançant un dé virtuel.
* Actions variées sur les cases : avancer, reculer, mini-jeux, révéler une case, etc.
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
* `SoloScreenUI.kt` : agencement du plateau et des composants du jeu.

### 4. **Mini-jeux**

* `BalloonGameScreen.kt` : mini-jeu utilisant le micro pour gonfler un ballon.
* `CompassGameScreen.kt` : mini-jeu utilisant l’accéléromètre pour orienter l’appareil.

### 5. **Utilitaires**

* `DiceUtils.kt` : fonctions et composants liés au dé virtuel.
* `ImagesEtMots.kt` et `ImageUtils.kt` : gestion des images et mots à deviner.
* `SoloScreenUtils.kt` : logique de jeu (actions, mini-jeux, mouvements).
* `SoloViewModel.kt` : `ViewModel` qui gère l’état global du jeu.

### 6. **Gestion des autorisations**

* Permissions audio pour `BalloonGameScreen.kt`.
* Gestion de l’orientation (portrait) pour le jeu et les mini-jeux.

## Développement et contribution

### Structure

* Chaque fonctionnalité (écran, mini-jeu, composants) est isolée dans son propre fichier.
* La navigation est centralisée (`AppNavGraph.kt`).
* `SoloViewModel` gère l’état et les règles du plateau.

### Étendre le projet

* **Ajouter un mini-jeu** :

    1. Créer un nouvel écran mini-jeu avec `@Composable`.
    2. Ajouter une action correspondante dans `CaseAction.kt`.
    3. Étendre la logique dans `handleAction` (`SoloScreenUtils.kt`).
    4. Ajouter un `composable` dans `AppNavGraph.kt` et la route dans `Routes.kt`.
* **Ajouter des images/mots** :

    * Étendre la liste `imagesEtMots` dans `ImagesEtMots.kt`.

## Utilisation pour les joueurs

1. Lancer le jeu, choisir **Solo**.
2. Lancer le dé pour avancer sur le plateau.
3. Suivre les actions des cases : mini-jeux ou révélations.
4. Deviner le mot en utilisant la zone de saisie.
5. Terminer la partie en devinant correctement le mot.

## Présentation Mode Multi
