# TP1-DAR2026 : Les sockets en mode connecté TCP

**Matière :** Développement d'applications réparties — LSI 3 — Faculté des Sciences de Sfax (2026–2027)
**Étudiant :** *Prénom NOM*
**Enseignante des TPs :** Zeineb DAOUD

> Compte rendu du TP1 : Activités 1-1, 1-2 et 1-3.
> Les captures d'écran se trouvent dans le dossier `screenshots/`.

---

## Rappel théorique (en bref)

- **TCP** est un protocole en **mode connecté** : le client et le serveur établissent d'abord une connexion fiable, puis échangent des données.
- Côté **serveur** : `ServerSocket(port)` ouvre un port d'écoute, puis `accept()` **bloque** le programme jusqu'à l'arrivée d'un client et retourne un `Socket` dédié à ce client.
- Côté **client** : `new Socket(adresseIP, port)` demande la connexion au serveur.
- Une fois connectés, chacun récupère un flux d'entrée (`getInputStream()`) et un flux de sortie (`getOutputStream()`) pour communiquer.
- À la fin, il faut **fermer** les sockets (`close()`) pour libérer les ressources.

## Structure du projet

```
TP1-DAR2026/
├── src/
│   ├── serverPackage/Server.java
│   └── clientPackage/Client.java
├── screenshots/
└── README.md
```

---
---

# Activité 1-1 : Première connexion Client/Serveur

**Objectif :** établir une première communication Client/Serveur en Java avec des sockets TCP.

## Étape 1 : Préparation de l'environnement Git

Commande : `git --version` (dans un terminal).
Elle affiche la version de Git installée, ce qui confirme que Git est disponible sur la machine.

![Version de Git](screenshots/a11-01-git-version.png)

*Description : <!-- ex. Git est installé, la version affichée est 2.xx -->*

## Étape 2 : Préparation de l'environnement Java

Commande : `java --version`.
Elle affiche la version du JDK. Si la commande n'est pas reconnue, il faut ajouter le dossier `bin` du JDK à la variable d'environnement `PATH`.

![Version de Java](screenshots/a11-02-java-version.png)

*Description : <!-- ex. JDK 17 installé, PATH correctement configuré -->*

Création du répertoire de travail `votreNomPrenom_LSI3_DevAppReparties` :

![Répertoire de travail](screenshots/a11-03-repertoire.png)

## Étape 3 : Création du serveur

1. Création du projet Java `TP1-DAR2026` avec un dépôt Git initialisé.

   ![Création du projet](screenshots/a11-04-projet.png)

2. Création du package `serverPackage` et de la classe `Server` (avec `main()`).
3. Code complété (les parties TODO) :

```java
package serverPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);      // 1ère étape : ouvrir le port 1234
            System.out.println("Je suis un serveur en attente la connexion d'un client ");

            Socket socket = serverSocket.accept();                   // 2ème étape : attendre un client
            System.out.println("un client est connecté");

            socket.close();                                          // dernière étape : fermer
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

![Code du serveur](screenshots/a11-05-code-serveur.png)

4. Lancement du serveur :

![Console du serveur en attente](screenshots/a11-06-console-serveur.png)

**Interprétation :** le serveur affiche « Je suis un serveur en attente la connexion d'un client » puis **reste bloqué** : c'est le rôle de `accept()`, qui attend qu'un client se connecte. Le programme ne se termine pas tant qu'aucun client n'arrive.

## Étape 4 : Création du client

1. Création du package `clientPackage` et de la classe `Client` (avec `main()`).
2. Code complété :

```java
package clientPackage;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Je suis un client pas encore connecté…");
            Socket socket = new Socket("localhost", 1234);           // demande de connexion
            System.out.println("je suis un client connecté");
            socket.close();                                          // dernière étape : fermer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

![Code du client](screenshots/a11-07-code-client.png)

3. Lancement du client (le serveur étant déjà lancé) :

![Consoles client et serveur](screenshots/a11-08-consoles.png)

**Interprétation :**
- Côté **client** : « Je suis un client pas encore connecté… » puis « je suis un client connecté » → la connexion TCP a réussi.
- Côté **serveur** : le message « un client est connecté » apparaît, car `accept()` s'est débloqué. Les deux programmes se terminent ensuite en fermant leurs sockets.
- Si on lance le client **sans** serveur, on obtient une `ConnectException: Connection refused` : personne n'écoute sur le port 1234.

