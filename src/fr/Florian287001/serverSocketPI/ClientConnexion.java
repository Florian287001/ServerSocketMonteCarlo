package fr.Florian287001.serverSocketPI;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientConnexion implements Runnable {

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	private int nbThrows;
	private int nbWorker;

	private static int count = 0;
	private String name = "Client-";
	private String host;
	private int port;

	public ClientConnexion(String host, int port, int nbThrows, int nbWorker) {
		name += ++count;
		this.host = host;
		this.port = port;
		this.nbThrows = nbThrows;
		this.nbWorker = nbWorker;
	}

	public void run() {
		long startTimeGlobal = System.nanoTime();
		List<Long> nbAleatoireMC = new ArrayList<>();
		for (int i = 0; i < nbWorker; i++) {
			/*
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			try {
				long startTime = System.nanoTime();
				connexion = new Socket(host, port + i);
				writer = new PrintWriter(connexion.getOutputStream(), true);
				reader = new BufferedInputStream(connexion.getInputStream());
				// On envoie la commande au serveur

				String commande = "PI;" + nbThrows/nbWorker;

				writer.write(commande);

				writer.flush();

				System.out.println("Commande " + commande
						+ " envoyée au serveur");

				// On attend la réponse
				String response = read();

				nbAleatoireMC.add(Long.parseLong(response));
				System.out.println("\t * " + name + " | Server " + i
						+ " : Reponse recue " + response);
				connexion.close();
				long stopTime = System.nanoTime();
				System.out.println("Time Duration: " + (stopTime - startTime)
						/ 1000000 + "ms");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		writer.write("CLOSE");
		writer.flush();
		writer.close();

		System.out.println("List : " + nbAleatoireMC);

		Iterator<Long> nbAleatoireMCIterator = nbAleatoireMC.iterator();
		double piMoy = 0;
		Long nbAleatoireMCTotal = 0L;
		while (nbAleatoireMCIterator.hasNext()) {
			nbAleatoireMCTotal = nbAleatoireMCTotal
					+ nbAleatoireMCIterator.next();
		}

		// calcule de PI
		double pi = 4.0 * (double) nbAleatoireMCTotal/ (nbThrows);
		
		long stopTimeGlobal = System.nanoTime();
		
		System.out.println("Pi : " + pi);
		System.out.println("Time Duration Global: " + (stopTimeGlobal - startTimeGlobal)
				/ 1000000 + "ms");
	}

	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}

}