# TP1-DAR2026 — Les sockets en mode connecté TCP

**Matière :** Développement d'applications réparties — LSI 3
**Établissement :** Faculté des Sciences de Sfax, Département d'informatique et des Communications
**Année universitaire :** 2026 – 2027
**Étudiant :** Wael Riahi

---

## 1. Introduction

Ce compte rendu présente la réalisation du TP1, qui porte sur la communication **Client/Serveur** en Java à l'aide des **sockets en mode connecté (TCP)**. Le travail est divisé en trois activités, chacune enrichissant la précédente :

| Activité | Objectif |
|---|---|
| **1-1** | Établir une première connexion entre un client et un serveur |
| **1-2** | Ajouter un service de calcul côté serveur (multiplication par 5) |
| **1-3** | Connecter deux machines distinctes et proposer une calculatrice à quatre opérations |

## 2. Environnement de travail

- Java (JDK ≥ 1.8)
- IDE : IntelliJ IDEA / Eclipse / VS Code
- Git et GitHub pour le versionnement et le rendu
- Le dépôt Git contient un commit par activité, ce qui permet de suivre l'évolution du projet.

## 3. Structure du projet

```
TP1-DAR2026/
├── src/
│   ├── serverPackage/Server.java   → application serveur
│   └── clientPackage/Client.java   → application client
└── README.md                       → ce compte rendu
```

## 4. Notions utilisées

- **TCP** est un protocole en *mode connecté* : une connexion fiable est d'abord établie entre le client et le serveur, puis les données sont échangées dans l'ordre et sans perte.
- **`ServerSocket(port)`** (serveur) ouvre un port d'écoute. Sa méthode **`accept()`** est *bloquante* : le programme attend l'arrivée d'un client et retourne un `Socket` dédié à cette connexion.
- **`Socket(adresse, port)`** (client) demande la connexion au serveur.
- **`getInputStream()` / `getOutputStream()`** donnent accès aux flux de lecture et d'écriture de la connexion.
- **`close()`** libère les ressources : il faut fermer les sockets à la fin.

---

## 5. Activité 1-1 : Première connexion Client/Serveur

### 5.1 Préparation
- `git --version` vérifie que Git est installé.
- `java --version` vérifie que Java est installé. Si la commande n'est pas reconnue, il faut ajouter le dossier `bin` du JDK à la variable d'environnement `PATH`.
- Un répertoire de travail `votreNomPrenom_LSI3_DevAppReparties` a été créé, contenant le projet Java `TP1-DAR2026` avec un dépôt Git.

### 5.2 Le serveur (`serverPackage.Server`)

Le serveur ouvre le port 1234 et attend un client :

```java
ServerSocket serverSocket = new ServerSocket(1234);
System.out.println("Je suis un serveur en attente la connexion d'un client ");
Socket socket = serverSocket.accept();      // bloque jusqu'à l'arrivée d'un client
System.out.println("un client est connecté");
socket.close();
serverSocket.close();
```

### 5.3 Le client (`clientPackage.Client`)

Le client demande la connexion au serveur :

```java
System.out.println("Je suis un client pas encore connecté…");
Socket socket = new Socket("localhost", 1234);
System.out.println("je suis un client connecté");
socket.close();
```

### 5.4 Résultat de l'exécution

On lance **d'abord le serveur**, puis le client.

```
--- Console du serveur ---
Je suis un serveur en attente la connexion d'un client
un client est connecté

--- Console du client ---
Je suis un client pas encore connecté…
je suis un client connecté
```

**Interprétation :**
- Au lancement, le serveur affiche son message d'attente puis **reste bloqué** sur `accept()` : il n'y a pas encore de client.
- Dès que le client se connecte, `accept()` se débloque et le serveur affiche « un client est connecté ». Côté client, le message « je suis un client connecté » confirme que la connexion TCP est établie.
- Si on lance le client **sans** serveur, on obtient une `ConnectException: Connection refused` : personne n'écoute sur le port 1234.

### 5.5 Remplacement de `localhost` par une adresse IP

