package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class client {
    static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            Scanner clavier = new Scanner(System.in);

            // Adresse IP de la machine SERVEUR (localhost si on laisse vide)
            System.out.print("Adresse IP du serveur (Entrée = localhost) : ");
            String saisie = clavier.nextLine().trim();
            if (saisie.isEmpty()) saisie = "localhost";

            InetAddress ipServeur = InetAddress.getByName(saisie);
            System.out.println("Je suis un client pas encore connecté…");

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipServeur, PORT), 5000); // timeout 5 s
            System.out.println("je suis un client connecté à " + socket.getRemoteSocketAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            int choix;
            do {
                System.out.println("\n===== CALCULATRICE =====");
                System.out.println("1. Addition");
                System.out.println("2. Soustraction");
                System.out.println("3. Multiplication");
                System.out.println("4. Division");
                System.out.println("0. Quitter");
                System.out.print("Votre choix : ");
                choix = clavier.nextInt();

                if (choix == 0) {
                    out.writeInt(0);
                    out.flush();
                } else if (choix >= 1 && choix <= 4) {
                    System.out.print("x = ");
                    int x = clavier.nextInt();
                    System.out.print("y = ");
                    int y = clavier.nextInt();
                    out.writeInt(choix);
                    out.writeInt(x);
                    out.writeInt(y);
                    out.flush();
                    System.out.println("Résultat du serveur : " + in.readUTF());
                } else {
                    System.out.println("Choix invalide, réessayez.");
                }
            } while (choix != 0);

            System.out.println("Fin de la communication, fermeture du socket");
            in.close();
            out.close();
            socket.close();
            clavier.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
