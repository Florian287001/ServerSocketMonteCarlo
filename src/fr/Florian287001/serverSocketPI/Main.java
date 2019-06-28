package fr.Florian287001.serverSocketPI;

import java.io.IOException;
import java.net.ServerSocket;

import fr.Florian287001.serverSocketPI.server.Server;

public class Main {

	public static int nbWorker = 5;
	
	public static void main(String[] args) {
		/*
		 * for(int port = 1; port <= 65535; port++){ try { ServerSocket sSocket
		 * = new ServerSocket(port); } catch (IOException e) {
		 * System.err.println("Le port " + port + " est d�j� utilis� ! "); } }
		 */

		String host = "127.0.0.1";
		int port = 23145;

		// D�marrage du serveur
		for(int i = 0; i<nbWorker; i++){
			Server ts = new Server(host, port + i);
			ts.open();
		}

		System.out.println("Serveur initialis�.");

		// D�marrage du client
		int nbThrows = 100000;
		Thread t = new Thread(new ClientConnexion(host, port));
		t.start();
		

	}

}