En remplaçant `"localhost"` par `"127.0.0.1"` (puis par l'adresse IP de la machine), le résultat est identique : `localhost` n'est que le nom de l'adresse de bouclage `127.0.0.1`.

---

## 6. Activité 1-2 : Service de calculatrice

### 6.1 Principe

Le client n'a pas de calculatrice : il demande au serveur de multiplier un entier `x` par 5.

| Étape | Client | Serveur |
|---|---|---|
| 1 | Saisit un entier `x` au clavier | Attend un entier |
| 2 | Envoie `x` | Reçoit `x` |
| 3 | | Calcule `x * 5` |
| 4 | Reçoit le résultat | Envoie le résultat |
| 5 | Affiche le résultat | |

### 6.2 Pourquoi `DataInputStream` et `DataOutputStream` ?

La méthode `write(int)` d'un flux classique n'envoie qu'**un seul octet** (8 bits). Un entier négatif ou supérieur à 255 serait donc déformé à l'arrivée.
`DataOutputStream.writeInt()` envoie les **4 octets** de l'entier, et `DataInputStream.readInt()` les relit : les **nombres négatifs** sont ainsi transmis correctement.

### 6.3 Code

**Serveur** — boucle de service :

```java
DataInputStream in = new DataInputStream(socket.getInputStream());
DataOutputStream out = new DataOutputStream(socket.getOutputStream());

while (true) {
    int x = in.readInt();
    if (x == 0) break;              // 0 = le client demande l'arrêt
    int resultat = x * 5;
    out.writeInt(resultat);
    out.flush();
}
in.close(); out.close(); socket.close(); serverSocket.close();
```

**Client** — saisie en boucle :

```java
int x;
do {
    System.out.print("Entrez un entier x (0 pour quitter) : ");
    x = clavier.nextInt();
    out.writeInt(x);
    out.flush();
    if (x != 0) {
        int resultat = in.readInt();
        System.out.println("Résultat du serveur : " + x + " * 5 = " + resultat);
    }
} while (x != 0);
in.close(); out.close(); socket.close();
```

### 6.4 Résultat de l'exécution (valeurs testées : 4, -3, 10, puis 0)

```
--- Console du client ---
Entrez un entier x (0 pour quitter) : 4
Résultat du serveur : 4 * 5 = 20
Entrez un entier x (0 pour quitter) : -3
Résultat du serveur : -3 * 5 = -15
Entrez un entier x (0 pour quitter) : 10
Résultat du serveur : 10 * 5 = 50
Entrez un entier x (0 pour quitter) : 0
Fin de la communication, fermeture du socket

--- Console du serveur ---
Reçu x = 4 -> envoi de 20
Reçu x = -3 -> envoi de -15
Reçu x = 10 -> envoi de 50
Le client a envoyé 0 : fin de la communication
Sockets fermés côté serveur
```

**Interprétation :**
- Les valeurs positives (`4` → `20`) et négatives (`-3` → `-15`) sont traitées correctement grâce à `DataInputStream` / `DataOutputStream`.
- `0` est la **valeur de sortie** : elle n'est pas calculée. Le client l'envoie pour prévenir le serveur, puis les deux programmes **ferment proprement leurs flux et leurs sockets**.
- Cet arrêt coordonné évite que le serveur reste bloqué en attente ou que la connexion soit coupée brutalement (ce qui provoquerait une `EOFException`).

---

## 7. Activité 1-3 : Amélioration du service de calculatrice

### 7.1 Étape 1 : Connexion entre deux machines distinctes

Jusqu'ici, tout fonctionnait avec `localhost` (client et serveur sur la même machine). Pour utiliser deux machines du **même réseau local** :

1. **Côté serveur**, on lie le `ServerSocket` à l'**adresse IP réelle** de la machine et au port choisi, avec `InetAddress` et `InetSocketAddress` :

```java
InetAddress ip = (args.length > 0) ? InetAddress.getByName(args[0]) : trouverIpLocale();
InetSocketAddress adresse = new InetSocketAddress(ip, 1234);
ServerSocket serverSocket = new ServerSocket();
serverSocket.bind(adresse);
```

La méthode `trouverIpLocale()` parcourt les interfaces réseau (`NetworkInterface`) et retient la première adresse IPv4 privée (du type 192.168.x.x ou 10.x.x.x).

2. **Côté client**, on remplace `localhost` par l'adresse IP de la machine serveur, saisie au démarrage :

```java
InetAddress ipServeur = InetAddress.getByName(saisie);
Socket socket = new Socket();
socket.connect(new InetSocketAddress(ipServeur, 1234), 5000);   // délai maximal : 5 secondes
```

**Procédure de test :**
1. Relever l'IP de la machine serveur (`ipconfig` sous Windows, `ip a` sous Linux).
2. Lancer le serveur sur la machine A.
3. Lancer le client sur la machine B et saisir l'IP de la machine A.
4. Le serveur affiche alors l'adresse de la machine cliente (`getRemoteSocketAddress()`), ce qui prouve que la connexion vient d'une autre machine.

> **Remarque :** si la connexion échoue, le pare-feu de la machine serveur bloque probablement le port 1234 : il faut autoriser Java (ou ce port) en entrée.

### 7.2 Étape 2 : Calculatrice à quatre opérations

**Protocole d'échange.** Pour chaque calcul, le client envoie trois entiers : le **choix** de l'opération (1 = addition, 2 = soustraction, 3 = multiplication, 4 = division), puis les opérandes **x** et **y**. Le serveur calcule et renvoie la réponse sous forme de texte (`writeUTF`), ce qui lui permet aussi de renvoyer un message d'erreur. Le choix `0` termine la communication.

**Menu proposé au client :**

```
===== CALCULATRICE =====
1. Addition
2. Soustraction
3. Multiplication
4. Division
0. Quitter
```

**Calcul côté serveur :**

```java
switch (choix) {
    case 1:  reponse = x + " + " + y + " = " + (x + y); break;
    case 2:  reponse = x + " - " + y + " = " + (x - y); break;
    case 3:  reponse = x + " * " + y + " = " + ((long) x * y); break;
    case 4:
        if (y == 0) reponse = "Erreur : division par zéro impossible";
        else        reponse = x + " / " + y + " = " + ((double) x / y);
        break;
    default: reponse = "Erreur : opération inconnue";
}
out.writeUTF(reponse);
```

### 7.3 Résultats des tests

```
--- Console du serveur ---
Je suis un serveur en attente de connexion sur /127.0.0.1:1234
un client est connecté : /127.0.0.1:34598
Opération 1 sur (10, 5) -> 10 + 5 = 15
Opération 3 sur (-4, 6) -> -4 * 6 = -24
Opération 4 sur (7, 2) -> 7 / 2 = 3.5
Opération 4 sur (5, 0) -> Erreur : division par zéro impossible
Le client quitte : fin de la communication
Sockets fermés côté serveur
```

| Opération | x | y | Résultat obtenu |
|---|---|---|---|
| Addition | 10 | 5 | 15 |
| Multiplication | -4 | 6 | -24 |
| Division | 7 | 2 | 3.5 |
| Division | 5 | 0 | Erreur : division par zéro impossible |

**Interprétation :**
- Les quatre opérations donnent le résultat correct, y compris avec des nombres négatifs.
- La **division** est calculée en `double` : `7 / 2 = 3.5` (et non `3` comme avec une division entière).
- La **division par zéro** est détectée **côté serveur**, qui renvoie un message d'erreur au lieu de planter.
- Le choix `0` ferme proprement la connexion des deux côtés.

---

## 8. Conclusion

Ce TP m'a permis de comprendre le fonctionnement des sockets TCP en Java :
- le **serveur** attend les connexions avec `ServerSocket` et `accept()` ;
- le **client** se connecte avec `Socket` en indiquant l'adresse IP et le port du serveur ;
- les données sont échangées via des flux `DataInputStream` / `DataOutputStream`, qui gèrent correctement les entiers (négatifs inclus) et les chaînes ;
- le **calcul est effectué côté serveur**, ce qui illustre le principe d'une application répartie ;
- il faut toujours **fermer proprement** les sockets des deux côtés.

Les activités successives ont permis de passer d'une simple connexion à un service complet, utilisable entre deux machines distinctes d'un même réseau.

## 9. Exécution du projet

```bash
# Compilation
javac -d out src/serverPackage/Server.java src/clientPackage/Client.java

# Terminal 1 : serveur (IP détectée automatiquement, ou à donner en argument)
java -cp out serverPackage.Server

# Terminal 2 : client (l'IP du serveur est demandée au démarrage)
java -cp out clientPackage.Client
```

**Dépôt GitHub :** https://github.com/VOTRE-PSEUDO/TP1-DAR2026 *(à remplacer par votre lien)*
