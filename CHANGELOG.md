## Changements mode solo

### Fonctionnalité de shuffle des cases du plateau

Lors de la soutenance bêta la démonstration du plateau était un plateau fixe, c'est-à-dire qu'on place manuellement les cases pour former un plateau.

Désormais à chaque partie, le plateau sera différent. Dans le code, on indiquera uniquement le nombre type de cases qui seront placés aléatoirement sur le plateau.

#### Avant :

```kotlin
val boardActions = List(25) { index ->
    when (index) {
        3, 13, 15 -> CaseAction.MoveForward2
        5, 7, 16, 10 -> CaseAction.MoveBackward3
        8, 6, 18 -> CaseAction.CompassMiniGame
        12, 4, 19, 1 -> CaseAction.RevealTile
        9, 2, 20 -> CaseAction.BalloonMiniGame
        else -> CaseAction.Nothing
    }
}
```

#### Après :

```kotlin
val boardActions: List<CaseAction> by lazy {
    val actions = mutableListOf<CaseAction>().apply {
        addAll(List(3) { CaseAction.MoveForward2 })
        addAll(List(4) { CaseAction.MoveBackward3 })
        addAll(List(3) { CaseAction.CompassMiniGame })
        addAll(List(4) { CaseAction.RevealTile })
        addAll(List(3) { CaseAction.BalloonMiniGame })
        addAll(List(8) { CaseAction.Nothing })
    }
    actions.shuffled(Random.Default)
}
```

### Ajout de nouveaux images et mots à deviner

A la soutenance bêta, on avait que 5 images embarqués pour le mode solo. Désormais on en a 10. Les images s'ajoutent dans une liste de pair qui associe une image à un mot.

```kotlin
val imagesEtMots = listOf(
    R.drawable.pomme to "pomme",
    R.drawable.voiture to "voiture",
    R.drawable.maison to "maison",
    R.drawable.imprimante to "imprimante",
    R.drawable.chat to "chat",
    R.drawable.chien to "chien",
    R.drawable.manette to "manette",
    R.drawable.micro to "micro",
    R.drawable.livre to "livre",
    R.drawable.valise to "valise"
)
```

### Correction de bug des boutons lors d'une transition

Lors d'un changement d'une page à une autre, pendant l'animation de transition on pouvait cliquer sur les boutons ce qui conduit à des mauvais comportement sur l'application. Désormais, le bouton se bloque, c'est à dire qu'il n'est plus clickable pendant une transition de page avec `Button(enabled = false)`.

## Changements mode multijoueur

### Gestion du lobby

- **Ajout de la possibilité de quitter un lobby** pour les joueurs.
- **Ajout de la possibilité d’annuler une partie** (par l’admin) avant son lancement.

Ces actions sont accessibles depuis l'écran `LobbyScreen` et déclenchent l’envoi de messages WebSocket au backend, suivi d’un retour vers l’écran d’accueil ou une mise à jour des joueurs connectés.

---

### Plateau & déplacement

- **Affichage du plateau de jeu** côté client **fonctionnel en mode multijoueur**.
- **Lancer de dé** implémenté mais non fonctionnel:
  - Envoi de la requête HTTP `/rolldice`.
  - Affichage dynamique du résultat sur le dé.
- **Comportement partiel des cases spéciales** :
  - Certaines cases déclenchent un effet (avancer ou reculer).
  - **Comportement encore instable ou incomplet** (bugs connus, manque de temps pour finaliser).

---

## Fonctionnalités non implémentées (par manque de temps)

- **Mini-jeux** : non réalisés.
- Gestion complète des effets des cases (autres que avancer/reculer).
- Séquences d'animation ou transitions interactives après un lancer de dé.

