# TP1-DAR2026 — Les sockets TCP en Java

**Matière :** Développement d'applications réparties  
**Établissement :** Faculté des Sciences de Sfax — LSI 3  
**Année universitaire :** 2026–2027  
**TP :** TP 1 — Les sockets en mode connecté TCP  
**Activités :** 1-1 Première connexion Client/Serveur ; 1-2 Service de calculatrice

> **À personnaliser avant le dépôt :** ajoutez votre nom/prénom, la date, et remplacez tous les emplacements de captures d'écran par vos propres captures. Les résultats doivent correspondre à une exécution réelle sur votre ordinateur.

## 1. Objectifs

- Établir une communication client/serveur en Java avec des sockets TCP.
- Comprendre le rôle du serveur, du client, de l'adresse IP et du port.
- Envoyer un entier du client au serveur et récupérer une réponse.
- Réaliser le calcul `résultat = x × 5` côté serveur.
- Tester les valeurs positives, négatives et zéro.
- Fermer proprement les flux et les sockets.
- Versionner et publier le projet avec Git et GitHub.

## 2. Environnement

- JDK 8 ou version ultérieure.
- Un IDE Java : IntelliJ IDEA, Eclipse ou VS Code.
- Git.
- Deux consoles d'exécution : une pour le serveur et une pour le client.

### 2.1 Vérification de Git

Dans un terminal, exécuter :

```bash
git --version
```

Cette commande affiche la version de Git installée. Si la commande n'est pas reconnue, installez Git puis rouvrez le terminal.

**Capture à insérer :**

![Version de Git](screenshots/01-git-version.png)

`[INSÉRER ICI LA CAPTURE D'ÉCRAN DE git --version]`

### 2.2 Vérification de Java

Exécuter :

```bash
java --version
javac --version
```

`java` affiche la version du moteur d'exécution. `javac` vérifie la présence du compilateur du JDK. Le TP demande un JDK de version 8 ou supérieure.

**Capture à insérer :**

![Version de Java](screenshots/02-java-version.png)

`[INSÉRER ICI LA CAPTURE D'ÉCRAN DES VERSIONS JAVA]`

## 3. Organisation du projet

Le dépôt contient les deux classes Java et ce compte rendu :

```text
TP1-DAR2026/
├── src/
│   ├── serverPackage/
│   │   └── Server.java
│   └── clientPackage/
│       └── Client.java
├── screenshots/
│   └── (captures d'écran à ajouter)
├── .gitignore
└── README.md
```

**Capture à insérer :**

![Structure du projet](screenshots/03-project-structure.png)

`[INSÉRER ICI UNE CAPTURE DE L'ARBORESCENCE DU PROJET DANS L'IDE]`

## 4. Activité 1-1 — Première connexion Client/Serveur

### 4.1 Principe

TCP établit une connexion entre deux extrémités avant l'échange de données. Le serveur ouvre un `ServerSocket` sur le port `5000`, puis attend une demande de connexion avec `accept()`. Le client crée un `Socket` vers l'hôte et le port du serveur. Lorsque le client se connecte, `accept()` retourne un socket de communication.

- `ServerSocket` : écoute les nouvelles connexions.
- `Socket` : représente la connexion entre le client et le serveur.
- `localhost` : désigne l'ordinateur local.
- `5000` : port choisi par cette application pour communiquer.

### 4.2 Code du serveur

Le serveur crée le `ServerSocket`, affiche un message d'attente, puis bloque sur `accept()` jusqu'à ce qu'un client se connecte. Les blocs `try-with-resources` ferment automatiquement le socket et les flux à la fin de leur utilisation.

Dans la version finale, le serveur attend ensuite un entier. Si cet entier vaut zéro, il termine la boucle. Sinon, il calcule le produit par cinq et envoie le résultat au client.

Fichier : [`src/serverPackage/Server.java`](src/serverPackage/Server.java).

### 4.3 Code du client