4. Remplacement de `localhost` par une adresse IP (`127.0.0.1`, puis l'IP de la machine) :

```java
Socket socket = new Socket("127.0.0.1", 1234);
```

![Client avec adresse IP](screenshots/a11-09-client-ip.png)

**Interprétation :** `localhost` est simplement le nom de l'adresse de bouclage `127.0.0.1`. Le résultat est identique : la connexion fonctionne.

## Étape 5 : Dépôt des livrables

Le projet est poussé sur GitHub et le lien du dépôt est déposé sur l'ENT (voir la fin du document).

---
---

# Activité 1-2 : Service de calculatrice

**Objectif :** étendre l'application pour que le serveur effectue un calcul (multiplication par 5) demandé par le client.

## Étape 1 : Préparation de l'espace de travail

On repart du projet de l'Activité 1-1 (même dépôt `TP1-DAR2026`).

![Espace de travail](screenshots/a12-01-workspace.png)

## Étape 2 : Création du service de multiplication

### Principe du protocole

| Ordre | Client | Serveur |
|---|---|---|
| a | Saisit un entier `x` au clavier | Attend un entier (`readInt`) |
| b | Envoie `x` (`writeInt`) | Reçoit `x` |
| c | | Calcule `x * 5` |
| d | Reçoit le résultat (`readInt`) | Envoie le résultat (`writeInt`) |
| e | Affiche le résultat | |

### Pourquoi `DataInputStream` / `DataOutputStream` ?

La méthode `OutputStream.write(int)` n'envoie que **1 octet** (les 8 bits de poids faible) : un entier négatif ou supérieur à 255 serait déformé à l'arrivée.
`DataOutputStream.writeInt()` envoie les **4 octets** complets de l'entier (complément à deux) et `DataInputStream.readInt()` les relit : les **nombres négatifs** sont donc transmis correctement.

### Code du serveur

```java
DataInputStream in = new DataInputStream(socket.getInputStream());
DataOutputStream out = new DataOutputStream(socket.getOutputStream());

while (true) {
    int x = in.readInt();
    if (x == 0) {                       // 0 : le client veut arrêter
        System.out.println("Le client a envoyé 0 : fin de la communication");
        break;
    }
    int resultat = x * 5;
    System.out.println("Reçu x = " + x + " -> envoi de " + resultat);
    out.writeInt(resultat);
    out.flush();
}
in.close(); out.close(); socket.close(); serverSocket.close();
```

![Code du serveur](screenshots/a12-02-code-serveur.png)

### Code du client

```java
Scanner clavier = new Scanner(System.in);
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

![Code du client](screenshots/a12-03-code-client.png)

### Tests (serveur lancé d'abord, puis client)

Valeurs testées : positive (`4`), négative (`-3`), puis `0`.

![Tests avec plusieurs valeurs](screenshots/a12-04-tests.png)

**Interprétation :**
- `4` → `20` ; `-3` → `-15` : le signe est conservé grâce à `DataInputStream`/`DataOutputStream`.
- `0` → **n'est pas calculé** : c'est la valeur sentinelle qui demande l'arrêt. Le client envoie `0`, le serveur le reçoit et sort de sa boucle.

### Saisie de plusieurs entiers et arrêt propre

La boucle côté client continue tant que l'entier saisi est différent de 0. Quand l'utilisateur saisit `0`, le client **prévient le serveur** en lui envoyant `0`, puis les deux côtés **ferment leurs flux et leurs sockets**.

![Boucle et fermeture propre](screenshots/a12-05-boucle-fermeture.png)

## Étape 3 : Dépôt des livrables

Projet mis à jour sur GitHub (commit de l'Activité 1-2) :

![Mise à jour GitHub](screenshots/a12-06-github.png)

---
---

# Activité 1-3 : Amélioration du service de calculatrice

## Étape 1 : Connexion sur deux machines distinctes

Les deux machines doivent être sur le **même réseau local** (même Wi-Fi / même routeur).

1. **Trouver l'IP de la machine serveur** : `ipconfig` (Windows) ou `ip a` / `ifconfig` (Linux/Mac). On cherche l'adresse IPv4 du réseau local (ex. `192.168.1.x`).

   ![Adresse IP du serveur](screenshots/a13-01-ip-serveur.png)

2. **Côté serveur** : on utilise `InetAddress` et `InetSocketAddress` pour lier le `ServerSocket` à l'IP réelle de la machine et au port 1234 :

```java
InetAddress ip = (args.length > 0) ? InetAddress.getByName(args[0]) : trouverIpLocale();
InetSocketAddress adresse = new InetSocketAddress(ip, 1234);
ServerSocket serverSocket = new ServerSocket();
serverSocket.bind(adresse);
```

   La méthode `trouverIpLocale()` parcourt les interfaces réseau (`NetworkInterface`) et retourne la première adresse IPv4 privée (192.168.x.x, 10.x.x.x…).

   ![Code du serveur](screenshots/a13-02-code-serveur.png)

3. **Côté client** : on remplace `localhost` par l'IP de la machine serveur, saisie au clavier :

```java
InetAddress ipServeur = InetAddress.getByName(saisie);
Socket socket = new Socket();
socket.connect(new InetSocketAddress(ipServeur, 1234), 5000);   // timeout de 5 secondes
```

   ![Code du client](screenshots/a13-03-code-client.png)

4. **Test sur deux machines** (serveur sur la machine A, client sur la machine B) :

   ![Connexion entre deux machines](screenshots/a13-04-deux-machines.png)

**Interprétation :** le serveur affiche l'adresse du client distant (`socket.getRemoteSocketAddress()`), ce qui prouve que la connexion vient d'une autre machine.

**Remarque (pare-feu) :** si le client n'arrive pas à se connecter, le pare-feu de la machine serveur bloque probablement le port 1234 : il faut autoriser Java ou ce port en entrée.

## Étape 2 : Service de calculatrice amélioré

### Protocole

Pour chaque calcul, le client envoie 3 entiers : **le choix** (1 = addition, 2 = soustraction, 3 = multiplication, 4 = division) puis les deux opérandes **x** et **y**. Le serveur calcule et renvoie la réponse sous forme de texte (`writeUTF`), ce qui permet aussi de renvoyer un message d'erreur. Le choix `0` termine la communication.

### Menu côté client

```
===== CALCULATRICE =====
1. Addition
2. Soustraction
3. Multiplication
4. Division
0. Quitter
```

![Menu du client](screenshots/a13-05-menu.png)

### Calcul côté serveur

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

![Code du calcul côté serveur](screenshots/a13-06-code-calcul.png)

### Tests

| Opération | x | y | Résultat attendu |
|---|---|---|---|
| Addition | 10 | 5 | 15 |
| Soustraction | 3 | 8 | -5 |
| Multiplication | -4 | 6 | -24 |
| Division | 7 | 2 | 3.5 |
| Division | 5 | 0 | Erreur : division par zéro impossible |

![Tests des quatre opérations](screenshots/a13-07-tests.png)

**Interprétation :**
- Les 4 opérations donnent le résultat correct, y compris avec des nombres négatifs.
- La **division** utilise des `double` : `7 / 2 = 3.5` (et non 3 comme en division entière).
- La **division par zéro** est détectée **côté serveur** et renvoie un message d'erreur au lieu de planter le programme.
- Le choix `0` ferme proprement la connexion des deux côtés.

![Division par zéro et fermeture](screenshots/a13-08-division-zero.png)

---

# Dépôt des livrables

- Dépôt GitHub : **https://github.com/VOTRE-PSEUDO/TP1-DAR2026** *(à remplacer)*
- Le lien est déposé dans l'espace de dépôt de l'ENT.

![Dépôt GitHub](screenshots/a13-09-github.png)

## Comment exécuter le projet

```bash
# Compilation
javac -d out src/serverPackage/Server.java src/clientPackage/Client.java

# Terminal 1 : serveur (IP détectée automatiquement, ou donner l'IP en argument)
java -cp out serverPackage.Server

# Terminal 2 : client (l'IP du serveur est demandée au démarrage)
java -cp out clientPackage.Client
```
