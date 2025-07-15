package src;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT = 55556;
	private static int id;
	private static Socket socket;
	private final InetAddress address;
	private final int port;

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public Client(int setID, InetAddress address, int port) {
		id = setID;
		this.address = address;
		this.port = port;
	}

	public int getID() {
		return id;
	}

	public static void main(String[] args) {
		connectToServer();

		new Thread(() -> sendMessages()).start();
		// new Thread(() -> readMessages()).start();
	}

	private static void connectToServer() {
		try {
			socket = new Socket("localhost", SERVER_PORT);
			// if (socket.isConnected()) {
			// 	System.out.println("You are connected to the server");
			// } else {
			// 	System.out.println("Something went wrong connecting you to the server");
			// }
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	private static void sendMessages() {
		try (
				Scanner scanner = new Scanner(System.in);
				DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
			while (true) {
				System.out.print("> ");
				String clientMessage = scanner.nextLine();
				dataOutputStream.writeUTF(clientMessage);
				// connectToServer();
				dataOutputStream.flush();
				// connectToServer();
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(0);
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
				System.out.println("Problem @ Read Msg Input Stream");
				System.exit(0);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Client %d: ", id);
	}
}