Le client se connecte à `localhost:5000`. Après la connexion, il affiche un message de confirmation. Dans l'activité 1-2, il lit les entiers saisis au clavier, les envoie au serveur, puis attend et affiche le résultat renvoyé.

Fichier : [`src/clientPackage/Client.java`](src/clientPackage/Client.java).

### 4.4 Exécution de la première connexion

Lancer le serveur **en premier**. Il doit afficher un message indiquant qu'il attend une connexion. Ensuite, lancer le client : le serveur doit signaler qu'un client est connecté et le client doit afficher son message de connexion.

**Capture du serveur en attente :**

![Serveur en attente](screenshots/04-server-waiting.png)

`[INSÉRER ICI LA CAPTURE DE LA CONSOLE SERVEUR AVANT LA CONNEXION]`

**Capture du client connecté :**

![Client connecté](screenshots/05-client-connected.png)

`[INSÉRER ICI LA CAPTURE DU CLIENT ET DU MESSAGE DE CONNEXION CÔTÉ SERVEUR]`

**Interprétation :** avant le lancement du client, le serveur reste en attente dans `accept()`. Lorsque le client tente de se connecter au bon hôte et au bon port, la connexion est établie et `accept()` retourne le socket. Le serveur peut alors communiquer avec ce client.

## 5. Activité 1-2 — Service de multiplication par cinq

### 5.1 Scénario de communication

1. Le client demande à l'utilisateur de saisir un entier `x`.
2. Le client transmet `x` avec `DataOutputStream.writeInt(x)`.
3. Le serveur reçoit l'entier avec `DataInputStream.readInt()`.
4. Le serveur calcule `x * 5`.
5. Le serveur renvoie le résultat avec `writeInt(result)`.
6. Le client récupère le résultat avec `readInt()` et l'affiche.
7. Le cycle continue jusqu'à la saisie de `0`. Zéro est envoyé comme signal d'arrêt et aucun résultat n'est demandé pour cette valeur.

### 5.2 Pourquoi `DataInputStream` et `DataOutputStream` ?

Ces flux permettent d'écrire et de lire des types primitifs Java avec un format correspondant. `writeInt()` et `readInt()` échangent un entier signé de 32 bits ; les valeurs négatives sont donc prises en charge correctement. Les deux côtés doivent lire et écrire les données dans le même ordre et avec des types compatibles.

`flush()` force l'envoi des données mises en tampon. Les flux sont fermés automatiquement grâce à `try-with-resources`.

### 5.3 Tests

Exécuter le serveur puis le client. Tester plusieurs valeurs, par exemple :

| Entier saisi `x` | Calcul côté serveur | Résultat attendu |
|---:|---:|---:|
| 4 | 4 × 5 | 20 |
| 7 | 7 × 5 | 35 |
| -3 | -3 × 5 | -15 |
| -8 | -8 × 5 | -40 |
| 0 | signal d'arrêt | fermeture propre |

**Capture des tests côté client :**

![Tests client](screenshots/06-calculator-positive-negative-zero.png)

`[INSÉRER ICI UNE CAPTURE MONTRANT LES VALEURS POSITIVES, NÉGATIVES ET LA SAISIE DE 0]`

**Capture des traitements côté serveur :**

![Résultats serveur](screenshots/07-server-results.png)

`[INSÉRER ICI UNE CAPTURE MONTRANT LES ENTIERS REÇUS ET LES RÉSULTATS ENVOYÉS]`

**Interprétation :** les résultats attendus sont respectivement `20`, `35`, `-15` et `-40`. Les valeurs négatives sont conservées grâce à l'utilisation de `readInt()` et `writeInt()`. Quand le client envoie zéro, le serveur sort de la boucle et les ressources sont fermées des deux côtés.

> Les valeurs ci-dessus sont les résultats attendus d'après le calcul. Remplacez ou complétez cette section avec vos propres captures d'exécution.

## 6. Utilisation d'une adresse IP au lieu de `localhost`

Dans `Client.java`, la constante suivante configure l'hôte :

