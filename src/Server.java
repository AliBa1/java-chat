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
				Client client = new Client(clients.size());
				clients.add(client);
				new Thread(() -> recieveMessages(client, clientSocket)).start();
			} catch (IOException e) {
				System.out.print("Error connecting client");
			}
		}
	}

	private static void recieveMessages(Client client, Socket clientSocket) {
		while (true) {
			try (InputStream inputStream = clientSocket.getInputStream()) {
				try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")) {
					String incomingMessage = client.toString();
					incomingMessage += scanner.hasNext() ? scanner.next() : "";
					System.out.print(incomingMessage);
					sendMessage(client.getID(), incomingMessage);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private static void sendMessage(int senderID, String message) {
		for (Client client : clients) {
			if (client.getID() != senderID) {
				try (Socket socket = new Socket(client.clientSocket.getInetAddress(), client.clientSocket.getLocalPort())) {
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataOutputStream.writeByte(1);
				dataOutputStream.writeUTF(message);
				dataOutputStream.flush();
				} catch (IOException e) {
					System.out.println("Couldn't send to client " + client.getID());
					// e.printStackTrace(System.err);
				}
			}
		}
	}
}
