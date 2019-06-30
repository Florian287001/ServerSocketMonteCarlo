package fr.Florian287001.serverSocketPI.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {

	// On initialise des valeurs par défaut
	private int port = 2345;
	private String host = "127.0.0.1";
	private ServerSocket server = null;
	private boolean isRunning = true;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	public Server() {
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server(String pHost, int pPort) {
		host = pHost;
		port = pPort;
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// On lance notre serveur
	public void open() {

		// Toujours dans un thread à part vu qu'il est dans une boucle infinie
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (isRunning == true) {

					try {
						// On attend une connexion d'un client
						Socket client = server.accept();

						System.out.println("Connexion cliente reçue.");

						while (!client.isClosed()) {

							try {

								writer = new PrintWriter(client
										.getOutputStream());
								reader = new BufferedInputStream(client
										.getInputStream());

								// On attend la demande du client
								String response = read();
								InetSocketAddress remote = (InetSocketAddress) client
										.getRemoteSocketAddress();

								// On affiche quelques infos, pour le débuggage
								String debug = "";
								debug = "Thread : "
										+ Thread.currentThread().getName()
										+ ". ";
								debug += "Demande de l'adresse : "
										+ remote.getAddress().getHostAddress()
										+ ".";
								debug += " Sur le port : " + remote.getPort()
										+ ".\n";
								debug += "\t -> Commande reçue : " + response
										+ "\n";
								System.err.println("\n" + debug);

								// On traite la demande du client en fonction de
								// la commande
								// envoyée
								String toSend = "";

								String[] s = response.toUpperCase().split(";");

								switch (s[0]) {
								case "PI":

									try {

										// Traitement avec la Methode de
										// Monte-Carlo

										int totalcount = Integer.parseInt(s[1]);
										//float timeMoyExecutionParThrowTot = 0;
										long startTimeThrow = System.nanoTime();
										long circleCount = 0;
										Random prng = new Random();
										for (int j = 0; j < totalcount; j++) {
											//long startTimeThrowBoucle = System.nanoTime();
											double x = prng.nextDouble();
											double y = prng.nextDouble();
											if ((x * x + y * y) < 1)
												++circleCount;
											//long endTimeThrowBoucle = System.nanoTime();
											//float timeMoyExecutionParThrow = (float) (endTimeThrowBoucle - startTimeThrowBoucle) / 100;
										}
										long endTimeThrow = System.nanoTime();
										float timeMoyExecutionParThrow = (float) (endTimeThrow - startTimeThrow) / 1000000;
										System.out.println(timeMoyExecutionParThrow + "ms");
										System.out.println((timeMoyExecutionParThrow*1000)/totalcount + "us");

										// Renvoie du résultat

										toSend = Long.toString(circleCount);

									} catch (Exception e) {
										e.printStackTrace();
									}

									// toSend = "test";
									break;

								case "CLOSE":
									toSend = "Communication terminée";
									break;
								default:
									toSend = "Commande inconnu !";
									break;
								}

								// On envoie la réponse au client
								writer.write(toSend);
								writer.flush();

								System.out
										.println("Connexion cliente envoyer.");
								writer = null;
								reader = null;
								client.close();
							} catch (SocketException e) {
								System.err
										.println("LA CONNEXION A ETE INTERROMPUE ! ");
								break;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
					server = null;
				}
			}
		});

		t.start();
	}

	public void close() {
		isRunning = false;
	}

	// La méthode que nous utilisons pour lire les réponses
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}
}