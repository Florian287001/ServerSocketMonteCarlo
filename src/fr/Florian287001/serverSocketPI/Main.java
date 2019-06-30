package fr.Florian287001.serverSocketPI;

import fr.Florian287001.serverSocketPI.server.Server;

public class Main {

	private static int nbWorker = 4;
	private static int nbThrows = 100000;
	
	public static void main(String[] args) {
		/*
		 * for(int port = 1; port <= 65535; port++){ try { ServerSocket sSocket
		 * = new ServerSocket(port); } catch (IOException e) {
		 * System.err.println("Le port " + port + " est déjà utilisé ! "); } }
		 */

		String host = "127.0.0.1";
		int port = 23145;

		// Démarrage du serveur
		for(int i = 0; i<nbWorker; i++){
			Server ts = new Server(host, port + i);
			ts.open();
		}

		System.out.println("Serveur initialisé.");

		// Démarrage du client
		Thread t = new Thread(new ClientConnexion(host, port, nbThrows, nbWorker));
		t.start();
		

	}

}
