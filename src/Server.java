package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private static final int PORT = 55556;
	private static final ArrayList<Client> clients = new ArrayList<>();
	private static ServerSocket serverSocket;

	public static void main(String[] args) {
		try (ServerSocket createdServerSocket = new ServerSocket(PORT)) {
			serverSocket = createdServerSocket;
			System.out.println("The server is running at localhost:" + PORT);
			acceptConnections();
			// new Thread(() -> acceptConnections()).start();
		} catch (IOException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace(System.err);
		}
	}

	public static void acceptConnections() {
		while (true) {
			try (Socket clientSocket = serverSocket.accept()) {
				System.out.println("A client has connected");
				Client client = new Client(clients.size(), clientSocket.getInetAddress(), clientSocket.getLocalPort());
				clients.add(client);
				new Thread(() -> recieveMessages(client)).start();
				// new Thread(() -> recieveMessages(client, clientSocket)).start();
			} catch (IOException e) {
				System.out.print("Error connecting client");
			}
		}
	}

	private static void recieveMessages(Client client) {
		try (
				Socket clientSocket = new Socket(client.getAddress(), client.getPort());
				InputStream inputStream = clientSocket.getInputStream();
				Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");) {
			while (scanner.hasNext()) {
				String incomingMessage = client.toString() + scanner.next();
				System.out.println(incomingMessage);
				sendMessage(client.getID(), incomingMessage);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
	}

	private static void sendMessage(int senderID, String message) {
		for (Client client : clients) {
			if (client.getID() != senderID) {
				try (
						Socket clientSocket = new Socket(client.getAddress(), client.getPort());) {
					DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
					dataOutputStream.writeUTF(message);
					dataOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}
}
