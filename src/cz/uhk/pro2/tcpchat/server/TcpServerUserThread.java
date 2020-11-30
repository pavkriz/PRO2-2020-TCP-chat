package cz.uhk.pro2.tcpchat.server;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TcpServerUserThread extends Thread {
    private final Socket connectedClient;
    private final MessageBroadcaster tcpChatServer;
    public TcpServerUserThread(Socket connectedClient, MessageBroadcaster tcpChatServer) {
        this.connectedClient = connectedClient;
        this.tcpChatServer = tcpChatServer;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = connectedClient.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String message;
            while (!connectedClient.isClosed() && (message = reader.readLine()) != null) {
                System.out.println("New message: " + message + " " + connectedClient);
                switch (message.toLowerCase()) {
                    case "/time" -> writeMessage(connectedClient, "Actual time: " + (new Date()).toString());
                    case "/quit" -> {
                        writeMessage(connectedClient, "Logging out...");
                        connectedClient.close();
                    }
                    default -> tcpChatServer.broadcastMessage(connectedClient, message);
                }
            }
            System.out.println("User " + connectedClient + " has been disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMessage(Socket socket, String message) {
        try {
            OutputStream os = socket.getOutputStream();
            PrintWriter w = new PrintWriter(new OutputStreamWriter(os), true);
            w.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
