package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT = 55556;
	private static int id;
	private static Socket socket;
	public ServerSocket clientSocket;

	public Client(int setID) {
		id = setID;
		try (ServerSocket newServerSocket = new ServerSocket()) {
			clientSocket = newServerSocket;
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.out.println("Problem @ 22");
			System.exit(0);
		}
	}

	public int getID() {
		return id;
	}

	public static void main(String[] args) {
		// try (Socket socket = new Socket("localhost", SERVER_PORT)) {
		// System.out.println("Client " + id + " has joined the server");
		// } catch (IOException e) {
		// e.printStackTrace(System.err);
		// }
		connectToServer();

		// new Thread(() -> sendMessages()).start();
		// new Thread(() -> readMessages()).start();
	}

	private static void connectToServer() {
		try {
			socket = new Socket("localhost", SERVER_PORT);
			if (socket.isConnected()) {
				System.out.println("You are connected to the server");
			} else {	
				System.out.println("Something went wrong connecting you to the server");
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private static void sendMessages() {
		while (true) {
			System.out.print("> ");
			try (Scanner scanner = new Scanner(System.in)) {
				String clientMessage = scanner.nextLine();
				DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataOutputStream.writeByte(1);
				dataOutputStream.writeUTF(clientMessage);
				dataOutputStream.flush();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.exit(0);
			}
		}
	}

	private static void readMessages() {
		while (true) {
			try (InputStream inputStream = socket.getInputStream()) {
				try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")) {
					String incomingMessage = scanner.hasNext() ? scanner.next() : "";
					System.out.println(incomingMessage);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.out.println("Problem @ 78");
				System.exit(0);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Client %d: ", id);
	}
}