```java
private static final String SERVER_HOST = "localhost";
```

Pour le test demandé, remplacez `localhost` par l'adresse IPv4 de l'ordinateur qui exécute le serveur. Sur Windows, exécutez `ipconfig` et repérez l'adresse IPv4 de l'interface réseau utilisée. Sur le même ordinateur, une adresse de bouclage telle que `127.0.0.1` peut également être utilisée. Pour deux ordinateurs différents, utilisez l'adresse IP locale du serveur et assurez-vous que le pare-feu autorise le port `5000`.

**Capture à insérer :**

![Test IP](screenshots/08-ip-address-test.png)

`[INSÉRER ICI LA CAPTURE DE L'ADRESSE IP UTILISÉE ET DE LA CONNEXION RÉUSSIE]`

**Interprétation :** `localhost` pointe vers l'ordinateur sur lequel le client est exécuté. Une adresse IP identifie l'interface réseau visée. Pour un test sur deux ordinateurs, le client doit utiliser l'adresse IP de l'ordinateur serveur, et les deux machines doivent pouvoir communiquer sur le port choisi.

Après ce test, remettez `SERVER_HOST` à `localhost` si vous souhaitez exécuter les deux applications sur le même ordinateur.

## 7. Comment compiler et exécuter depuis un terminal

Depuis le dossier racine du projet :

```bash
javac -d out src/serverPackage/Server.java src/clientPackage/Client.java
```

Dans un premier terminal, lancer le serveur :

```bash
java -cp out serverPackage.Server
```

Dans un deuxième terminal, lancer le client :

```bash
java -cp out clientPackage.Client
```

**Important :** lancez le serveur avant le client. Si le client affiche une erreur de connexion, vérifiez que le serveur tourne, que le port est identique des deux côtés et que l'adresse de l'hôte est correcte.

## 8. Publication sur GitHub

1. Créer ou ouvrir votre compte sur [GitHub](https://github.com/).
2. Créer un dépôt public nommé exactement `TP1-DAR2026`.
3. Ajouter les fichiers du projet, notamment `src/`, `.gitignore`, `README.md` et les captures dans `screenshots/`.
4. Valider (commit) les modifications et les envoyer (push) sur GitHub.
5. Ouvrir la page du dépôt, vérifier que le README s'affiche et que toutes les captures apparaissent.
6. Copier l'URL du dépôt et la déposer dans l'espace ENT prévu par l'enseignante.

Si vous utilisez le terminal avec Git, les commandes de base depuis le dossier du projet sont :

```bash
git init
git add .
git commit -m "TP1: TCP sockets and multiplication service"
git branch -M main
git remote add origin https://github.com/VOTRE-NOM/TP1-DAR2026.git
git push -u origin main
```

Remplacez `VOTRE-NOM` par votre identifiant GitHub. Si le dépôt a déjà été créé et cloné avec Git, ne répétez pas `git init` ni `git remote add origin`. GitHub peut demander une authentification lors du `push`.

## 9. Conclusion

Ce TP met en œuvre une communication TCP entre un client et un serveur Java. Le serveur attend la connexion, reçoit des entiers et fournit un service de multiplication par cinq. Le client peut effectuer plusieurs demandes au cours de la même connexion, y compris avec des nombres négatifs. La saisie de zéro arrête la communication et permet la fermeture des ressources.

## 10. Liste de vérification avant le dépôt

- [ ] Le projet compile sans erreur.
- [ ] Le serveur est lancé avant le client.
- [ ] Les tests positifs, négatifs et zéro ont été réalisés.
- [ ] Le test avec une adresse IP a été tenté et documenté.
- [ ] Mon nom et la date sont ajoutés si demandés.
- [ ] Toutes les images référencées dans ce README sont de vraies captures ajoutées au dossier `screenshots/`.
- [ ] Le dépôt GitHub contient le code et le compte rendu à jour.
- [ ] Le lien GitHub a été déposé sur ENT.
