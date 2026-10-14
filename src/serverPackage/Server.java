package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class Server {
    static final int PORT = 1234;

    // Cherche l'adresse IPv4 réelle (réseau local) de la machine
    static InetAddress trouverIpLocale() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
            Enumeration<InetAddress> adresses = ni.getInetAddresses();
            while (adresses.hasMoreElements()) {
                InetAddress a = adresses.nextElement();
                if (a instanceof Inet4Address && a.isSiteLocalAddress()) return a;
            }
        }
        return InetAddress.getLoopbackAddress(); // solution de repli
    }

    public static void main(String[] args) {
        try {
            // IP donnée en argument, sinon détection automatique
            InetAddress ip = (args.length > 0) ? InetAddress.getByName(args[0]) : trouverIpLocale();
            InetSocketAddress adresse = new InetSocketAddress(ip, PORT);

            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(adresse);          // écoute sur l'IP réelle + le port choisi
            System.out.println("Je suis un serveur en attente de connexion sur " + adresse);

            Socket socket = serverSocket.accept();
            System.out.println("un client est connecté : " + socket.getRemoteSocketAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                int choix = in.readInt();        // 1=+  2=-  3=*  4=/  0=quitter
                if (choix == 0) {
                    System.out.println("Le client quitte : fin de la communication");
                    break;
                }
                int x = in.readInt();
                int y = in.readInt();

                String reponse;
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
                System.out.println("Opération " + choix + " sur (" + x + ", " + y + ") -> " + reponse);
                out.writeUTF(reponse);           // résultat envoyé sous forme de texte
                out.flush();
            }

            in.close();
            out.close();
            socket.close();
            serverSocket.close();
            System.out.println("Sockets fermés côté serveur");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
