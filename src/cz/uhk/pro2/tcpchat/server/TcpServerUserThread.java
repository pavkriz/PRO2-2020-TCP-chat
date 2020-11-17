package cz.uhk.pro2.tcpchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Thread that handles a single connected client
 */
public class TcpServerUserThread extends Thread implements TcpServerUserThreadObserver {
    private final Socket connectedClientSocket;
    private final MessageBroacaster broacaster;
    private final List<TcpServerUserThreadListener> listeners;

    public TcpServerUserThread(Socket connectedClientSocket, MessageBroacaster broadcaster) {
        this.connectedClientSocket = connectedClientSocket;
        this.broacaster = broadcaster;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            InputStream is = connectedClientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("New message received: " + message + " " + connectedClientSocket);
                if (!processCommand(message)) {
                    broacaster.broadcastMessage(message, connectedClientSocket);
                }
                if (connectedClientSocket.isClosed()) {
                    break;
                }
            }
            System.out.println("UserThread ended " + connectedClientSocket);
            listeners.forEach(TcpServerUserThreadListener::notifyListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addListener(TcpServerUserThreadListener listener) {
        listeners.add(listener);
    }

    private boolean processCommand(String command) throws IOException {
        switch (command) {
            case "/time":
                sendResponseToSender(String.format("Current time is: %s", new Date()));
                return true;
            case "/quit":
                connectedClientSocket.close();
                this.interrupt();
                return true;
            default:
                return false;
        }
    }

    private void sendResponseToSender(String message) throws IOException {
        OutputStream os = connectedClientSocket.getOutputStream();
        PrintWriter w = new PrintWriter(new OutputStreamWriter(os), true);
        w.println(message);
    }

}
