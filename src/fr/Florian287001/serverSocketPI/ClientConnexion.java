package fr.Florian287001.serverSocketPI;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class ClientConnexion implements Runnable {

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	private static int count = 0;
	private String name = "Client-";
	private String host;
	private int port;

	public ClientConnexion(String host, int port) {
		name += ++count;
		this.host = host;
		this.port = port;
	}

	public void run() {
		long startTimeGlobal = System.nanoTime();
		List<Long> nbAleatoireMC = new ArrayList<>();
		for (int i = 0; i < Main.nbWorker; i++) {
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

				String commande = "PI";

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
						/ 100000 + "ms");
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
		double pi = 4.0 * (double) nbAleatoireMCTotal/ (100000 * Main.nbWorker);
		
		long stopTimeGlobal = System.nanoTime();
		
		System.out.println("Pi : " + pi);
		System.out.println("Time Duration Global: " + (stopTimeGlobal - startTimeGlobal)
				/ 100000 + "ms");
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